package com.netthreads.network.osc.decoder;

import io.netty.buffer.ByteBuf;

/**
 * Some helper methods.
 * 
 */
public class DecoderHelper
{
	public static final int PAD_BYTES = 4;

	public static byte[] BUNDLE =
	{
			(byte) 0x23, /* [#] */
			(byte) 0x62, /* [b] */
			(byte) 0x75, /* [u] */
			(byte) 0x6e, /* [n] */
			(byte) 0x64, /* [d] */
			(byte) 0x6c, /* [l] */
			(byte) 0x65, /* [e] */
			(byte) 0x0	/* [^@ (NUL)] */
	};
	
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

	/**
	 * Calculates the number of bytes to read which would pad the current buffer position out to 'padBytes'.
	 * 
	 * @param position
	 *            The current position
	 * @param padBytes
	 *            The padding count.
	 * 
	 * @return Number of padding bytes.
	 */
	public static int padBytes(ByteBuf buffer, int padBytes)
	{
		int pad = 0;

		int position = buffer.readerIndex();

		int remainder = position % padBytes;
		if (remainder > 0)
		{
			pad = padBytes - remainder;
		}

		return pad;
	}

}
