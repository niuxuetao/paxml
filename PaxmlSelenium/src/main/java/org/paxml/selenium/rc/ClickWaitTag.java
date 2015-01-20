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
 * The clickWait tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "clickWait")
public class ClickWaitTag extends LocatorTag {
    private boolean snapshotBeforePageLoad;
    private long pageLoadTimeout = -1;

    public ClickWaitTag() {
        super();
        setWaitTill(VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommandAfterWait(Context context) {
        final XSelenium selenium = getSelenium(context);
        if (snapshotBeforePageLoad) {
            selenium.takeSnapshot(context);
        }
        SeleniumUtils util = getSeleniumUtils(context);
        util.click(getLocator());
        util.waitForPageToLoad(pageLoadTimeout);
        return null;
    }

    public boolean isSnapshotBeforePageLoad() {
        return snapshotBeforePageLoad;
    }

    public void setSnapshotBeforePageLoad(boolean snapshotBeforePageLoad) {
        this.snapshotBeforePageLoad = snapshotBeforePageLoad;
    }

    public long getPageLoadTimeout() {
        return pageLoadTimeout;
    }

    public void setPageLoadTimeout(long pageLoadTimeout) {
        this.pageLoadTimeout = pageLoadTimeout;
    }

}
