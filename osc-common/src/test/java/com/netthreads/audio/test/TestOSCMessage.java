package com.netthreads.audio.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.junit.Test;

import com.netthreads.osc.common.domain.OSCEncoder;
import com.netthreads.osc.common.domain.OSCMessage;

public class TestOSCMessage
{
	
	private String MESSAGE1_ADDRESS = "/oscillator/4/frequency";
	private byte[] MESSAGE1 =
	{
	        (byte) 0x2f, (byte) 0x6f, (byte) 0x73, (byte) 0x63, (byte) 0x69, (byte) 0x6c, (byte) 0x6c, (byte) 0x61, (byte) 0x74, (byte) 0x6f, (byte) 0x72, (byte) 0x2f, (byte) 0x34, (byte) 0x2f, (byte) 0x66, (byte) 0x72, (byte) 0x65, (byte) 0x71, (byte) 0x75, (byte) 0x65, (byte) 0x6e, (byte) 0x63, (byte) 0x79, (byte) 0x0, (byte) 0x2c, (byte) 0x66, (byte) 0x0, (byte) 0x0, (byte) 0x43, (byte) 0xdc, (byte) 0x0, (byte) 0x0
	};
	
	private String MESSAGE2_ADDRESS = "/foo";
	private byte[] MESSAGE2 =
	{
	        (byte) 0x2f, (byte) 0x66, (byte) 0x6f, (byte) 0x6f, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x2c, (byte) 0x69, (byte) 0x69, (byte) 0x73, (byte) 0x66, (byte) 0x66, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x3, (byte) 0xe8, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x68, (byte) 0x65, (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x3f, (byte) 0x9d, (byte) 0xf3, (byte) 0xb6, (byte) 0x40, (byte) 0xb5, (byte) 0xb2,
	        (byte) 0x2d
	};
	
	private String MESSAGE3_ADDRESS = "/instrumenta/noteon";
	private byte[] MESSAGE3 =
	{
	        (byte) 0x2f, /* [/] - 47 */
	        (byte) 0x69, /* [i] - 105 */
	        (byte) 0x6e, /* [n] - 110 */
	        (byte) 0x73, /* [s] - 115 */
	        (byte) 0x74, /* [t] - 116 */
	        (byte) 0x72, /* [r] - 114 */
	        (byte) 0x75, /* [u] - 117 */
	        (byte) 0x6d, /* [m] - 109 */
	        (byte) 0x65, /* [e] - 101 */
	        (byte) 0x6e, /* [n] - 110 */
	        (byte) 0x74, /* [t] - 116 */
	        (byte) 0x61, /* [a] - 97 */
	        (byte) 0x2f, /* [/] - 47 */
	        (byte) 0x6e, /* [n] - 110 */
	        (byte) 0x6f, /* [o] - 111 */
	        (byte) 0x74, /* [t] - 116 */
	        (byte) 0x65, /* [e] - 101 */
	        (byte) 0x6f, /* [o] - 111 */
	        (byte) 0x6e, /* [n] - 110 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x2c, /* [,] - 44 */
	        (byte) 0x69, /* [i] - 105 */
	        (byte) 0x69, /* [i] - 105 */
	        (byte) 0x69, /* [i] - 105 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x40, /* [@] - 64 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x20, /* [ ] - 32 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x0, /* [^@ (NUL)] - 0 */
	        (byte) 0x10, /* [^P] - 16 */
	};
	
	/**
	 * Test message.
	 */
	@Test
	public void test1()
	{
		// Test arguments
		float value1 = 440.0f;
		
		// Build message
		OSCMessage oscMessage = OSCMessage.$(MESSAGE1_ADDRESS);
		
		oscMessage.addArgument(value1);
		
		// Stream to buffer.
		ByteBuf buffer = Unpooled.buffer(MESSAGE1.length);
		
		OSCEncoder encoder = new OSCEncoder();
		
		encoder.encode(oscMessage, buffer);
		
		// Test buffer matches expected output.
		byte[] outStream = buffer.array();
		
		boolean match = TestHelper.compareBuffer(MESSAGE1, outStream);
		
		junit.framework.Assert.assertTrue(match);
	}
	
	/**
	 * Test message.
	 */
	@Test
	public void test2()
	{
		// Test arguments
		int value1 = 1000;
		int value2 = -1;
		String value3 = "hello";
		float value4 = 1.234f;
		float value5 = 5.678f;
		
		// Build message
		OSCMessage oscMessage = OSCMessage.$(MESSAGE2_ADDRESS);
		
		oscMessage.addArgument(value1);
		oscMessage.addArgument(value2);
		oscMessage.addArgument(value3);
		oscMessage.addArgument(value4);
		oscMessage.addArgument(value5);
		
		// Stream to buffer.
		ByteBuf buffer = Unpooled.buffer(MESSAGE2.length);
		
		OSCEncoder encoder = new OSCEncoder();
		
		encoder.encode(oscMessage, buffer);
		
		// Test buffer matches expected output.
		byte[] outStream = buffer.array();
		
		boolean match = TestHelper.compareBuffer(MESSAGE2, outStream);
		
		junit.framework.Assert.assertTrue(match);
	}
	
	/**
	 * Test message.
	 * 
	 */
	@Test
	public void test3()
	{
		// Test arguments
		int value1 = 64;
		int value2 = 32;
		int value3 = 16;
		
		// Build message
		OSCMessage oscMessage = OSCMessage.$(MESSAGE3_ADDRESS);
		
		oscMessage.addArgument(value1); // channel
		oscMessage.addArgument(value2); // note
		oscMessage.addArgument(value3); // velocity
		
		// Stream to buffer.
		ByteBuf buffer = Unpooled.buffer(MESSAGE3.length);
		
		OSCEncoder encoder = new OSCEncoder();
		
		encoder.encode(oscMessage, buffer);
		
		// Test buffer matches expected output.
		byte[] outStream = buffer.array();
		
		boolean match = TestHelper.compareBuffer(MESSAGE3, outStream);
		
		junit.framework.Assert.assertTrue(match);
	}
	
}
