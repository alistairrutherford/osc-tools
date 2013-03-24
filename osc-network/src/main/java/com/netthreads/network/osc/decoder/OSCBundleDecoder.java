package com.netthreads.network.osc.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;

/**
 * Decode OSC Message.
 * 
 */
public class OSCBundleDecoder extends MessageToMessageDecoder<DatagramPacket, OSCBundle>
{
	private static final Logger logger = LoggerFactory.getLogger(OSCBundleDecoder.class);

	private static final int MAX_BUFFER = 0xFFFF;

	private DelimiterBasedFrameDecoder delimiterBasedFrameDecoder;
	private LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder;
	private OSCMessageDecoder oscMessageDecoder;

	/**
	 * Construct decoder.
	 * 
	 */
	public OSCBundleDecoder()
	{
		delimiterBasedFrameDecoder = new DelimiterBasedFrameDecoder(8192, true, Delimiters.nulDelimiter());
		lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(MAX_BUFFER, 0, 4, 0, 4);
		oscMessageDecoder = new OSCMessageDecoder();
	}

	/**
	 * Decode message and populate OSCBundle from pool.
	 * 
	 */
	@Override
	public OSCBundle decode(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
	{
		OSCBundle oscBundle = null;

		ByteBuf byteBuf = msg.data();

		// Read past "bundle message"
		ByteBuf data = (ByteBuf) delimiterBasedFrameDecoder.decode(ctx, byteBuf);

		if (data != null)
		{
			// Time-stamp (2208988800000)
			long timeStamp = byteBuf.readLong();

			while (data != null)
			{
				// Message length
				data = (ByteBuf) lengthFieldBasedFrameDecoder.decode(ctx, byteBuf);

				if (data != null)
				{
					// Message
					OSCMessage oscMessage = oscMessageDecoder.decode(ctx, data);

					if (oscMessage != null)
					{
						if (oscBundle == null)
						{
							oscBundle = OSCBundle.$();
							oscBundle.setTimeTag(timeStamp);
						}

						oscBundle.addMessage(oscMessage);
					}
				}
			}
		}

		logger.debug(oscBundle.toString());

		return oscBundle;
	}
}
