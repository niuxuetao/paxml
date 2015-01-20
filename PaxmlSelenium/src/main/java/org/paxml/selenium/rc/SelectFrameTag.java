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

import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;

/**
 * The selectFrame tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "selectFrame")
public class SelectFrameTag extends LocatorTag {
    public static final String TOP = "top";
    public static final String PARENT = "parent";
    private String target;
    private boolean wait;
    private long timeout = -1;

    @Override
    protected void afterPropertiesInjection(Context context) {
        super.afterPropertiesInjection(context);
        String locator = getLocator();
        if (locator != null && (locator.startsWith("index=") || locator.startsWith("relative="))) {
            setWaitTill(NONE);
        }
    }

    @Override
    protected Object onCommandAfterWait(Context context) {
        SeleniumUtils selenium = getSeleniumUtils(context);
        selenium.selectFrame(getLocator(), wait, timeout >= 0 ? timeout : getSelenium(context)
                .getWaitForReloadTimeout());
        return null;
    }

    @Override
    public String getLocator() {

        if (StringUtils.isBlank(target)) {
            return super.getLocator();
        } else if (TOP.equalsIgnoreCase(target)) {
            return "relative=top";
        } else if (PARENT.equalsIgnoreCase(target)) {
            return "relative=parent";
        } else if (StringUtils.isNumericSpace(target)) {
            return "index=" + Long.parseLong(target);
        } else {
            throw new PaxmlRuntimeException("Unknown target: " + target);
        }
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
