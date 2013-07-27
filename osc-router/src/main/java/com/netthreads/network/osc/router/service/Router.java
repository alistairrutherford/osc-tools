package com.netthreads.network.osc.router.service;

import com.netthreads.network.osc.router.model.OSCItem;

/**
 * OSC Message routers implement this interface.
 * 
 */
public interface Router
{
	public boolean route(OSCItem oscItem);
	
	public void start();
	
	public void stop();
}
