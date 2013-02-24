package com.netthreads.audio.test;

import org.junit.Test;

public class TestHelper
{
	@Test
	public void dummyTest()
	{
		junit.framework.Assert.assertTrue(true);
	}
	
	/**
	 * Compare byte buffers.
	 * 
	 * @param bufferA
	 * @param bufferB
	 * 
	 * @return true if buffers are the same.
	 */
	public static boolean compareBuffer(byte[] bufferA, byte[] bufferB)
	{
		boolean status = false;

		if (bufferA.length == bufferB.length)
		{
			boolean match = true;
			int index = bufferA.length - 1;
			while (index >= 0 && match)
			{
				match = bufferA[index] == bufferB[index];
				index--;
			}

			status = match;
		}

		return status;
	}

}
