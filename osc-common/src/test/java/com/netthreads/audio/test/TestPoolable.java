package com.netthreads.audio.test;

import org.junit.Test;

import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;

public class TestPoolable
{
	/**
	 * Test expected pool contents.
	 * 
	 */
	@Test
	public void testOSCMessagePool()
	{
		for (int i = 0; i < OSCMessage.POOL_INITIAL_CAPACITY; i++)
		{
			OSCMessage oscMessage = OSCMessage.$(String.valueOf(i));

			oscMessage.free();

			int size = OSCMessage.getPool().size();

			junit.framework.Assert.assertTrue(size == 1);
		}

		OSCMessage oscMessageA = OSCMessage.$(String.valueOf(OSCMessage.POOL_INITIAL_CAPACITY));

		OSCMessage oscMessageB = OSCMessage.$(String.valueOf(OSCMessage.POOL_INITIAL_CAPACITY));

		oscMessageA.free();
		oscMessageB.free();
		
		int size = OSCMessage.getPool().size();

		junit.framework.Assert.assertTrue(size == 2);
	}
	
	/**
	 * Test expected pool contents.
	 * 
	 */
	@Test
	public void testOSCBundlePool()
	{
		for (int i = 0; i < OSCBundle.POOL_INITIAL_CAPACITY; i++)
		{
			OSCBundle oscBundle = OSCBundle.$();

			for (int j = 0; j < 4; j++)
			{
				OSCMessage oscMessage = OSCMessage.$(String.valueOf(j));

				oscBundle.addMessage(oscMessage);
			}
			
			oscBundle.free();

			int oscBundlePoolSize = OSCBundle.getPool().size();

			junit.framework.Assert.assertTrue(oscBundlePoolSize == 1);
			
			int oscMessagePoolSize = OSCMessage.getPool().size();

			junit.framework.Assert.assertTrue(oscMessagePoolSize == 4);
			
		}

	}
	
}
