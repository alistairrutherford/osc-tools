package com.netthreads.osc.common.domain;

import io.netty.buffer.ByteBuf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Encoder for OSCMessage and OSCBundle.
 * 
 */
public class OSCEncoder
{
	private static final BigDecimal MILLISECONDS_FROM_1900_TO_1970 = new BigDecimal("2208988800000");

	private static final byte ZERO = 0;
	private static final int PAD_BYTES = 4;

	/**
	 * Encode appropriately.
	 * 
	 * TODO: Think about moving the encoding back into the domain objects as this is not good design.
	 * 
	 * @param message
	 * @param buffer
	 */
	private void encode(OSCPacket message, ByteBuf buffer)
	{
		if (message instanceof OSCBundle)
		{
			encode((OSCBundle) message, buffer);
		}
		else
		{
			encode((OSCMessage) message, buffer);
		}
	}

	/**
	 * Encode message.
	 * 
	 * @param oscMessage
	 * @param buffer
	 */
	public void encode(OSCMessage oscMessage, ByteBuf buffer)
	{
		// Write address
		writeAddress(oscMessage.getAddress(), buffer);

		// Types
		writeTypes(oscMessage.getArguments(), buffer);

		// Arguments
		writeArguments(oscMessage.getArguments(), buffer);
	}

	/**
	 * Encode bundle.
	 * 
	 * @param oscBundle
	 * @param buffer
	 */
	public void encode(OSCBundle oscBundle, ByteBuf buffer)
	{
		buffer.writeBytes(OSCString.$().get(OSCDefinition.MESSAGE_BUNDLE_START));

		buffer.writeByte(ZERO);

		// Time-stamp, note : java time runs from 1970 onwards.
		long millisecs = oscBundle.getTimeTag() + MILLISECONDS_FROM_1900_TO_1970.longValue();
		buffer.writeLong(millisecs);

		// ---------------------------------------------------------------
		// For each packet stuff bytes into buffer.
		// ---------------------------------------------------------------
		for (OSCPacket message : oscBundle.getMessages())
		{
			// ---------------------------------------------------------------
			// Make a note of position to write size
			// ---------------------------------------------------------------
			int sizePos = buffer.writerIndex();

			// Insert holder size.
			buffer.writeInt(0);

			// ---------------------------------------------------------------
			// Make a note of start of contents
			// ---------------------------------------------------------------
			int startPos = buffer.writerIndex();

			// Encode OSC packet
			this.encode(message, buffer);

			// Mark
			buffer.markWriterIndex();

			// Insert size of message
			int writerIndex = buffer.writerIndex();
			int size = writerIndex - startPos;
			buffer.writerIndex(sizePos);
			buffer.writeInt(size);

			// Reset to mark.
			buffer.resetWriterIndex();
		}

	}

	/**
	 * Write address.
	 * 
	 * @param address
	 * @param buffer
	 */
	private void writeAddress(String address, ByteBuf buffer)
	{
		// Note: We cache ALL address strings.
		buffer.writeBytes(OSCString.$().get(address));

		buffer.writeByte(ZERO);

		pad(buffer, PAD_BYTES);
	}

	/**
	 * Write types.
	 * 
	 * @param buffer
	 *            The outgoing buffer.
	 */
	private void writeTypes(List<Object> arguments, ByteBuf buffer)
	{
		buffer.writeByte((byte) ',');

		for (Object argument : arguments)
		{
			writeType(argument, buffer);
		}

		buffer.writeByte(ZERO);

		pad(buffer, PAD_BYTES);
	}

	/**
	 * Write type.
	 * 
	 * @param argument
	 * @param buffer
	 */
	public void writeType(Object argument, ByteBuf buffer)
	{
		if (argument instanceof String)
		{
			buffer.writeByte((byte) OSCDefinition.TYPE_STRING);
		}
		else if (argument instanceof Float)
		{
			buffer.writeByte((byte) OSCDefinition.TYPE_FLOAT);
		}
		else if (argument instanceof Integer)
		{
			buffer.writeByte((byte) OSCDefinition.TYPE_INT);
		}
		else if (argument instanceof BigInteger)
		{
			buffer.writeByte((byte) OSCDefinition.TYPE_LONG);
		}
		else if (argument instanceof byte[])
		{
			buffer.writeByte((byte) OSCDefinition.TYPE_BLOB);
		}
		else if (argument instanceof Boolean)
		{
			buffer.writeByte((byte) (((Boolean) argument) ? OSCDefinition.TYPE_TRUE : OSCDefinition.TYPE_FALSE));
		}
		else if (argument instanceof Object[])
		{
			Object[] arrayArguments = (Object[]) argument;
			buffer.writeByte((byte) OSCDefinition.TYPE_ARRAY_START);
			for (Object arrayArgument : arrayArguments)
			{
				writeType(arrayArgument, buffer);
			}
			buffer.writeByte((byte) OSCDefinition.TYPE_ARRAY_END);
		}
	}

	/**
	 * Write arguments.
	 * 
	 * @param buffer
	 */
	private void writeArguments(List<Object> arguments, ByteBuf buffer)
	{
		if (arguments.size() > 0)
		{
			for (Object argument : arguments)
			{
				if (argument instanceof String)
				{
					// Adds to heap?
					buffer.writeBytes(((String) argument).getBytes());

					pad(buffer, PAD_BYTES);
				}
				else if (argument instanceof Float)
				{
					buffer.writeFloat((Float) argument);
				}
				else if (argument instanceof Integer)
				{
					buffer.writeInt((Integer) argument);
				}
				else if (argument instanceof BigInteger)
				{
					buffer.writeLong(((BigInteger) argument).longValue());
				}
				else if (argument instanceof byte[])
				{
					byte[] bytes = (byte[]) argument;

					buffer.writeInt(bytes.length);
					buffer.writeBytes((byte[]) argument);

					pad(buffer, PAD_BYTES);
				}
				else if (argument instanceof Boolean)
				{
					buffer.writeByte(((Boolean) argument) ? (byte) 0x01 : (byte) 0x00);
				}
			}

			pad(buffer, PAD_BYTES);
		}
	}

	/**
	 * Pad buffer contents out to the nearest 'padByte' bytes.
	 * 
	 * @param start
	 * @param buffer
	 */
	private void pad(ByteBuf buffer, int padBytes)
	{
		int position = buffer.writerIndex();

		int remainder = position % padBytes;
		if (remainder > 0)
		{
			int pad = padBytes - remainder;
			while (pad > 0)
			{
				buffer.writeByte(ZERO);

				pad--;
			}
		}
	}

}
