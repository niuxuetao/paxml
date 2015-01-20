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
import org.paxml.annotation.Tag;
import org.paxml.assertion.AssertTag.AssertTagException;

/**
 * Tag that asserts elements' visibility.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "assertDisplayed")
public class AssertDisplayedTag extends AssertElementTag {

    @Override
    protected void doAssertion(WebElement ele) {
        if (!ele.isDisplayed()) {
            throw new AssertTagException("Element " + getSelector() + " is not visible: " + ele);
        }
    }

}
