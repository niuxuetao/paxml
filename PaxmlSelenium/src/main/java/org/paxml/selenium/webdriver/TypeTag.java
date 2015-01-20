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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebElement;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The type tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "type")
public class TypeTag extends SelectableTag {
    private static final Log log = LogFactory.getLog(TypeTag.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        Object value = getValue();
        if (value == null) {
            if (log.isInfoEnabled()) {
                log.info("Clearing element values with selector " + getSelector());
            }
            for (WebElement ele : findElements(null)) {
                ele.clear();
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Typing: " + getValue() + " in elements with selector " + getSelector());
            }
            for (WebElement ele : findElements(null)) {
                ele.sendKeys(value.toString());
            }
        }

        return null;
    }

}
