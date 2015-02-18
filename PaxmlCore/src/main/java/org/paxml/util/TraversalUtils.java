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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The utils for traversing collections.
 * 
 * @author Xuetao Niu
 * 
 */
public class TraversalUtils {
	/**
	 * Make a list of maps out of a map of lists. The given map is treated as a
	 * grid, where the row index is the map key. The combination algorithm is to
	 * grab an item from each row of the grid to make a map, where each result
	 * map is contains that chosen item associated with the key. For instance,
	 * map: {a:[1,2],b:[3,4]} becomes list
	 * [{a:1,b:3},{a:1,b:4},{a:2,b:3},{a:2,b:4}].
	 * 
	 * @param map
	 *            the map of lists
	 * @return the list of maps
	 */
	public static <K, V> List<Map<K, V>> combination(Map<K, List<V>> map) {
		List<K> keys = new ArrayList<K>(map.size());
		List<List<V>> grid = new ArrayList<List<V>>(map.size());
		for (Map.Entry entry : map.entrySet()) {
			keys.add((K) entry.getKey());
			List<V> row = (List<V>) entry.getValue();
			if (row == null) {
				row = new ArrayList<V>();
				row.add(null);
			}
			grid.add(row);
		}
		return combine(keys.size() - 1, keys, grid);
	}

	private static <K, V> List<Map<K, V>> combine(int rowIndex, List<K> keys, List<List<V>> grid) {

		List<V> row = grid.get(rowIndex);
		K key = keys.get(rowIndex);

		List<Map<K, V>> result = new ArrayList();
		if (rowIndex <= 0) {
			// reached the 1st row, create new maps
			for (V val : row) {
				Map<K, V> map = new LinkedHashMap();
				map.put(key, val);
				result.add(map);
			}

		} else {
			// not to the 1st row yet
			for (V val : row) {
				List<Map<K, V>> created = combine(rowIndex - 1, keys, grid);
				for (Map<K, V> map : created) {
					map.put(key, val);
				}
				result.addAll(created);
			}

		}
		return result;
	}
}
