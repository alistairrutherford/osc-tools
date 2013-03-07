package com.netthreads.network.osc.router.service;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

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
public class OSCService extends Service<Void> implements OSCServerListener
{
	private Logger logger = LoggerFactory.getLogger(OSCService.class);

	private final ObservableList<OSCItem> observableList;

	private final OSCMessageCache messageCache;

	private final MIDIDeviceCache midiDeviceCache;

	private final ImplementsRefresh refreshView;

	private OSCServer oscServer;

	private ApplicationProperties applicationProperties;

	private int port;

	private MIDIMessageLookup messageLookup;

	private long elapsed;
	private long refreshMsec;

	// ---------------------------------------------------------------
	// Active Property
	// ---------------------------------------------------------------

	private BooleanProperty activeProperty;

	public BooleanProperty getActiveProperty()
	{
		return activeProperty;
	}

	public boolean getActive()
	{
		return activeProperty.get();
	}

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

		// Properties
		activeProperty = new SimpleBooleanProperty();

		// Cache singleton.
		messageCache = AppInjector.getInjector().getInstance(OSCMessageCache.class);

		midiDeviceCache = AppInjector.getInjector().getInstance(MIDIDeviceCache.class);

		// MIDI Message table.
		messageLookup = AppInjector.getInjector().getInstance(MIDIMessageLookup.class);

		applicationProperties = AppInjector.getInjector().getInstance(ApplicationProperties.class);

		elapsed = 0;
	}

	/**
	 * Activate service.
	 * 
	 * @param port
	 * 
	 * @return True if successful.
	 */
	public boolean process(int port)
	{
		boolean status = true;

		setPort(port);

		reset();

		start();

		return status;
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

		updateValues(oscMessage, oscItem);

		routeMessage(oscItem);

		updateView();
	}

	/**
	 * Only update the view if a set amount of msec as passed. This is to stop the UI from getting swamped with refresh
	 * calls.
	 */
	private void updateView()
	{
		elapsed += System.currentTimeMillis();

		if (elapsed > refreshMsec)
		{
			logger.debug("Refresh");

			// Update view.
			//refreshView.refresh();

			elapsed = 0;
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
	 * Route message according to setting.
	 * 
	 * @param oscItem
	 */
	private void routeMessage(OSCItem oscItem)
	{
		// Route with message?
		String routeType = oscItem.getRoute();

		// Route message to device?
		String device = oscItem.getDevice();

		// Fetch device and examine it's status.
		MidiDevice midiDevice = midiDeviceCache.get(device);

		if (midiDevice != null && midiDevice.isOpen())
		{
			int message = messageLookup.getMessage(routeType);

			// Send message.
			switch (message)
			{
			case ShortMessage.CONTINUE:
				// TODO
				break;

			case ShortMessage.NOTE_ON:
				// Parameter check.
				List<OSCValue> noteOnValues = oscItem.getValues();
				if (messageLookup.getParametersCount(routeType) != noteOnValues.size())
				{
					oscItem.setWorking(OSCItem.WORKING_ERROR);
				}
				else
				{
					int noteOn = Integer.valueOf(noteOnValues.get(0).getValue());
					int velocityOn = Integer.valueOf(noteOnValues.get(1).getValue());
					int channelOn = Integer.valueOf(noteOnValues.get(2).getValue());

					sendNote(ShortMessage.NOTE_ON, noteOn, velocityOn, channelOn, midiDevice);

					// Set status.
					oscItem.setWorking(OSCItem.WORKING_DONE);
				}
				break;

			case ShortMessage.NOTE_OFF:
				// Parameter check.
				List<OSCValue> noteOffValues = oscItem.getValues();
				if (messageLookup.getParametersCount(routeType) != noteOffValues.size())
				{
					oscItem.setWorking(OSCItem.WORKING_ERROR);
				}
				else
				{
					int noteOff = Integer.valueOf(noteOffValues.get(0).getValue());
					int velocityOff = Integer.valueOf(noteOffValues.get(1).getValue());
					int channelOff = Integer.valueOf(noteOffValues.get(2).getValue());

					sendNote(ShortMessage.NOTE_OFF, noteOff, velocityOff, channelOff, midiDevice);

					// Set status.
					oscItem.setWorking(OSCItem.WORKING_DONE);
				}
				break;

			case ShortMessage.POLY_PRESSURE:
				// TODO
				break;
			case ShortMessage.CHANNEL_PRESSURE:
				// TODO
				break;
			case ShortMessage.PITCH_BEND:
				// TODO
				break;
			case ShortMessage.PROGRAM_CHANGE:
				// TODO
				break;
			case ShortMessage.CONTROL_CHANGE:
				// TODO
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Send MIDI note.
	 * 
	 * @param note
	 * @param velocity
	 * @param channel
	 * @param midiDevice
	 */
	private void sendNote(int command, int note, int velocity, int channel, MidiDevice midiDevice)
	{
		try
		{
			ShortMessage midiMessage = new ShortMessage();

			midiMessage.setMessage(command, channel, note, velocity);

			midiDevice.getReceiver().send(midiMessage, -1);
		}
		catch (InvalidMidiDataException e)
		{
			logger.error(e.getLocalizedMessage());
		}
		catch (MidiUnavailableException e)
		{
			logger.error(e.getLocalizedMessage());
		}
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

		elapsed = 0;

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

		elapsed = 0;

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
