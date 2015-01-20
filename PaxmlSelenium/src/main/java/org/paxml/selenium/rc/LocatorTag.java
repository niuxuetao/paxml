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

import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;

/**
 * The base impl of locator tags.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class LocatorTag extends SeleniumTag {
    public static final String VISIBLE = "visible";
    public static final String PRESENT = "present";
    public static final String NONE = "none";

    	
    private String locator;
    private long timeout = -1;
    private long checkInterval = -1;
    private String waitTill = PRESENT;

    public String getLocator() {
    	
    	return locator;
    }

    public void setLocator(String locator) {

        this.locator = locator;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    protected long getTimeout(Context context) {
        if (timeout >= 0) {
            return timeout;
        }
        return getSelenium(context).getWaitForTimeout();

    }

    protected long getCheckInterval(Context context) {
        if (checkInterval >= 0) {
            return checkInterval;
        }
        return getSelenium(context).getWaitForCheckInterval();

    }

    protected void waitFor(Context context) {
        if (PRESENT.equalsIgnoreCase(waitTill)) {
            getSeleniumUtils(context).waitTillPresent(getLocator(), timeout, checkInterval, true);
        } else if (VISIBLE.equalsIgnoreCase(waitTill)) {
            getSeleniumUtils(context).waitTillVisible(getLocator(), timeout, checkInterval, true);
        }
        
    }

    @Override
    protected Object onCommand(Context context) {
        // wait for the existence of the element before doing anything
        waitFor(context);
        return onCommandAfterWait(context);
    }

    protected Object onCommandAfterWait(Context context) {
        // subclasses should overwrite
        throw new PaxmlRuntimeException("Subclass should overwrite this method but it doesn't: " + getClass().getName());
    }

    public String getWaitTill() {
        return waitTill;
    }

    public void setWaitTill(String waitTill) {
        this.waitTill = waitTill;
    }

}
