/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.assertion;

import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;

/**
 * Assert tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "assert")
public class AssertTag extends BeanTag {
    /**
     * Assertion exception.
     * 
     * @author Xuetao Niu
     * 
     */
    public static class AssertTagException extends RuntimeException {

        /**
         * Construct from factors.
         * 
         * @param message
         *            the message
         * 
         */
        public AssertTagException(final String message) {
            super(message);

        }

    }

    private Object expected;
    private Object actual;
    private boolean negate;

    private boolean strict = false;

    public static void doAssertion(Object actual, Object expected, boolean negate, Object message, boolean strict) {
        if (!strict) {
            actual = actual == null ? null : actual + "";
            expected = expected == null ? null : expected + "";
        }
        final boolean same;
        if (actual == null || expected == null) {
            same = actual == expected;
        } else {
            same = actual.equals(expected);
        }
        if ((!same && !negate) || (same && negate)) {

            throw new AssertTagException(message == null ? "Expected " + (negate ? "not" : "") + ": " + expected
                    + ", but got: " + actual : message.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        doAssertion(actual, expected, negate, getValue(), strict);
        return null;
    }

    public Object getExpected() {
        return expected;
    }

    public void setExpected(Object expected) {
        this.expected = expected;
    }

    public Object getActual() {
        return actual;
    }

    public void setActual(Object actual) {
        this.actual = actual;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    protected boolean isNegate() {
        return negate;
    }

    protected void setNegate(boolean negate) {
        this.negate = negate;
    }

}
