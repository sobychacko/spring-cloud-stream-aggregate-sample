package org.springframework.cloud.stream.app.netty.tcp.source;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Soby Chacko
 */
@ConfigurationProperties("netty.tcp")
public class NettyTcpSourceProperties {

	private String address = "localhost";
	private int port;

	private int selectorCount = 2;
	private int workerThreadCount = 4;
	private boolean socketKeepalive = false;
	private int socketBacklog = 100;
	private boolean socketReuseAddress = true;
	private int socketLinger = 10000;
	private int socketTimeout = 20000;
	private boolean tcpNoDelay = true;
	private long shutdownQuietPeriod = 2L;
	private long shutdownTimeout = 1000L;
	private boolean textOutput = false;

	private int maxFrameLength;
	private int lengthFieldOffset;
	private int lengthFieldLength;
	private int lengthAdjustment = 0;
	private int initialBytesToStrip = 0;

	private ChannelInboundHandlerFactory.Decoder decoder;
	private int maxLineLength;
	private int fixedFrameLength;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getSelectorCount() {
		return selectorCount;
	}

	public void setSelectorCount(int selectorCount) {
		this.selectorCount = selectorCount;
	}

	public int getWorkerThreadCount() {
		return workerThreadCount;
	}

	public void setWorkerThreadCount(int workerThreadCount) {
		this.workerThreadCount = workerThreadCount;
	}

	public boolean isSocketKeepalive() {
		return socketKeepalive;
	}

	public void setSocketKeepalive(boolean socketKeepalive) {
		this.socketKeepalive = socketKeepalive;
	}

	public int getSocketBacklog() {
		return socketBacklog;
	}

	public void setSocketBacklog(int socketBacklog) {
		this.socketBacklog = socketBacklog;
	}

	public boolean isSocketReuseAddress() {
		return socketReuseAddress;
	}

	public void setSocketReuseAddress(boolean socketReuseAddress) {
		this.socketReuseAddress = socketReuseAddress;
	}

	public int getSocketLinger() {
		return socketLinger;
	}

	public void setSocketLinger(int socketLinger) {
		this.socketLinger = socketLinger;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public long getShutdownQuietPeriod() {
		return shutdownQuietPeriod;
	}

	public void setShutdownQuietPeriod(long shutdownQuietPeriod) {
		this.shutdownQuietPeriod = shutdownQuietPeriod;
	}

	public long getShutdownTimeout() {
		return shutdownTimeout;
	}

	public void setShutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public boolean isTextOutput() {
		return textOutput;
	}

	public void setTextOutput(boolean textOutput) {
		this.textOutput = textOutput;
	}

	public int getMaxFrameLength() {
		return maxFrameLength;
	}

	public void setMaxFrameLength(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
	}

	public int getLengthFieldOffset() {
		return lengthFieldOffset;
	}

	public void setLengthFieldOffset(int lengthFieldOffset) {
		this.lengthFieldOffset = lengthFieldOffset;
	}

	public int getLengthFieldLength() {
		return lengthFieldLength;
	}

	public void setLengthFieldLength(int lengthFieldLength) {
		this.lengthFieldLength = lengthFieldLength;
	}

	public int getLengthAdjustment() {
		return lengthAdjustment;
	}

	public void setLengthAdjustment(int lengthAdjustment) {
		this.lengthAdjustment = lengthAdjustment;
	}

	public int getInitialBytesToStrip() {
		return initialBytesToStrip;
	}

	public void setInitialBytesToStrip(int initialBytesToStrip) {
		this.initialBytesToStrip = initialBytesToStrip;
	}

	public ChannelInboundHandlerFactory.Decoder getDecoder() {
		return decoder;
	}

	public void setDecoder(ChannelInboundHandlerFactory.Decoder decoder) {
		this.decoder = decoder;
	}

	public int getMaxLineLength() {
		return maxLineLength;
	}

	public void setMaxLineLength(int maxLineLength) {
		this.maxLineLength = maxLineLength;
	}

	public int getFixedFrameLength() {
		return fixedFrameLength;
	}
}
