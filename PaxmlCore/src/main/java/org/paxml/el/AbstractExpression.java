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
package org.paxml.el;

import org.paxml.core.Context;

/**
 * The abstract impl that provides the default evaluteString() and toString() implementations.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class AbstractExpression implements IExpression {
    /**
     * {@inheritDoc}
     */
    public String evaluateString(Context context) {
        Object obj = evaluate(context);
        String str = obj == null ? null : obj.toString();
        if (str == null && treatNullAsBlank()) {
            str = "";
        }
        return str;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getString();
    }
    /**
     * Method to be overridden by child classes. By default returns false.
     * @return true if null is treated as blank string, false to treat as null.
     */
    protected boolean treatNullAsBlank() {
        return false;
    }
}
