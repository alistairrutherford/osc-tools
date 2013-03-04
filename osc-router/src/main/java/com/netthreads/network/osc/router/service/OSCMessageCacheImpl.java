package com.netthreads.network.osc.router.service;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.netthreads.network.osc.router.model.OSCItem;

/**
 * Message cache.
 * 
 */
@Singleton
public class OSCMessageCacheImpl implements OSCMessageCache
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Message map.
	 */
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
	
	@Override
	public Collection<OSCItem> items()
	{
		return map.values();
	}
	
	/**
	 * Encode contents.
	 * 
	 * @param filePath
	 *            The file path.
	 * 
	 * @throws FileNotFoundException
	 */
	public void serialize(String filePath) throws Exception
	{
		FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
		
		XMLEncoder encoder = new XMLEncoder(fileOutputStream);
		
		encoder.writeObject(map);
		
		encoder.close();
	}
	
	/**
	 * Decode contents.
	 * 
	 * @param filePath
	 *            The file path.
	 * 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
    public void deserialize(String filePath) throws Exception
	{
		InputStream fileOutputStream = new FileInputStream(new File(filePath));
		
		XMLDecoder decoder = new XMLDecoder(fileOutputStream);
		
		Map<String, OSCItem> cacheImpl = (Map<String, OSCItem>) decoder.readObject();
		
		map.putAll(cacheImpl);
		
		decoder.close();
	}
	
}
