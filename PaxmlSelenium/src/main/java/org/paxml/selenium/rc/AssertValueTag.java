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
 * Tag that verifies the value of an element.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "assertValue")
public class AssertValueTag extends AbstractSeleniumAssertTag {

    @Override
    protected Object getActualValue(Context context) {
        return getSeleniumUtils(context).getValue(getLocator());
    }
    
    @Override
    protected String getDefaultMessage(Context context, Object expected, Object actual) {
        return "Expected value '" + expected + "' does not match the actual value '"+actual+"' by locator: " + getLocator();
    }

}
