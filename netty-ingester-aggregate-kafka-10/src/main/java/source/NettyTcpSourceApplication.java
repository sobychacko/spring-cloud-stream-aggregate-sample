package source;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author Soby Chacko
 */
@SpringBootApplication
@Import(org.springframework.cloud.stream.app.netty.tcp.source.NettyTcpSourceConfiguration.class)
public class NettyTcpSourceApplication {
}

