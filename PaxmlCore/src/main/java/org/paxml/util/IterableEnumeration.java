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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Converts an Enumeration into iterable.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the type of iterable elements.
 */
public class IterableEnumeration<T> implements Iterable<T> {

    private final Enumeration<T> en;

    /**
     * Construct from iterator.
     * 
     * @param en
     *            the enumeration, never null
     */
    public IterableEnumeration(final Enumeration<T> en) {
        this.en = en;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            public boolean hasNext() {
                return en.hasMoreElements();
            }

            public T next() {
                return en.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException("Cannot remove from Enumeration");
            }

        };
    }

}
