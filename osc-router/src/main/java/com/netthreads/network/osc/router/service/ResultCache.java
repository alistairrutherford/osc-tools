package com.netthreads.network.osc.router.service;

import com.google.inject.ImplementedBy;
import com.netthreads.network.osc.router.model.OSCItem;

/**
 * Result cache.
 * 
 */
@ImplementedBy(ResultCacheImpl.class)
public interface ResultCache
{
	/**
	 * Synchronised get from map.
	 * 
	 * @param name
	 * 
	 * @return The object or null if none found.
	 */
	public OSCItem get(String name);
	
	/**
	 * Put in cache.
	 * 
	 * @param name
	 *            The key.
	 * 
	 * @param projectResult
	 *            The value.
	 */
	public void put(String name, OSCItem item);
	
	/**
	 * Clear cache.
	 */
	public void clear();
}
