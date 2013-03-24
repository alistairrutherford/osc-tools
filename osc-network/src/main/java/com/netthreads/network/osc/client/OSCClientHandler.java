/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.netthreads.network.osc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class OSCClientHandler extends ChannelInboundMessageHandlerAdapter<DatagramPacket>
{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		super.channelActive(ctx);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception
	{
		String response = msg.data().toString(CharsetUtil.UTF_8);
		System.out.println(response.substring(6));
		ctx.close();
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
	{
		super.userEventTriggered(ctx, evt);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
	{
		cause.printStackTrace();
		ctx.close();
	}
}
