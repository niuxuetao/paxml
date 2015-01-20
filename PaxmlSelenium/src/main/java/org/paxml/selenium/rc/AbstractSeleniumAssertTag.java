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

import org.paxml.assertion.AssertTag;
import org.paxml.core.Context;

/**
 * Abstract impl of selenium assert tags.
 * 
 */
public abstract class AbstractSeleniumAssertTag extends LocatorTag {
    private boolean trim = true;
    private Object expected;

    /**
     * Get the actual value.
     * 
     * @param context
     *            the context
     * @return the actual value
     */
    abstract protected Object getActualValue(Context context);
    
    /**
     * Get default message if not given by the tag.
     * @param context the context
     * @param expected the expected value, after trimming if trimmed
     * @param actual the actual value, after trimming if trimmed
     * @return the message
     */
    abstract protected String getDefaultMessage(Context context, Object expected, Object actual);

    /**
     * Selects the text of the element specified by {@link #getLocator()} and
     * compares against what is returned by {@link #getValue()}.
     * 
     * @param context
     *            The context.
     * @return null.
     */
    @Override
    protected Object onCommand(Context context) {

        final Object actual = getActualValue(context);

        String actualStr = String.valueOf(actual);
        if (trim) {
            actualStr = actualStr.trim();
        }
        String expectedStr = String.valueOf(expected);
        if (trim) {
            expectedStr = expectedStr.trim();
        }
        final Object value = getValue();

        AssertTag.doAssertion(actualStr, expectedStr, false,
                value == null ? getDefaultMessage(context, expectedStr, actualStr) : value.toString(), false);

        return null;
    }

    public Object getExpected() {
        return expected;
    }

    public void setExpected(Object expected) {
        this.expected = expected;
    }

    public boolean isTrim() {
        return trim;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

}
