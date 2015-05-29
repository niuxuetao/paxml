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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.assertion.AssertTag;
import org.paxml.core.Context;

/**
 * The type tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "type")
public class TypeTag extends LocatorTag {
    private static final Log log = LogFactory.getLog(TypeTag.class);
    private String expected;
    
    public TypeTag(){
        super();
        setWaitTill(VISIBLE);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommandAfterWait(Context context) {
        if (log.isDebugEnabled()) {
            log.debug("Typing: " + getValue() + " in " + getLocator());
        }

        Object value = getValue();
        if (value == null) {
            value = "";
        } else if (value instanceof List) {
            value = StringUtils.join((List) value, "\r\n");
        }
        final String str = value.toString();
        final SeleniumUtils selenium = getSeleniumUtils(context);
        final String locator = getLocator();
        selenium.type(locator, str);
        if (expected != null) {
            if (log.isDebugEnabled()) {
                log.debug("Verifying type result to be: " + expected);
            }
            AssertTag.doAssertion(selenium.getValue(locator), expected, false, null, false);
        }
        return null;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

}
