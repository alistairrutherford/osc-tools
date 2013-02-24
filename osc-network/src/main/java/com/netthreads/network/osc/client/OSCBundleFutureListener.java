package com.netthreads.network.osc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import com.netthreads.osc.common.collections.Pool;
import com.netthreads.osc.common.collections.Resettable;
import com.netthreads.osc.common.collections.ResettingPool;
import com.netthreads.osc.common.domain.OSCBundle;

/**
 * Future listener which cleans up when the operation is complete.
 * 
 */
public class OSCBundleFutureListener implements ChannelFutureListener, Resettable
{
	public static final int POOL_INITIAL_CAPACITY = 32;

	// Message Pool.
	private static final Pool<OSCBundleFutureListener> pool = new ResettingPool<OSCBundleFutureListener>(
	        POOL_INITIAL_CAPACITY)
	{
		@Override
		protected OSCBundleFutureListener newObject()
		{
			return new OSCBundleFutureListener();
		}
	};

	private OSCBundle oscBundle;
	private DatagramPacket datagramPacket;

	/**
	 * Get instance.
	 * 
	 * @return instance;
	 */
	public static OSCBundleFutureListener $(OSCBundle oscBundle, InetSocketAddress socket)
	{
		OSCBundleFutureListener oscBundleFutureListener = pool.obtain();

		// OSCBundle
		oscBundleFutureListener.setOscBundle(oscBundle);

		// DatagramPacket
		ByteBuf buffer = Unpooled.buffer();
		DatagramPacket datagramPacket = new DatagramPacket(buffer, socket);

		oscBundleFutureListener.setDatagramPacket(datagramPacket);

		return oscBundleFutureListener;
	}

	public OSCBundleFutureListener()
	{
		reset();
	};

	/**
	 * When operation complete then free the appropriate data.
	 * 
	 */
	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception
	{
		oscBundle.free();

		pool.free(this);
	}

	@Override
	public void reset()
	{
		if (datagramPacket != null)
		{
			datagramPacket.data().clear();
		}

	}

	public OSCBundle getOscBundle()
	{
		return oscBundle;
	}

	public void setOscBundle(OSCBundle oscBundle)
	{
		this.oscBundle = oscBundle;
	}

	public DatagramPacket getDatagramPacket()
	{
		return datagramPacket;
	}

	public void setDatagramPacket(DatagramPacket datagramPacket)
	{
		this.datagramPacket = datagramPacket;
	}

}
