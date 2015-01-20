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

import java.util.LinkedList;

import org.paxml.core.Context;

/**
 * Expression impl that does string concatenation of sub expressions.
 * 
 * @author Xuetao Niu
 * 
 */
public class ConcatExpression extends AbstractExpression {
    private final LinkedList<IExpression> parts = new LinkedList<IExpression>();

    /**
     * {@inheritDoc}
     * 
     * @return null if no sub expressions are added; the actual evaluation
     *         result of the 1st sub expression if it has only one expression;
     *         the string concatenation of all sub expressions.
     */
    public Object evaluate(Context context) {
        final int size = parts.size();
        if (size <= 0) {
            return null;
        } else if (size == 1) {
            return parts.getFirst().evaluate(context);
        }
        StringBuilder sb = new StringBuilder();
        for (IExpression exp : parts) {
            sb.append(exp.evaluateString(context));
        }
        return sb.toString();
    }

    /**
     * Add a sub expression.
     * 
     * @param exp
     *            the sub expression
     */
    public void addPart(IExpression exp) {
        parts.addLast(exp);
    }

    /**
     * {@inheritDoc} Show parts in a list manner.
     */
    public String getString() {
        return parts.toString();
    }

}
