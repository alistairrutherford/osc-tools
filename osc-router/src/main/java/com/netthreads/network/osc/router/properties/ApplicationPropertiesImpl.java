package com.netthreads.network.osc.router.properties;

import com.google.inject.Singleton;

@Singleton
public class ApplicationPropertiesImpl implements ApplicationProperties
{
	private int port;;
	private long refreshMSec;

	/**
	 * Initialise default properties.
	 */
	public ApplicationPropertiesImpl()
	{
		port = DEFAULT_PORT;
		refreshMSec = DEFAULT_REFRESH_MSEC;
	}

	/**
	 * Load properties.
	 * 
	 */
	@Override
	public boolean load()
	{
		boolean status = true;

		// TODO Implement load from disk.

		return status;
	}

	@Override
	public int getPort()
	{
		return port;
	}

	@Override
	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public long getRefreshMsec()
	{
		return refreshMSec;
	}
}
