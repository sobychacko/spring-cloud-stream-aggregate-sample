/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.netty.tcp.source;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;

import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;

/**
 * @author Soby Chacko
 */
public class NettyTcpReceivingChannelAdapter extends MessageProducerSupport {

	private final ServerBootstrap serverBootstrap;

	private final String address;
	private final int port;

	private ChannelFuture channelFuture;
	private ChannelInboundHandlerFactory channelInboundHandlerFactory;

	public NettyTcpReceivingChannelAdapter(String address, int port, ServerBootstrap serverBootstrap) {
		super();
		this.address = address;
		this.port = port;
		this.serverBootstrap = serverBootstrap;
	}

	public void setChannelInboundHandlers(ChannelInboundHandlerFactory channelInboundHandlerFactory) {
		this.channelInboundHandlerFactory = channelInboundHandlerFactory;
	}

	@Override
	protected void onInit() {
		super.onInit();

		serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(final SocketChannel socketChannel) throws Exception {
				final ChannelPipeline channelPipeline = socketChannel.pipeline();

				boolean first = true;
				ChannelInboundHandler[] channelInboundHandlers = channelInboundHandlerFactory.createChannelInboundHandlers();
				for (ChannelInboundHandler channelInboundHandler : channelInboundHandlers) {
					if (first) {
						channelPipeline.addFirst(channelInboundHandler);
						first = false;
					}
					else {
						channelPipeline.addLast(channelInboundHandler);
					}
				}

				channelPipeline.addLast("sourceOutputHandler", new SimpleChannelInboundHandler<ByteBuf>() {
					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						byte[] data;
						if (msg.hasArray()) {
							data = msg.array();
						}
						else {
							data = new byte[msg.readableBytes()];
							msg.readBytes(data);
						}
						AbstractIntegrationMessageBuilder<byte[]> builder = getMessageBuilderFactory().withPayload(data);
						sendMessage(builder.build());
					}
				});
				socketChannel.closeFuture().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						socketChannel.close();
					}
				});
			}
		});
		serverBootstrap.validate();
	}

	@Override
	public void doStart() {
		try {
			this.channelFuture = this.serverBootstrap.localAddress(address, port).bind().sync();
		}
		catch (InterruptedException e) {
			//log it
		}
	}

	@Override
	public void doStop() {
		try {
			this.channelFuture.channel().close().await(2000L);
		}
		catch (InterruptedException e) {
			//log it
		}
	}

}
