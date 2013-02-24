package com.netthreads.osc.common.domain;

import java.util.ArrayList;
import java.util.List;

import com.netthreads.osc.common.collections.Pool;
import com.netthreads.osc.common.collections.ResettingPool;

/**
 * An OSC Bundle consists of the OSC-string "#bundle" followed by an OSC Time
 * Tag, followed by zero or more OSC Bundle Elements. The OSC-timetag is a
 * 64-bit fixed point time tag.
 * 
 */
public class OSCBundle implements OSCPacket
{
	public static final int POOL_INITIAL_CAPACITY = 32;
	
	private long timeTag;
	private List<OSCPacket> messages;
	
	// Message Pool.
	private static final Pool<OSCBundle> pool = new ResettingPool<OSCBundle>(POOL_INITIAL_CAPACITY)
	{
		@Override
		protected OSCBundle newObject()
		{
			return new OSCBundle();
		}
	};
	
	/**
	 * Get instance.
	 * 
	 * @return instance;
	 */
	public static OSCBundle $()
	{
		OSCBundle bundle = pool.obtain();
		
		return bundle;
	}
	
	/**
	 * Construct bundle.
	 * 
	 */
	private OSCBundle()
	{
		timeTag = 0;
		messages = new ArrayList<OSCPacket>();
	}
	
	/**
	 * Time tag as milliseconds in normal java time.
	 * 
	 * @return milliseconds in normal java time.
	 */
	public long getTimeTag()
	{
		return timeTag;
	}
	
	/**
	 * Set time tag as milliseconds in normal java time.
	 * 
	 * @param timeTag
	 */
	public void setTimeTag(long timeTag)
	{
		this.timeTag = timeTag;
	}
	
	/**
	 * Add message to bundle.
	 * 
	 * @param message
	 */
	public void addMessage(OSCMessage message)
	{
		messages.add(message);
	}
	
	/**
	 * Return list of associated messages.
	 * 
	 * @return
	 */
	public List<OSCPacket> getMessages()
	{
		return messages;
	}
	
	/**
	 * Reset attributes.
	 * 
	 */
	@Override
	public void reset()
	{
		timeTag = 0;
		
		for (OSCPacket message : messages)
		{
			message.reset();
		}
		
		messages.clear();
	}

	@Override
	public void free()
	{
		for (OSCPacket message : messages)
		{
			message.free();
		}
		
		pool.free(this);
	}
	
	/**
	 * For debug only.
	 */
	@Override
	public String toString()
	{
		String message = timeTag+": ";
		
		int size = messages.size();
		for (int i = 0; i < size; i++)
		{
			message += messages.get(i) + ",";
		}
		
		return message;
	}
	
	/**
	 * For test only.
	 * 
	 * @return The pool.
	 */
	public static Pool<OSCBundle> getPool()
	{
		return pool;
	}
}
