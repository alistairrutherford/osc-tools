package com.netthreads.network.osc.router;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Central injector which holds our singletons.
 * 
 */
public class AppInjector
{
	/**
	 * Guice injector.
	 * 
	 */
	public static Injector injector = null;
	
	public static synchronized Injector getInjector()
	{
		if (injector == null)
		{
			injector = Guice.createInjector();
		}
		
		return injector;
	}
}
