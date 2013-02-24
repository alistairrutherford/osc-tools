package com.netthreads.osc.common.domain;

import java.util.ArrayList;
import java.util.List;

import com.netthreads.osc.common.collections.Pool;
import com.netthreads.osc.common.collections.ResettingPool;

/**
 * An OSC message consists of an OSC Address Pattern followed by an OSC Type Tag
 * String followed by zero or more OSC Arguments.
 * 
 */
public class OSCMessage implements OSCPacket
{
	public static final int POOL_INITIAL_CAPACITY = 32;
	
	private String address;
	private List<Character> types;
	private List<Object> arguments;
	
	// Message Pool.
	private static final Pool<OSCMessage> pool = new ResettingPool<OSCMessage>(POOL_INITIAL_CAPACITY)
	{
		@Override
		protected OSCMessage newObject()
		{
			return new OSCMessage();
		}
	};
	
	/**
	 * Get instance of.
	 * 
	 */
	public static OSCMessage $(String address)
	{
		OSCMessage message = pool.obtain();
		
		message.setAddress(address);
		
		return message;
	}
	
	private OSCMessage()
	{
		types = new ArrayList<Character>();
		arguments = new ArrayList<Object>();
	}
	
	/**
	 * Reset attributes.
	 * 
	 */
	@Override
	public void reset()
	{
		types.clear();
		arguments.clear();
		address = "";
	}
	
	/**
	 * Place back into pool.
	 * 
	 */
	@Override
	public void free()
	{
		pool.free(this);
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public void addArgument(Object argument)
	{
		arguments.add(argument);
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public List<Object> getArguments()
	{
		return arguments;
	}
	
	public List<Character> getTypes()
	{
		return types;
	}
	
	/**
	 * For debug only.
	 */
	@Override
	public String toString()
	{
		String message = address;
		
		int size = arguments.size();
		for (int i = 0; i < size; i++)
		{
			message += arguments.get(i) + ",";
		}
		
		return message;
	}
	
	/**
	 * For test only.
	 * 
	 * @return The associated pool.
	 */
	public static Pool<OSCMessage> getPool()
	{
		return pool;
	}
}
