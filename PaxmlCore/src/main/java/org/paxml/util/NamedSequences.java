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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wrapped logic of a map of atomic long.
 * 
 * @author Xuetao Niu
 * 
 */
public class NamedSequences {

	private final ConcurrentMap<Object, AtomicLong> map = new ConcurrentHashMap<Object, AtomicLong>();
	private final long startWith;

	public NamedSequences() {
		this(0L);
	}

	public NamedSequences(long startWith) {
		this.startWith = startWith;
	}

	public long getNextValue(Object key) {
		return getSequence(key).getAndIncrement();
	}

	public AtomicLong getSequence(Object key) {
		AtomicLong seq = map.get(key);
		if (seq == null) {
			seq = new AtomicLong(startWith);
		}
		AtomicLong oldSeq = map.putIfAbsent(key, seq);
		if (oldSeq != null) {
			seq = oldSeq;
		}
		return seq;
	}

	public AtomicLong deleteSequence(Object key) {
		return map.remove(key);
	}

	public int getSize() {
		return map.size();
	}

	public void clear() {
		map.clear();
	}

	public Set<Object> getKeys() {
		return Collections.unmodifiableSet(map.keySet());
	}
}
