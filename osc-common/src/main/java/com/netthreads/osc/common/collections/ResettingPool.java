package com.netthreads.osc.common.collections;

/**
 * Pool with reset on obtain.
 * 
 * @param <T>
 */
public abstract class ResettingPool<T extends Resettable> extends Pool<T>
{
	/**
	 * Construct Pool.
	 * 
	 * @param initialCapacity
	 */
	public ResettingPool(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	/**
	 * Return instance with reset.
	 * 
	 */
	@Override
	public T obtain()
	{
		T item = super.obtain();

		item.reset();

		return item;
	}

}
