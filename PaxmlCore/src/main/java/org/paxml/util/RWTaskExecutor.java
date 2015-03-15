package org.paxml.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.paxml.core.PaxmlRuntimeException;

/**
 * Manage task execution with read-write lock per task key.
 * 
 * @author Xuetao Niu
 * 
 */
public class RWTaskExecutor {

	private final ConcurrentMap<Object, ReentrantReadWriteLock> locks = new ConcurrentHashMap<Object, ReentrantReadWriteLock>();

	/**
	 * Execute the a task with read-lock for the given lock key.
	 * 
	 * @param lockKey
	 *            the task key
	 * @param cal
	 *            the task
	 * @return the return value of the task
	 * 
	 */
	public <K, V> V executeRead(K lockKey, Callable<V> cal) {
		return execute(false, lockKey, cal);
	}

	/**
	 * Execute the a task with write-lock for the given lock key.
	 * 
	 * @param lockKey
	 *            the task key
	 * @param cal
	 *            the task
	 * @return the return value of the task
	 */
	public <K, V> V executeWrite(K lockKey, Callable<V> cal) {

		return execute(true, lockKey, cal);
	}

	private <K, V> V execute(boolean write, K lockKey, Callable<V> cal) {
		ReentrantReadWriteLock lock = locks.get(lockKey);
		if (lock == null) {
			lock = new ReentrantReadWriteLock();
			ReentrantReadWriteLock old = locks.putIfAbsent(lockKey, lock);
			if (old != null) {
				lock = old;
			}
		}
		Lock l = write ? lock.writeLock() : lock.readLock();
		try {
			l.lock();
			return cal.call();
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		} finally {
			l.unlock();
		}
	}
}
