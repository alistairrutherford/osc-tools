package com.netthreads.network.osc.router.service;

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
import com.thoughtworks.xstream.XStream;

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
	
	/**
	 * Clear cache.
	 * 
	 */
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
		
		XStream xstream = new XStream();
		xstream.toXML(map, fileOutputStream);
	}
	
	/**
	 * Decode contents.
	 * 
	 * @param filePath
	 *            The file path.
	 * @throws Exception
	 * 
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings({
            "unchecked", "resource"
    })
	public void deserialize(String filePath) throws Exception
	{
		InputStream fileOutputStream = null;
		
		try
		{
			fileOutputStream = new FileInputStream(new File(filePath));
		}
		catch (FileNotFoundException e)
		{
			// Try to get from classpath
			fileOutputStream = this.getClass().getResourceAsStream(filePath);
			
			if (fileOutputStream == null)
			{
				throw new Exception(e.getLocalizedMessage());
			}
			
		}
		
		XStream xstream = new XStream();
		
		map = (Map<String, OSCItem>) xstream.fromXML(fileOutputStream);
		
		fileOutputStream.close();
	}
	
}
