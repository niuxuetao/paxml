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
package org.paxml.selenium.webdriver;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The frame tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "frame")
public class FrameTag extends WebDriverTag {
    private String name;
    private int index = -1;
    private Boolean top;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        TargetLocator tl = getSession().switchTo();
        if (index >= 0) {
            tl.frame(index);
        }
        if (StringUtils.isNotBlank(name)) {
            tl.frame(name);
        }
        if (top != null && top) {
            tl.defaultContent();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Boolean getTop() {
        return top;
    }

    public void setTop(Boolean top) {
        this.top = top;
    }

}
