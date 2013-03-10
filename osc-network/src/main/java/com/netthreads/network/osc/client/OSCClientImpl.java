package com.netthreads.network.osc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;

import com.google.inject.Singleton;
import com.netthreads.osc.common.domain.OSCBundle;
import com.netthreads.osc.common.domain.OSCEncoder;

@Singleton
public class OSCClientImpl implements OSCClient
{
	private final OSCEncoder encoder;
	private Bootstrap bootstrap;

	private boolean connected;

	private InetSocketAddress socket;
	private Channel channel;

	/**
	 * OSC Client.
	 * 
	 */
	public OSCClientImpl()
	{
		connected = false;

		encoder = new OSCEncoder();

		channel = null;
	}

	/**
	 * Connect to OSC server.
	 * 
	 * @return True if connected.
	 */
	@Override
	public boolean connect(String host, int port)
	{
		// We are going to have to revisit this as I want to be able to reuse this.
		bootstrap = new Bootstrap();

		bootstrap.group(new NioEventLoopGroup()).channel(NioDatagramChannel.class).localAddress(new InetSocketAddress(0)).option(ChannelOption.SO_BROADCAST, true).handler(new OSCClientHandler());

		try
		{
			channel = bootstrap.bind().sync().channel();

			socket = new InetSocketAddress(host, port);

			if (socket.getAddress() != null)
			{
				connected = true;
			}
			else
			{
				connected = false;
			}
		}
		catch (InterruptedException e)
		{
			connected = false;
		}

		return connected;

	}

	/**
	 * Disconnect.
	 * 
	 */
	@Override
	public void disconnect()
	{
		if (connected)
		{
			try
			{
				if (channel != null)
				{
					channel.close().awaitUninterruptibly();
				}
			}
			finally
			{
				if (bootstrap != null)
				{
					bootstrap.shutdown();
				}

				connected = false;
			}

		}

	}

	/**
	 * Send message.
	 * 
	 * Note, the future listener handles re-pooling the bundle and datagram packet objects.
	 * 
	 * @param oscBundle
	 */
	@Override
	public synchronized void send(OSCBundle oscBundle)
	{
		OSCBundleFutureListener oscBundleFutureListener = OSCBundleFutureListener.$(oscBundle, socket);

		DatagramPacket datagramPacket = oscBundleFutureListener.getDatagramPacket();

		encoder.encode(oscBundle, datagramPacket.data());

		ChannelFuture channelFuture = channel.write(datagramPacket);

		oscBundleFutureListener.setOscBundle(oscBundle);

		channelFuture.addListener(oscBundleFutureListener);
	}

	/**
	 * Return connection status.
	 * 
	 * @return connection status.
	 */
	public boolean isConnected()
	{
		return connected;
	}
}
