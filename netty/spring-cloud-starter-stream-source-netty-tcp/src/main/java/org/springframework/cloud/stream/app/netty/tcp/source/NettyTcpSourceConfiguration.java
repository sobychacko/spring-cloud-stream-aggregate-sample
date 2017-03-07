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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;

/**
 * @author Soby Chacko
 */
@EnableBinding(Source.class)
@EnableConfigurationProperties(NettyTcpSourceProperties.class)
public class NettyTcpSourceConfiguration {

	@Autowired
	private Source source;

	@Autowired
	private NettyTcpSourceProperties nettyTcpSourceProperties;

	@Bean
	public NettyTcpReceivingChannelAdapter nettyTcpReceivingChannelAdapter() throws Exception {
		NettyTcpReceivingChannelAdapter nettyTcpReceivingChannelAdapter =
				new NettyTcpReceivingChannelAdapter(nettyTcpSourceProperties.getAddress(),
						nettyTcpSourceProperties.getPort(), nettySeverBootstrapFactoryBean().getObject());
		nettyTcpReceivingChannelAdapter.setOutputChannel(source.output());

		nettyTcpReceivingChannelAdapter.setChannelInboundHandlers(nettyChannelInboundHandlers());
		return nettyTcpReceivingChannelAdapter;
	}

	@Bean
	public NettySeverBootstrapFactoryBean nettySeverBootstrapFactoryBean() {
		return new NettySeverBootstrapFactoryBean(nettyTcpSourceProperties);
	}

	@Bean
	public ChannelInboundHandlerFactory nettyChannelInboundHandlers() {
		return new ChannelInboundHandlerFactory(nettyTcpSourceProperties);
	}

}
