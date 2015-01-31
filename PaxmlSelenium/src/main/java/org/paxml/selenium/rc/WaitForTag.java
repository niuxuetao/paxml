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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The waitFor tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = WaitForTag.TAG_NAME)
public class WaitForTag extends LocatorTag {
    private static final Log log = LogFactory.getLog(WaitForTag.class);
    public static final String TAG_NAME = "waitFor";
    private String js;    
    private boolean hard = true;
    public WaitForTag(){
        super();
        setWaitTill(PRESENT);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        if (js != null) {
            return getSeleniumUtils(context).waitForCondition(js, getTimeout(), hard);
        } else {
            if (PRESENT.equalsIgnoreCase(getWaitTill())) {
                return getSeleniumUtils(context).waitTillPresent(getLocator(), getTimeout(), getCheckInterval(), hard);
            } else if (VISIBLE.equalsIgnoreCase(getWaitTill())) {
                return getSeleniumUtils(context).waitTillVisible(getLocator(), getTimeout(), getCheckInterval(), hard);
            }
            
        }
        
        return true;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public boolean isHard() {
        return hard;
    }

    public void setHard(boolean hard) {
        this.hard = hard;
    }

}
