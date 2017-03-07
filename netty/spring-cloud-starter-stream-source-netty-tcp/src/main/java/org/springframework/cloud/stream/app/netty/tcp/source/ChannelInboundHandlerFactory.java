package org.springframework.cloud.stream.app.netty.tcp.source;

import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;

/**
 * @author Soby Chacko
 */
public class ChannelInboundHandlerFactory {

	public enum Decoder {
		LENGTH, LINE, FIXED_FRAME_LENGTH
	}

	private NettyTcpSourceProperties nettyTcpSourceProperties;

	public ChannelInboundHandlerFactory(NettyTcpSourceProperties nettyTcpSourceProperties) {
		this.nettyTcpSourceProperties = nettyTcpSourceProperties;
	}

	public ChannelInboundHandler[] createChannelInboundHandlers() throws Exception {
		switch (nettyTcpSourceProperties.getDecoder()) {
			case LINE:
				return new ChannelInboundHandler[]{new LineBasedFrameDecoder(nettyTcpSourceProperties.getMaxLineLength())};
			case LENGTH:
				return new ChannelInboundHandler[]{
						new LengthFieldBasedFrameDecoder(nettyTcpSourceProperties.getMaxFrameLength(),
								nettyTcpSourceProperties.getLengthFieldOffset(), nettyTcpSourceProperties.getLengthFieldLength(),
								nettyTcpSourceProperties.getLengthAdjustment(), nettyTcpSourceProperties.getInitialBytesToStrip())};
			case FIXED_FRAME_LENGTH:
				return new ChannelInboundHandler[]{
						new FixedLengthFrameDecoder(nettyTcpSourceProperties.getFixedFrameLength())};
			default:
				return new ChannelInboundHandler[]{};
		}

	}
}
