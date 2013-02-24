package com.netthreads.network.osc.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSC server.
 *  
 */
public class OSCServerImpl implements OSCServer
{
	private static final Logger logger = LoggerFactory.getLogger(OSCServerImpl.class);

	private final int port;
	private OSCServerListener oscServerListener;
	private Bootstrap bootStrap;
	
	/**
	 * OSCServer handler listens for OSC messages and passes them to linked handler.
	 * 
	 * @param port
	 * @param oscServerListener
	 */
	public OSCServerImpl(int port, OSCServerListener oscServerListener)
	{
		this.port = port;
		
		this.oscServerListener = oscServerListener;
	}

	/* (non-Javadoc)
	 * @see com.netthreads.network.osc.server.OSCServer#listen()
	 */
	public void listen()
	{
		bootStrap = new Bootstrap();
		try
		{
			oscServerListener.handleStart();
			
			bootStrap.group(new NioEventLoopGroup())
				.channel(NioDatagramChannel.class)
				.localAddress(new InetSocketAddress(port))
				.option(ChannelOption.SO_BROADCAST, true)
				.handler(new OSCServerHandler(oscServerListener));

			bootStrap.bind()
				.sync()
				.channel()
				.closeFuture()
				.await();
		}
		catch (InterruptedException e)
		{
			logger.info("Interrupted");
		}
		finally
		{
			bootStrap.shutdown();
			
			oscServerListener.handleShutdown();
		}
	}
	
	/**
	 * Shut down server.
	 * 
	 */
	public void shutdown()
	{
		bootStrap.shutdown();		
	}
}
