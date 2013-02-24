package com.netthreads.audio.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.junit.Test;

import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCEncoder;
import com.netthreads.osc.common.domain.OSCMessage;

/**
 * Test bundle.
 * 
 */
public class TestOSCBundle
{
	private String MESSAGE1_ADDRESS = "/synthA/noteOn";
	private byte[] MESSAGE_A =
	{
			(byte) 0x23, /* [#] */
			(byte) 0x62, /* [b] */
			(byte) 0x75, /* [u] */
			(byte) 0x6e, /* [n] */
			(byte) 0x64, /* [d] */
			(byte) 0x6c, /* [l] */
			(byte) 0x65, /* [e] */
			(byte) 0x0, /* [^@ (NUL)] */

			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x2, /* [^B] */
			(byte) 0x2, /* [^B] */
			(byte) 0x51, /* [Q] */
			(byte) 254, /* [0xfe] [þ] */
			(byte) 0x24, /* [$] */
			(byte) 0x0, /* [^@ (NUL)] */

			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x18, /* [^X] */

			(byte) 0x2f, /* [/] */
			(byte) 0x73, /* [s] */
			(byte) 0x79, /* [y] */
			(byte) 0x6e, /* [n] */
			(byte) 0x74, /* [t] */
			(byte) 0x68, /* [h] */
			(byte) 0x41, /* [A] */
			(byte) 0x2f, /* [/] */
			(byte) 0x6e, /* [n] */
			(byte) 0x6f, /* [o] */
			(byte) 0x74, /* [t] */
			(byte) 0x65, /* [e] */
			(byte) 0x4f, /* [O] */
			(byte) 0x6e, /* [n] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x2c, /* [,] */
			(byte) 0x69, /* [i] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x1, /* [^A] */

			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x18, /* [^X] */

			(byte) 0x2f, /* [/] */
			(byte) 0x73, /* [s] */
			(byte) 0x79, /* [y] */
			(byte) 0x6e, /* [n] */
			(byte) 0x74, /* [t] */
			(byte) 0x68, /* [h] */
			(byte) 0x41, /* [A] */
			(byte) 0x2f, /* [/] */
			(byte) 0x6e, /* [n] */
			(byte) 0x6f, /* [o] */
			(byte) 0x74, /* [t] */
			(byte) 0x65, /* [e] */
			(byte) 0x4f, /* [O] */
			(byte) 0x6e, /* [n] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x2c, /* [,] */
			(byte) 0x69, /* [i] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x0, /* [^@ (NUL)] */
			(byte) 0x2
	/* [^B] */

	};

	/**
	 * Test message.
	 */
	@Test
	public void test1()
	{
		// Test arguments
		int value1 = 1;
		int value2 = 2;

		// Build message
		OSCBundle oscBundle = OSCBundle.$();
		oscBundle.setTimeTag(0);

		// Message
		OSCMessage oscMessage = OSCMessage.$(MESSAGE1_ADDRESS);
		oscMessage.addArgument(value1);

		oscBundle.addMessage(oscMessage);

		// Message
		oscMessage = OSCMessage.$(MESSAGE1_ADDRESS);
		oscMessage.addArgument(value2);

		oscBundle.addMessage(oscMessage);

		// Stream to buffer.
		ByteBuf buffer = Unpooled.buffer(MESSAGE_A.length);

		OSCEncoder encoder = new OSCEncoder();

		encoder.encode(oscBundle, buffer);

		// Test buffer matches expected output.
		byte[] outStream = buffer.array();

		boolean match = TestHelper.compareBuffer(MESSAGE_A, outStream);

		junit.framework.Assert.assertTrue(match);
	}

}
