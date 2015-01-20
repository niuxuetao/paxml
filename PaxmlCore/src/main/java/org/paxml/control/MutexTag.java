/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.control;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.IExpression;
import org.paxml.util.ReflectUtils;

/**
 * Base impl of mutex tag.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "mutex", factory = MutexTagFactory.class)
public class MutexTag extends AbstractClosureTag {

    /**
     * The default lock acquisition time out in ms.
     */
    public static final long DEFAULT_TIMEOUT = 120000;

    private static final Log log = LogFactory.getLog(MutexTag.class);

    private static final ConcurrentMap<String, Lock> MAP = new ConcurrentHashMap<String, Lock>();

    private IExpression timeout;
    private IExpression name;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doExecute(Context context) {
        String lockName = name == null ? null : name.evaluateString(context);
        if (lockName == null) {
            lockName = "";
        }
        Long timeoutValue = timeout == null ? null : ReflectUtils.coerceType(timeout.evaluate(context), Long.class);
        if (timeoutValue == null) {
            timeoutValue = DEFAULT_TIMEOUT;
        }
        Lock lock = getLock(lockName);

        if (log.isInfoEnabled()) {
            log.info("Waiting to enter mutex '" + lockName + "', timeout: " + timeoutValue);
        }
        long start = System.currentTimeMillis();
        long duration = 0;
        try {

            if (!lock.tryLock(timeoutValue, TimeUnit.MILLISECONDS)) {
                throw new PaxmlRuntimeException("Cannot enter mutex after waiting for " + timeoutValue + " ms, mutex name: "
                        + lockName);
            }
            duration = System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            if (log.isInfoEnabled()) {

                log.info("Mutex '" + lockName + "' entered after waiting for " + duration + " ms");
            }
        } catch (InterruptedException e) {
            throw new PaxmlRuntimeException("Mutex entering interrupted, mutex name: " + lockName, e);
        }

        try {

            return super.doExecute(context);
        } finally {
            lock.unlock();
            if (log.isInfoEnabled()) {
                duration = System.currentTimeMillis() - start;
                log.info("Mutex '" + lockName + "' exited after being used for " + duration + " ms");
            }
        }

    }

    public IExpression getTimeout() {
        return timeout;
    }

    public void setTimeout(IExpression timeout) {
        this.timeout = timeout;
    }

    public IExpression getName() {
        return name;
    }

    public void setName(IExpression name) {
        this.name = name;
    }

    private Lock getLock(String lockName) {

        Lock lock = MAP.get(lockName);
        if (lock == null) {
            lock = new ReentrantLock(true);
            Lock existingLock = MAP.putIfAbsent(lockName, lock);
            if (existingLock != null) {
                lock = existingLock;
            }
        }
        return lock;
    }

}
