package com.netthreads.network.osc.router.service;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Collection;

import com.google.inject.ImplementedBy;
import com.netthreads.network.osc.router.model.OSCItem;

/**
 * Result cache.
 * 
 */
@ImplementedBy(OSCMessageCacheImpl.class)
public interface OSCMessageCache extends Serializable
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
	 * Return current items.
	 * 
	 * @return The items.
	 */
	public Collection<OSCItem> items();
	
	/**
	 * Clear cache.
	 */
	public void clear();
	
	/**
	 * Encode the contents.
	 * 
	 * @param filePath
	 * 
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public void serialize(String filePath) throws Exception;
	
	/**
	 * Load the contents.
	 * 
	 * @param filePath
	 * 
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public void deserialize(String filePath) throws Exception;
	
}
