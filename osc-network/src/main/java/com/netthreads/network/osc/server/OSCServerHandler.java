package com.netthreads.network.osc.server;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netthreads.network.osc.decoder.OSCBundleDecoder;
import com.netthreads.network.osc.decoder.OSCMessageDecoder;
import com.netthreads.network.osc.decoder.OSCTypeDecoder;
import com.netthreads.network.osc.decoder.OSCTypeDecoder.OSCType;
import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCMessage;

/**
 * OSC Server handler to decode messages.
 * 
 */
@Sharable
public class OSCServerHandler extends ChannelInboundMessageHandlerAdapter<DatagramPacket>
{
	private static final Logger logger = LoggerFactory.getLogger(OSCServerHandler.class);
	
	private OSCTypeDecoder oscTypeDecoder;
	private OSCMessageDecoder oscMessageDecoder;
	private OSCBundleDecoder oscBundleDecoder;
	private OSCServerListener oscServerListener;
	
	/**
	 * Construct Server handler.
	 * 
	 * @param oscServerListener
	 *            The message handler.
	 */
	public OSCServerHandler(OSCServerListener oscServerListener)
	{
		this.oscServerListener = oscServerListener;
		
		oscTypeDecoder = new OSCTypeDecoder();
		
		oscMessageDecoder = new OSCMessageDecoder();
		
		oscBundleDecoder = new OSCBundleDecoder();
	}
	
	/**
	 * Message received handler.
	 * 
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
	{
		OSCType type = oscTypeDecoder.decode(ctx, msg);
		
		if (type != null)
		{
			switch (type)
			{
				case OSC_MESSAGE:
					OSCMessage oscMessage = oscMessageDecoder.decode(ctx, msg);
					
					if (oscMessage != null)
					{
						logger.info("Receive OSCMessage: " + oscMessage.getAddress() + ", " + oscMessage.getArguments());
						
						if (oscServerListener != null)
						{
							oscServerListener.handleOSCMessage(oscMessage);
						}
					}
					break;
				case OSC_BUNDLE:
					OSCBundle oscBundle = oscBundleDecoder.decode(ctx, msg);
					
					if (oscBundle != null)
					{
						logger.info("Receive OSCBundle: " + oscBundle.getMessages().size());
						
						if (oscServerListener != null)
						{
							oscServerListener.handleOSCBundle(oscBundle);
						}
					}
					break;
				
				default:
					break;
			}
			
		}
	}
	
	/**
	 * Error.
	 * 
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		logger.error(cause.getLocalizedMessage());
	}
	
}
