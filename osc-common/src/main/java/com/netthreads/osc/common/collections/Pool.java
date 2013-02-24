package com.netthreads.osc.common.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Object pool.
 *
 * @param <T>
 */
abstract public class Pool<T>
{
	private List<T> queue;

	/**
	 * Construct pool.
	 * 
	 */
	public Pool(int initialCapacity)
	{
		queue = new ArrayList<T>(initialCapacity);
	}

	/**
	 * Implement in sub-classes.
	 * 
	 * @return Instance of T.
	 */
	abstract protected T newObject();

	/**
	 * Return an instance of pooled object.
	 * 
	 * @return instance of pooled object.
	 */
	public T obtain()
	{
		T object = null;

		if (queue.isEmpty())
		{
			object = newObject();
		}
		else
		{
			object = queue.remove(0);
		}

		return object;
	}
	
	/**
	 * Return object to the pool.
	 * 
	 * @param object
	 */
	public void free(T object)
	{
		queue.add(object);
	}

	/**
	 * Return current queue size.
	 * 
	 * @return The current queue size.
	 */
	public int size()
	{
		return queue.size();
	}
}
