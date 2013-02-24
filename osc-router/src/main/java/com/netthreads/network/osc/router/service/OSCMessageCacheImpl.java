package com.netthreads.network.osc.router.service;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.netthreads.network.osc.router.model.OSCItem;

/**
 * Result cache.
 *
 */
@Singleton
public class OSCMessageCacheImpl implements OSCMessageCache
{
	private Map<String, OSCItem> map;
	
	/**
	 * Construct object.
	 * 
	 */
	public OSCMessageCacheImpl()
	{
		map = new HashMap<String, OSCItem>();
	}
	
	/**
	 * Synchronised get from map.
	 * 
	 * @param name
	 * 
	 * @return The object or null if none found.
	 */
	@Override
	public synchronized OSCItem get(String name)
	{
		return map.get(name);
	}
	
	/**
	 * Put in cache.
	 * 
	 * @param name
	 *            The key.
	 * 
	 * @param projectResult
	 *            The value.
	 */
	@Override
	public synchronized void put(String name, OSCItem item)
	{
		if (!map.containsKey(name))
		{
			map.put(name, item);
		}
	}

	@Override
	public void clear()
	{
		map.clear();
	}
}
