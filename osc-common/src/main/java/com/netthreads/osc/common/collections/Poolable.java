package com.netthreads.osc.common.collections;

/**
 * Required to implement pool and message resetting handling.
 * 
 */
public interface Poolable
{
	public void free();
}
