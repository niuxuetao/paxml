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

import java.util.Iterator;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;

public class RangedIterator<T> extends AbstractIteratorDecorator {
	private int index;
	private final int to;

	public RangedIterator(int from, int to, Iterator<T> it) {
		super(it);
		for (int i = 0; i < from && it.hasNext(); i++) {
			it.next();
		}
		index = from;
		this.to = to;
	}

	@Override
	public boolean hasNext() {

		return index <= to && super.hasNext();
	}

	@Override
	public T next() {
		T t = (T) super.next();
		index++;
		return t;
	}

}
