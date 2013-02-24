package com.netthreads.network.osc.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.MessageToMessageDecoder;

import com.netthreads.network.osc.decoder.OSCTypeDecoder.OSCType;

/**
 * Decode OSC message type.
 * 
 * Note: After decoding the input buffer reader index is reset.
 *
 */
public class OSCTypeDecoder extends MessageToMessageDecoder<DatagramPacket, OSCType>
{
	public static enum OSCType
	{
		OSC_MESSAGE, OSC_BUNDLE
	};

	private DelimiterBasedFrameDecoder delimiterBasedFrameDecoder;

	public OSCTypeDecoder()
	{
		delimiterBasedFrameDecoder = new DelimiterBasedFrameDecoder(8192, false, Delimiters.nulDelimiter());
	}

	@Override
	public OSCType decode(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
	{
		OSCType type = null;

		ByteBuf buffer = msg.data();
		int readerIndex = buffer.readerIndex();

		ByteBuf typeBuf = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, msg.data());

		if (typeBuf != null)
		{
			if (typeBuf.getByte(0) == DecoderHelper.BUNDLE[0])
			{
				int readableBytes = typeBuf.readableBytes();
				
				if (readableBytes >= DecoderHelper.BUNDLE.length)
				{
					boolean match = DecoderHelper.compareBuffer(typeBuf.array(), DecoderHelper.BUNDLE);

					if (match)
					{
						type = OSCType.OSC_BUNDLE;

					}
				}
			}
			else
			{
				type = OSCType.OSC_MESSAGE;
			}
		}

		// We must reset our source buffer
		msg.data().readerIndex(readerIndex);

		return type;
	}

}
