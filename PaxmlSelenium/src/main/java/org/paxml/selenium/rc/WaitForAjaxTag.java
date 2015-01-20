/**
 * This file is part of PaxmlSelenium.
 *
 * PaxmlSelenium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlSelenium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlSelenium.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.selenium.rc;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The waitForAjax tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "waitForAjax")
public class WaitForAjaxTag extends SeleniumTag {
    private boolean hard = true;
    private long timeout = -1;

    @Override
    protected Object onCommand(Context context) {
        SeleniumUtils selenium = getSeleniumUtils(context);        
        if (selenium.waitForAjaxRequestsStart(XSelenium.WAIT_FOR_AJAX_START_TIMEOUT, false)) {
            return selenium.waitForAjaxRequestsStop(timeout, hard);
        } else {
            return false;
        }
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isHard() {
        return hard;
    }

    public void setHard(boolean hard) {
        this.hard = hard;
    }

}
