package com.netthreads.network.osc.server;

import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;

/**
 * Server callback handlers.
 * 
 */
public interface OSCServerListener
{
	/**
	 * Handle server started.
	 * 
	 */
	public void handleStart();
	
	/**
	 * Handle server shutdown.
	 */
	public void handleShutdown();
	
	/**
	 * Handle OSC Message.
	 * 
	 * @param oscMessage
	 */
	public void handleOSCMessage(OSCMessage oscMessage);
	
	/**
	 * Handle OSC Bundle message.
	 * 
	 * @param oscBundle
	 */
	public void handleOSCBundle(OSCBundle oscBundle);
}
