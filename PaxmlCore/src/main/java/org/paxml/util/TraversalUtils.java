package org.paxml.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TraversalUtils {
	
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
		return combine(keys.size()-1, keys, grid);
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
