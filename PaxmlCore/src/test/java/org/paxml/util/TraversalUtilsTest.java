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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class TraversalUtilsTest {
	@Test
	public void testCombination1() {
		Map<String, List<Integer>> map = new LinkedHashMap();
		map.put("a", Arrays.asList(1, 2));
		map.put("b", Arrays.asList(10));
		map.put("c", Arrays.asList(100, 200, 300));

		List<Map<String, Integer>> results = TraversalUtils.combination(map);
		System.out.println(results);

		Assert.assertEquals(map(1, 10, 100), results.get(0));
		Assert.assertEquals(map(2, 10, 100), results.get(1));
		Assert.assertEquals(map(1, 10, 200), results.get(2));
		Assert.assertEquals(map(2, 10, 200), results.get(3));
		Assert.assertEquals(map(1, 10, 300), results.get(4));
		Assert.assertEquals(map(2, 10, 300), results.get(5));

		Assert.assertEquals(6, results.size());
	}

	@Test
	public void testCombination2() {
		Map<String, List<Integer>> map = new LinkedHashMap();
		map.put("a", Arrays.asList(1, 2));
		map.put("b", Arrays.asList(10));

		List<Map<String, Integer>> results = TraversalUtils.combination(map);
		System.out.println(results);

		Assert.assertEquals(map(1, 10), results.get(0));
		Assert.assertEquals(map(2, 10), results.get(1));

		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testCombination3() {
		Map<String, List<Integer>> map = new LinkedHashMap();
		map.put("a", Arrays.asList(1, 2));

		List<Map<String, Integer>> results = TraversalUtils.combination(map);
		System.out.println(results);

		Assert.assertEquals(map(1), results.get(0));
		Assert.assertEquals(map(2), results.get(1));

		Assert.assertEquals(2, results.size());
	}

	@Test
	public void testCombination4() {
		Map<String, List<Integer>> map = new LinkedHashMap();
		map.put("a", Arrays.asList(1));

		List<Map<String, Integer>> results = TraversalUtils.combination(map);
		System.out.println(results);

		Assert.assertEquals(map(1), results.get(0));

		Assert.assertEquals(1, results.size());
	}

	@Test
	public void testCombination5() {
		Map<String, List<Integer>> map = new LinkedHashMap();
		map.put("a", Arrays.asList(1));
		map.put("b", null);

		List<Map<String, Integer>> results = TraversalUtils.combination(map);
		System.out.println(results);

		Assert.assertEquals(map(1, null), results.get(0));

		Assert.assertEquals(1, results.size());
	}

	@Test
	public void testCombination6() {
		Map<String, List<Integer>> map = new LinkedHashMap();
		map.put("a", Arrays.asList(1, 2));
		map.put("b", null);

		List<Map<String, Integer>> results = TraversalUtils.combination(map);
		System.out.println(results);

		Assert.assertEquals(map(1, null), results.get(0));
		Assert.assertEquals(map(2, null), results.get(1));

		Assert.assertEquals(2, results.size());
	}

	private Map<String, Integer> map(Integer... vals) {
		Map<String, Integer> map = new LinkedHashMap();
		for (int i = 0; i < vals.length; i++) {
			map.put("" + (char) ('a' + i), vals[i]);
		}
		return map;
	}
}
