package com.netthreads.osc.test.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.network.osc.server.OSCServer;
import com.netthreads.network.osc.server.OSCServerImpl;
import com.netthreads.network.osc.server.OSCServerListener;
import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;

/**
 * OSC Server task runs server thread.
 * 
 */
public class OSCServerTask implements Callable<Void>, OSCServerListener
{
	private static final Logger logger = LoggerFactory.getLogger(OSCServerTask.class);
	
	private final int port;
	private final int max;
	private OSCServer oscServer;
	
	private List<OSCBundle> bundles;
	
	/**
	 * Construct task.
	 * 
	 * @param oscServer
	 *            Server instance.
	 */
	public OSCServerTask(int port, int max)
	{
		this.port = port;
		this.max = max;
		
		bundles = new ArrayList<OSCBundle>();
	}
	
	/**
	 * Send test messages through OSCClient.
	 * 
	 */
	@Override
	public Void call() throws Exception
	{
		try
		{
			oscServer = new OSCServerImpl(port, this);
			
			logger.info("Starting..");
			
			oscServer.listen();
		}
		finally
		{
			logger.info("Stopping");
		}
		
		logger.info("Finished");
		
		return null;
	}
	
	/**
	 * Return server object.
	 * 
	 * @return The server object.
	 */
	public OSCServer getOscServer()
	{
		return oscServer;
	}
	
	@Override
	public void handleStart()
	{
		System.out.print("Starting");
	}
	
	@Override
	public void handleShutdown()
	{
		System.out.print("Shutdown");
	}
	
	@Override
	public void handleOSCMessage(OSCMessage oscMessage)
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void handleOSCBundle(OSCBundle oscBundle)
	{
		bundles.add(oscBundle);
		
		if (bundles.size() >= max)
		{
			oscServer.shutdown();
		}
	}
	
	public List<OSCBundle> getBundles()
	{
		return bundles;
	}
	
}
