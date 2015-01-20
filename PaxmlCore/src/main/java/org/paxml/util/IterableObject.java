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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Converts any object into iterable.
 * 
 * @author Xuetao Niu
 * 
 *            the type of iterable elements.
 */
public class IterableObject implements Iterable<Object> {
    private final Object value;

    /**
     * Construct from any value.
     * 
     * @param value
     *            any value, never null
     */
    public IterableObject(final Object value) {
        this.value = value;
    }
    
    private Iterator<Object> getIterator(Object obj) {
    	if(obj instanceof Map){
    		return ((Map)obj).keySet().iterator();
    	}else if (obj instanceof Iterator) {
            return (Iterator) obj;
        } else if (obj instanceof Iterable) {
            return getIterator(((Iterable) obj).iterator());
        } else if (obj instanceof Map) {
            return getIterator(((Map) obj).entrySet().iterator());
        } else if (obj instanceof Enumeration) {
            return getIterator(new IterableEnumeration((Enumeration) obj));
        } else if (obj.getClass().isArray()) {
            return getIterator(Arrays.asList((Object[]) obj));
        } else {
            return getIterator(Arrays.asList(obj));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<Object> iterator() {

        return getIterator(value);
    }

}
