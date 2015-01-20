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
package org.paxml.tag;

import javax.xml.namespace.QName;

import org.paxml.core.Context;
import org.paxml.el.IExpression;

/**
 * The class holding info for id expression.
 * 
 * @author Xuetao Niu
 * 
 */
public class IdExpression {
    private final IExpression expression;
    private final QName attribute;

    /**
     * Create from basic info.
     * 
     * @param expression
     *            the id expression.
     * @param attribute
     *            the attribute of the xml element 
     * 
     */
    public IdExpression(final IExpression expression, final QName attribute) {
        super();
        this.expression = expression;
        this.attribute = attribute;
    }

    public IExpression getExpression() {
        return expression;
    }

    public QName getAttribute() {
        return attribute;
    }

    /**
     * Evaluate the id value.
     * 
     * @param context
     *            the context
     * @return the id value
     */
    public String getId(Context context) {
        return expression.evaluateString(context);
    }
}
