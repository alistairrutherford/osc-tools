package com.netthreads.network.osc.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.string.StringDecoder;

import com.netthreads.osc.common.domain.OSCDefinition;
import com.netthreads.osc.common.domain.OSCMessage;

/**
 * Decode OSC Message.
 * 
 */
public class OSCMessageDecoder extends MessageToMessageDecoder<DatagramPacket, OSCMessage>
{
	private DelimiterBasedFrameDecoder delimiterBasedFrameDecoder;
	private LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder;
	private StringDecoder stringDecoder;
	
	public OSCMessageDecoder()
	{
		delimiterBasedFrameDecoder = new DelimiterBasedFrameDecoder(8192, Delimiters.nulDelimiter());
		lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(2 ^ 32, 0, 4);
		stringDecoder = new StringDecoder();
	}
	
	/**
	 * Decode message and populate OSCMessage from pool.
	 * 
	 */
	@Override
	public OSCMessage decode(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
	{
		ByteBuf byteBuf = msg.data();
		
		OSCMessage oscMessage = decode(ctx, byteBuf);
		
		return oscMessage;
	}
	
	/**
	 * Decode message.
	 * 
	 * @param ctx
	 * @param byteBuf
	 * 
	 * @return Populated message or null if not complete.
	 * 
	 * @throws Exception
	 */
	public OSCMessage decode(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception
	{
		OSCMessage oscMessage = null;
		
		skipPadding(byteBuf, DecoderHelper.PAD_BYTES);
		
		ByteBuf data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);
		
		if (data != null)
		{
			String address = stringDecoder.decode(ctx, data);
			
			if (address != null)
			{
				oscMessage = OSCMessage.$(address);
				
				skipPadding(byteBuf, DecoderHelper.PAD_BYTES);
				
				data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);
				
				if (data != null)
				{
					String types = stringDecoder.decode(ctx, data);
					
					if (types != null)
					{
						skipPadding(byteBuf, DecoderHelper.PAD_BYTES);
						
						int typeCount = types.length();
						
						// Note we skip leading comma
						for (int index = 1; index < typeCount; index++)
						{
							char typeChar = types.charAt(index);
							
							Object argument = extractArgument(ctx, typeChar, byteBuf);
							
							// Note type
							oscMessage.getTypes().add(typeChar);
							
							// Note argument object.
							oscMessage.addArgument(argument);
						}
					}
				}
			}
		}
		
		return oscMessage;
	}
	
	/**
	 * Extract argument.
	 * 
	 * @param type
	 * @param byteBuf
	 * 
	 * @return The argument object.
	 * @throws Exception
	 */
	private Object extractArgument(ChannelHandlerContext ctx, char type, ByteBuf byteBuf) throws Exception
	{
		Object argument = null;
		
		switch (type)
		{
			case OSCDefinition.TYPE_STRING:
				ByteBuf data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);
				argument = stringDecoder.decode(ctx, data);
				break;
			case OSCDefinition.TYPE_FLOAT:
				argument = byteBuf.readFloat();
				break;
			case OSCDefinition.TYPE_INT:
				argument = byteBuf.readInt();
				break;
			case OSCDefinition.TYPE_LONG:
				argument = byteBuf.readLong();
				break;
			case OSCDefinition.TYPE_BLOB:
				argument = Unpooled.copiedBuffer((ByteBuf) lengthFieldBasedFrameDecoder.decode(ctx, byteBuf));
				skipPadding(byteBuf, DecoderHelper.PAD_BYTES);
				break;
			case OSCDefinition.TYPE_TRUE:
				argument = byteBuf.readBoolean();
				break;
			case OSCDefinition.TYPE_FALSE:
				argument = byteBuf.readBoolean();
				break;
			case OSCDefinition.TYPE_ARRAY_START:
				// TODO
				break;
			case OSCDefinition.TYPE_ARRAY_END:
				// TODO
				break;
			default:
				break;
		}
		
		return argument;
	}
	
	/**
	 * Skip pad
	 * 
	 * @param byteBuf
	 */
	private void skipPadding(ByteBuf byteBuf, int padding)
	{
		// Calculate padding.
		int padBytes = DecoderHelper.padBytes(byteBuf, padding);
		
		// Skip the padding.
		byteBuf.readerIndex(byteBuf.readerIndex() + padBytes);
	}
	
}
