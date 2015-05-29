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
import org.openqa.selenium.WebElement;
import org.paxml.annotation.Tag;
import org.paxml.assertion.AssertTag.AssertTagException;

/**
 * Tag that verifies the value attribute of an element.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "assertValue")
public class AssertValueTag extends AssertElementTag {

    @Override
    protected void doAssertion(WebElement ele) {
        String valueString = ele.getAttribute("value");
        Object expected = getValue();
        if (StringUtils.isEmpty(valueString)) {
            if (expected != null && expected.toString().length() > 0) {
                throw new AssertTagException("Expected value is '" + expected + "' but got '" + valueString
                        + "' with selector " + getSelector() + " on element: " + ele);
            }
        } else {
            if (expected == null) {
                throw new AssertTagException("Excepts no value but got '" + valueString + "' with selector "
                        + getSelector() + " on element: " + ele);
            }
            String expectedString = expected.toString();
            if (!valueString.equals(expectedString)) {
                throw new AssertTagException("Expected value is '" + expectedString + "' but got '" + valueString
                        + "' with selector " + getSelector() + " on element: " + ele);
            }

        }
    }

}
