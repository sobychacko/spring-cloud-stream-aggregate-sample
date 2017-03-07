package org.springframework.cloud.stream.app.netty.tcp.source;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * @author Soby Chacko
 */
public class NettySeverBootstrapFactoryBean extends AbstractNettyServerBootstrapLifecycle implements FactoryBean<ServerBootstrap> {

	private static final Log logger = LogFactory.getLog(NettySeverBootstrapFactoryBean.class);

	private transient ServerBootstrap server;
	private transient NioEventLoopGroup selectorGroup;
	private transient NioEventLoopGroup workerGroup;

	private ByteBufAllocator byteBufAllocator = new UnpooledByteBufAllocator(false);

	private NettyTcpSourceProperties nettyTcpSourceProperties;

	public NettySeverBootstrapFactoryBean(NettyTcpSourceProperties nettyTcpSourceProperties) {
		this.nettyTcpSourceProperties = nettyTcpSourceProperties;
	}

	@Override
	protected void doInit() {
		this.selectorGroup = new NioEventLoopGroup(nettyTcpSourceProperties.getSelectorCount(), new CustomizableThreadFactory());
		this.workerGroup = new NioEventLoopGroup(nettyTcpSourceProperties.getWorkerThreadCount(), new CustomizableThreadFactory());

		this.server = new ServerBootstrap()
				.group(selectorGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, nettyTcpSourceProperties.isSocketKeepalive())
				.option(ChannelOption.SO_BACKLOG, nettyTcpSourceProperties.getSocketBacklog())
				.option(ChannelOption.SO_REUSEADDR, nettyTcpSourceProperties.isSocketReuseAddress())
				.option(ChannelOption.SO_LINGER, nettyTcpSourceProperties.getSocketLinger())
				.option(ChannelOption.SO_TIMEOUT, nettyTcpSourceProperties.getSocketTimeout())
				.option(ChannelOption.TCP_NODELAY, nettyTcpSourceProperties.isTcpNoDelay())
				.option(ChannelOption.ALLOCATOR, byteBufAllocator)
				.childOption(ChannelOption.SO_KEEPALIVE, nettyTcpSourceProperties.isSocketKeepalive())
				.childOption(ChannelOption.SO_REUSEADDR, nettyTcpSourceProperties.isSocketReuseAddress())
				.childOption(ChannelOption.SO_LINGER, nettyTcpSourceProperties.getSocketLinger())
				.childOption(ChannelOption.TCP_NODELAY, nettyTcpSourceProperties.isTcpNoDelay());
	}

	@Override
	protected void doStop() {
		Future<?> workerGroupFuture = workerGroup.shutdownGracefully(nettyTcpSourceProperties.getShutdownQuietPeriod(),
				nettyTcpSourceProperties.getShutdownTimeout(), TimeUnit.MILLISECONDS);
		Future<?> selectorGroupFuture = selectorGroup.shutdownGracefully(nettyTcpSourceProperties.getShutdownQuietPeriod(),
				nettyTcpSourceProperties.getShutdownTimeout(), TimeUnit.MILLISECONDS);

		try {
			boolean workerGroupShutdown = workerGroupFuture.await().isSuccess();
			if (!workerGroupShutdown) {
				logger.warn("Netty server worker groups did not shut down in " + nettyTcpSourceProperties.getShutdownTimeout() + "milliseconds");
			}
		}
		catch (InterruptedException e) {
			logger.warn("Netty server worker group shutdown interrupted", e);
		}

		try {
			boolean selectorGroupShutdown = selectorGroupFuture.await().isSuccess();
			if (!selectorGroupShutdown) {
				logger.warn("Netty server selector groups did not shut down in " + nettyTcpSourceProperties.getShutdownTimeout() + "milliseconds");
			}
		}
		catch (InterruptedException e) {
			logger.warn("Netty server selector group shutdown interrupted", e);
		}
	}

	@Override
	public ServerBootstrap getObject() throws Exception {
		return server;
	}

	@Override
	public Class<?> getObjectType() {
		return ServerBootstrap.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
