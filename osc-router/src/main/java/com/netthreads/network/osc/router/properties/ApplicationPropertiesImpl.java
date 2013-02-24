package com.netthreads.network.osc.router.properties;

import com.google.inject.Singleton;

@Singleton
public class ApplicationPropertiesImpl implements ApplicationProperties
{
	private int port;;
	
	public ApplicationPropertiesImpl()
	{
		port = DEFAULT_PORT;
	}

	/**
	 * Load properties.
	 * 
	 */
	public boolean load()
	{
		boolean status = true;
		
		// TODO Implement laod from disk.
		
		return status;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
}
