package com.netthreads.network.osc.router.properties;

import com.google.inject.Singleton;

/**
 * This is a work in progress.
 * 
 *
 */
@Singleton
public class ApplicationProperties
{
	public static final String SAMPLE_SETTINGS = "/messages.xml";
	
	public static final int DEFAULT_PORT = 9000;
	public static final long DEFAULT_REFRESH_MSEC = 1500;
	public static final boolean DEFAULT_LOAD_SAMPLE_FILE = true;
	
	private int port;;
	private long refreshMSec;
	private boolean loadSampleFile;
	
	/**
	 * Initialise default properties.
	 */
	public ApplicationProperties()
	{
		port = DEFAULT_PORT;
		refreshMSec = DEFAULT_REFRESH_MSEC;
		loadSampleFile = DEFAULT_LOAD_SAMPLE_FILE;
	}
	
	/**
	 * Load properties.
	 * 
	 */
	public boolean load()
	{
		boolean status = true;
		
		// TODO Implement load from disk.
		
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
	
	public long getRefreshMsec()
	{
		return refreshMSec;
	}
	
	public boolean isLoadSampleFile()
	{
		return loadSampleFile;
	}
	
	public void setLoadSampleFile(boolean loadSampleFile)
	{
		this.loadSampleFile = loadSampleFile;
	}
}
