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
import org.paxml.core.PaxmlRuntimeException;

/**
 * Pause tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "pause")
public class PauseTag extends BeanTag {
    private static final Log log = LogFactory.getLog(PauseTag.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        long ms = 0;

        try {
            Object value = getValue();
            if (value != null) {
                ms = Long.parseLong(value.toString());
            }
            log.info("Pausing for " + ms + " ms ...");
            Thread.sleep(ms);
        } catch (Exception e) {
            throw new PaxmlRuntimeException("Cannot pause for: " + ms + " milliseconds", e);
        }

        return null;
    }

}
