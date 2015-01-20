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
 * Literal expression impl which simply wraps around a string.
 * 
 * @author Xuetao Niu
 * 
 */
public class LiteralExpression extends AbstractExpression {
    private final String literal;

    /**
     * Construct from a string.
     * 
     * @param literal
     *            the string
     */
    public LiteralExpression(final String literal) {
        
        this.literal = literal;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Context context) {
        return literal;
    }

    /**
     * {@inheritDoc}
     */
    public String getString() {
        return literal;
    }

}
