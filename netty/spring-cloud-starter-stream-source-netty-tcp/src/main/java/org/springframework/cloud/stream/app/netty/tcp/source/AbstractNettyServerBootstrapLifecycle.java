package org.springframework.cloud.stream.app.netty.tcp.source;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.SmartLifecycle;

/**
 * @author Soby Chacko
 */
public abstract class AbstractNettyServerBootstrapLifecycle implements SmartLifecycle, InitializingBean, DisposableBean {

	private final ReentrantLock lifecycleLock = new ReentrantLock();

	private volatile boolean running = false;
	private volatile boolean autoStartup = true;
	private volatile int phase = 0;

	@Override
	public void afterPropertiesSet() throws Exception {
		lifecycleLock.lock();
		try {
			doInit();
		}
		finally {
			lifecycleLock.unlock();
		}
	}

	@Override
	public void destroy() throws Exception {
		lifecycleLock.lock();
		try {
			doDestroy();
		}
		finally {
			lifecycleLock.unlock();
		}
	}

	@Override
	public boolean isAutoStartup() {
		return autoStartup;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	public void start() {
		lifecycleLock.lock();
		try {
			if (this.running) {
				return;
			}
			doStart();
			this.running = true;
		}
		finally {
			lifecycleLock.unlock();
		}
	}

	@Override
	public void stop() {
		lifecycleLock.lock();
		try {
			if (!this.running) {
				return;
			}
			doStop();
			this.running = false;
		}
		finally {
			lifecycleLock.unlock();
		}
	}

	@Override
	public boolean isRunning() {
		lifecycleLock.lock();
		try {
			return running;
		}
		finally {
			lifecycleLock.unlock();
		}
	}

	@Override
	public int getPhase() {
		return phase;
	}

	protected abstract void doInit();

	protected abstract void doStop();

	protected void doStart() {

	}

	protected void doDestroy() {

	}

}
