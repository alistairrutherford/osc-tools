package com.netthreads.network.osc.client;

import com.google.inject.ImplementedBy;
import com.netthreads.osc.common.domain.OSCBundle;

@ImplementedBy(OSCClientImpl.class)
public interface OSCClient
{
	/**
	 * Connect to OSC end-point.
	 * 
	 * @param host
	 * @param port
	 * 
	 * @return True if successful.
	 */
	public boolean connect(String host, int port);

	/**
	 * Disconnect from end-point.
	 */
	public void disconnect();

	/**
	 * Send an OSC bundle to connected end-point.
	 * 
	 * @param oscBundle
	 */
	public void send(OSCBundle oscBundle);
	
	/**
	 * Return connection status.
	 * 
	 * @return connection status.
	 */
	public boolean isConnected();
}
