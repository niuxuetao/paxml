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
import org.paxml.assertion.AssertTag;
import org.paxml.core.Context;

/**
 * Tag that asserts element's non presence.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "assertNotPresent")
public class AssertNotPresentTag extends LocatorTag {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        final String locator = getLocator();
        final Object value = getValue();
        AssertTag.doAssertion(getSeleniumUtils(context).isPresent(locator), true, true,
                value == null ? "Expect element not present, but it is: " + locator : value.toString(), false);

        return null;
    }
    
    
}
