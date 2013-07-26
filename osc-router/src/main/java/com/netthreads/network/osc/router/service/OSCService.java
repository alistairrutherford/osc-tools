package com.netthreads.network.osc.router.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.network.osc.router.AppInjector;
import com.netthreads.network.osc.router.controller.ImplementsRefresh;
import com.netthreads.network.osc.router.model.OSCItem;
import com.netthreads.network.osc.router.model.OSCValue;
import com.netthreads.network.osc.router.properties.ApplicationProperties;
import com.netthreads.network.osc.server.OSCServer;
import com.netthreads.network.osc.server.OSCServerImpl;
import com.netthreads.network.osc.server.OSCServerListener;
import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;
import com.netthreads.osc.common.domain.OSCPacket;

/**
 * OSC Service.
 * 
 * Present socket through which OSC messages are received and routed.
 * 
 */
public class OSCService extends Service<Void> implements OSCServerListener, RunStop
{
	private Logger logger = LoggerFactory.getLogger(OSCService.class);
	
	private final ObservableList<OSCItem> observableList;
	
	private final OSCMessageCache messageCache;
	
	private final MIDIDeviceCache midiDeviceCache;
	
	private final ImplementsRefresh refreshView;
	
	private OSCServer oscServer;
	
	private final List<OSCRouter> routers;
	
	private final ApplicationProperties applicationProperties;
	
	private int port;
	
	private long elapsedMsec;
	private long refreshMsec;
	
	// ---------------------------------------------------------------
	// Active Property
	// ---------------------------------------------------------------
	
	private BooleanProperty activeProperty;
	
	public BooleanProperty getActiveProperty()
	{
		return activeProperty;
	}
	
	/**
	 * Return service active status.
	 * 
	 * @return The active status.
	 */
	public boolean getActive()
	{
		return activeProperty.get();
	}
	
	/**
	 * Set service active status.
	 * 
	 * @param activeProperty
	 */
	public void setActive(boolean activeProperty)
	{
		this.activeProperty.set(activeProperty);
		
		if (!activeProperty)
		{
			oscServer.shutdown();
		}
	}
	
	/**
	 * OSC Service.
	 * 
	 * @param observableList
	 *            Data source.
	 * @param refreshView
	 *            View to update.
	 */
	public OSCService(ObservableList<OSCItem> observableList, ImplementsRefresh refreshView)
	{
		this.observableList = observableList;
		
		this.refreshView = refreshView;

		routers = new ArrayList<OSCRouter>();
				
		// Properties
		activeProperty = new SimpleBooleanProperty();
		
		// Cache singleton.
		messageCache = AppInjector.getInjector().getInstance(OSCMessageCache.class);
		
		midiDeviceCache = AppInjector.getInjector().getInstance(MIDIDeviceCache.class);
		
		applicationProperties = AppInjector.getInjector().getInstance(ApplicationProperties.class);
		
		elapsedMsec = 0;
		
		port = ApplicationProperties.DEFAULT_PORT;
	}
	
	/**
	 * Add a router to the list.
	 * 
	 * @param oscRouter
	 */
	public void addRouter(OSCRouter oscRouter)
	{
		routers.add(oscRouter);
	}
	
	/**
	 * Activate service.
	 * 
	 * @param port
	 * 
	 * @return True if successful.
	 */
	@Override
	public void run()
	{
		reset();
		
		start();
		
		startRouters();
	}

	/**
	 * Stop service.
	 * 
	 */
	@Override
	public void stop()
	{
	    cancel();
	    
		stopRouters();
	}
	
	/**
	 * Start OSC Routers.
	 * 
	 */
	public void startRouters()
	{
		Iterator<OSCRouter> iterator = routers.iterator();
		boolean done = false;
		while (iterator.hasNext() && !done)
		{
			OSCRouter oscRouter = iterator.next();

			oscRouter.start();
		}
	}

	/**
	 * Stop OSC Routers.
	 * 
	 */
	public void stopRouters()
	{
		Iterator<OSCRouter> iterator = routers.iterator();
		boolean done = false;
		while (iterator.hasNext() && !done)
		{
			OSCRouter oscRouter = iterator.next();

			oscRouter.stop();
		}
	}
	
	/**
	 * Create service task.
	 * 
	 */
	@Override
	protected Task<Void> createTask()
	{
		final OSCService thisService = this;
		
		return new Task<Void>()
		{
			protected Void call()
			{
				try
				{
					oscServer = new OSCServerImpl(port, thisService);
					
					oscServer.listen();
				}
				catch (Exception e)
				{
					logger.error(e.getLocalizedMessage());
				}
				finally
				{
					setActive(false);
				}
				
				return null;
			}
		};
	}
	
	/**
	 * Handle shutdown.
	 * 
	 */
	@Override
	protected void cancelled()
	{
		super.cancelled();
	}
	
	/**
	 * Handle bundle.
	 * 
	 */
	@Override
	public void handleOSCBundle(OSCBundle oscBundle)
	{
		List<OSCPacket> messages = oscBundle.getMessages();
		
		for (OSCPacket oscPacket : messages)
		{
			if (oscPacket instanceof OSCMessage)
			{
				OSCMessage oscMessage = (OSCMessage) oscPacket;
				
				handleOSCMessage(oscMessage);
			}
			else if (oscPacket instanceof OSCBundle)
			{
				handleOSCBundle((OSCBundle) oscPacket);
			}
			
		}
	}
	
