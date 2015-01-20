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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;

public class CachedIterator<T> extends AbstractIteratorDecorator {

	private int cacheSize;

	public CachedIterator(int cacheSize, Iterator iterator) {
		super(iterator);

		setCacheSize(cacheSize);
	}

	public int getCacheSize() {
		return cacheSize;
	}

	@Override
	public List<T> next() {
		List<T> r = new ArrayList<T>(cacheSize);
		for (int i = 0; i < cacheSize && super.hasNext(); i++) {
			r.add((T) super.next());
		}
		return r;

	}

	public void setCacheSize(int cacheSize) {
		if (cacheSize < 1) {
			cacheSize = 1;
		}
		this.cacheSize = cacheSize;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported");
	}

}
