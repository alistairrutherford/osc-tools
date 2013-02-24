package com.netthreads.osc.common.domain;

import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * String cache to encode all strings into ByteBuffers.
 * 
 */
public class OSCString
{
	private static final Logger logger = LoggerFactory.getLogger(OSCString.class);

	private static final String CHAR_ENCODE = "US-ASCII";

	public Charset charset = Charset.forName(CHAR_ENCODE);
	public CharsetEncoder encoder = charset.newEncoder();

	private Map<String, byte[]> strings;

	private static OSCString instance = null;

	/**
	 * Return singleton instance.
	 * 
	 * @return the singleton instance.
	 */
	public static synchronized OSCString $()
	{
		if (instance == null)
		{

			instance = new OSCString();
		}

		return instance;
	}

	public OSCString()
	{
		strings = new HashMap<String, byte[]>();

		initialise();
	}

	/**
	 * Encode built in OSC strings.
	 * 
	 */
	private void initialise()
	{
		put(OSCDefinition.MESSAGE_BUNDLE_START, OSCDefinition.MESSAGE_BUNDLE_START);
	}

	/**
	 * Put string into map.
	 * 
	 * @param name
	 * @param value
	 */
	public byte[] put(String name, String value)
	{
		byte[] buffer = null;

		try
		{
			buffer = encoder.encode(CharBuffer.wrap(value)).array();

			strings.put(name, buffer);
		}
		catch (CharacterCodingException e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}

		return buffer;
	}

	/**
	 * Return associated byte buffer for name. Will buffer if it doesn't already exist in cache.
	 * 
	 * @param name
	 * 
	 * @return The buffer.
	 */
	public final byte[] get(String name)
	{
		byte[] target = strings.get(name);
		
		if (target == null)
		{
			target = put(name, name);
		}

		return target;
	}

}
