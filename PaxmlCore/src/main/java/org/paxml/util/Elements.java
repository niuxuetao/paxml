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
package org.paxml.util;

import org.apache.axiom.om.OMElement;

/**
 * The iterable of all sub elements.
 * 
 * @author Xuetao Niu
 * 
 */
public class Elements extends IterableIterator<OMElement> {
    /**
     * Construct from owning element.
     * 
     * @param ele
     *            the owning element.
     */
    public Elements(final OMElement ele) {
        this(ele, null);
    }

    /**
     * Construct from owning elements with local name filter.
     * 
     * @param ele
     *            the owning element
     * @param filter
     *            the local name filter
     */
    public Elements(final OMElement ele, final String filter) {
        super(filter == null ? ele.getChildElements() : ele.getChildrenWithLocalName(filter));
    }

}
