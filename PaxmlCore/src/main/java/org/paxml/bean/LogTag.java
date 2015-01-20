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
package org.paxml.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlResource;

/**
 * Log tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "log")
public class LogTag extends BeanTag {
    private static final Log log = LogFactory.getLog(LogTag.class);

    /**
     * Log levels.
     * 
     * @author Xuetao Niu
     * 
     */
    public static enum Level {
        /**
         * Debug level.
         */
        DEBUG,
        /**
         * Info level.
         */
        INFO,
        /**
         * Warn level.
         */
        WARN,
        /**
         * Error level.
         */
        ERROR
    }

    private Level level = Level.INFO;
    private boolean fullResourceName;
    private boolean includeResourceName;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        Object value = getValue();
        PaxmlResource res = getEntity().getResource();
        String msg = includeResourceName ? ((fullResourceName ? res.getPath() : res.getName()) + ": " + value) : String
                .valueOf(value);

        if (level == Level.ERROR) {
            log.error(msg);
        } else if (level == Level.WARN) {
            log.warn(msg);
        } else if (level == Level.DEBUG) {
            log.debug(msg);
        } else {
            log.info(msg);
        }
        return value;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public boolean isFullResourceName() {
        return fullResourceName;
    }

    public void setFullResourceName(boolean fullResourceName) {
        this.fullResourceName = fullResourceName;
    }

}