	/**
	 * Handle message.
	 * 
	 */
	@Override
	public void handleOSCMessage(OSCMessage oscMessage)
	{
		logger.debug("Received :" + oscMessage.getAddress());
		
		String address = oscMessage.getAddress();
		
		// Look in cache for item.
		OSCItem oscItem = messageCache.get(address);
		
		// If not found then create and entry for message and values.
		if (oscItem == null)
		{
			oscItem = createOSCItem(oscMessage);
		}
		
		// Update item values from message.
		updateValues(oscMessage, oscItem);

		// Send to router
		route(oscItem);
		
		// Update UI.
		updateView();
	}
	
	/**
	 * Route item.
	 * 
	 * TODO: Probably not a very efficient way to do this.
	 * 
	 * @param oscItem
	 */
	private void route(OSCItem oscItem)
	{
		// Pass off routing to provided routers.
		Iterator<OSCRouter> iterator = routers.iterator();
		boolean done = false;
		while (iterator.hasNext() && !done)
		{
			OSCRouter oscRouter = iterator.next();
			done = oscRouter.route(oscItem);
		}
	}
	
	/**
	 * Only update the view if a set amount of msec as passed. This is to stop
	 * the UI from getting swamped with refresh calls.
	 */
	private void updateView()
	{
		elapsedMsec += System.currentTimeMillis();
		
		if (elapsedMsec > refreshMsec)
		{
			logger.debug("Refresh");
			
			// Update view.
			refreshView.refresh();
			
			elapsedMsec = 0;
		}
		
	}
	
	/**
	 * Create OSC Item from message.
	 * 
	 * @param oscMessage
	 * 
	 * @return A new item.
	 */
	private OSCItem createOSCItem(OSCMessage oscMessage)
	{
		String address = oscMessage.getAddress();
		
		OSCItem oscItem = new OSCItem();
		oscItem.setAddress(address);
		oscItem.setRoute(MIDIMessageLookupImpl.NAMES[0]);
		
		observableList.add(oscItem);
		
		messageCache.put(address, oscItem);
		
		List<Character> types = oscMessage.getTypes();
		
		// Set up arguments holder.
		for (int i = 0; i < types.size(); i++)
		{
			OSCValue oscValue = new OSCValue();
			oscValue.setType(types.get(i).toString());
			oscValue.setLabel(null);
			
			oscItem.getValues().add(oscValue);
		}
		
		return oscItem;
	}
	
	/**
	 * Update values.
	 * 
	 * @param oscMessage
	 * @param oscItem
	 */
	private void updateValues(OSCMessage oscMessage, OSCItem oscItem)
	{
		// Fill values for arguments.
		List<Object> arguments = oscMessage.getArguments();
		for (int i = 0; i < arguments.size(); i++)
		{
			OSCValue oscValue = oscItem.getValues().get(i);
			
			// TODO: Not sure about this.
			oscValue.setValue(arguments.get(i).toString());
		}
		
		oscItem.setWorking(OSCItem.WORKING_DONE);
	}
	
	/**
	 * Call back to set state.
	 * 
	 */
	@Override
	public void handleStart()
	{
		setActive(true);
		
		for (OSCItem oscItem : messageCache.items())
		{
			String deviceName = oscItem.getDevice();
			
			oscItem.setStatus(OSCItem.STATUS_CLOSED);
			
			if (midiDeviceCache.openDevice(deviceName))
			{
				oscItem.setStatus(OSCItem.STATUS_OPEN);
			}
		}
		
		refreshView.refresh();
		
		elapsedMsec = 0;
		
		refreshMsec = applicationProperties.getRefreshMsec();
		
		logger.info("Started");
	}
	
	/**
	 * Call back to set state.
	 * 
	 */
	@Override
	public void handleShutdown()
	{
		setActive(false);
		
		for (OSCItem oscItem : messageCache.items())
		{
			String deviceName = oscItem.getDevice();
			
			midiDeviceCache.closeDevice(deviceName);
			
			oscItem.setStatus(OSCItem.STATUS_CLOSED);
			
			oscItem.setWorking(OSCItem.WORKING_DONE);
			
		}
		
		refreshView.refresh();
		
		elapsedMsec = 0;
		
		logger.info("Stopped");
	}
	
	/**
	 * Return current port.
	 * 
	 * @return The port.
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * Set listen port.
	 * 
	 * @param port
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
	
	/**
	 * Load message definitions.
	 * 
	 * @param filePath
	 * 
	 * @throws Exception
	 */
	public void load(String filePath) throws Exception
	{
		try
		{
			messageCache.deserialize(filePath);
			
			// Update view.
			for (OSCItem oscItem : messageCache.items())
			{
				observableList.add(oscItem);
			}
		}
		catch (Exception e)
		{
			throw new Exception(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Save message definitions.
	 * 
	 * @param filePath
	 * 
	 * @throws Exception
	 */
	public void save(String filePath) throws Exception
	{
		messageCache.serialize(filePath);
	}
}
