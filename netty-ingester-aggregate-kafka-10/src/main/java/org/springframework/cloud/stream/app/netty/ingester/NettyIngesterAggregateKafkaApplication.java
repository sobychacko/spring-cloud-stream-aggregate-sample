package org.springframework.cloud.stream.app.netty.ingester;

import processor.PayloadDeserializerProcessorApplication;
import source.NettyTcpSourceApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.aggregate.AggregateApplicationBuilder;

/**
 * @author Soby Chacko
 */
@SpringBootApplication
public class NettyIngesterAggregateKafkaApplication {

	public static void main(String... args) {
		new AggregateApplicationBuilder()
				.from(NettyTcpSourceApplication.class).namespace("netty.tcp").args(args)
				.via(PayloadDeserializerProcessorApplication.class).namespace("payload.deserializer").args(args)
				.run(args);
	}

}
