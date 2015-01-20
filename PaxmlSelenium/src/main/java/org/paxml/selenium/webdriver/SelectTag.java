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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The select tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "select")
public class SelectTag extends SelectableTag {
    private String text;
    private int index = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {

        for (WebElement ele : findElements(null)) {
            if (!"select".equalsIgnoreCase(ele.getTagName())) {
                throw new RuntimeException("Element is not a <select> tag with selector " + getSelector()
                        + ", element is instead: " + ele);
            }
            Select dropDown = new Select(ele);
            if (text != null) {
                dropDown.selectByVisibleText(text);
            } else if (index >= 0) {
                dropDown.selectByIndex(index);
            } else if (getValue() != null) {
                dropDown.selectByValue(getValue().toString());
            } else {
                dropDown.deselectAll();
            }
        }

        return null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}