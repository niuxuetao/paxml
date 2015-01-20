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
package org.paxml.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;
import org.paxml.util.ReflectUtils;

public class ReflectUtilsTest {
	@Test
	public void testCoerceType() {

		doTestCoerceType(null, Class.class, null);
		doTestCoerceType(1, String.class, "1");
		doTestCoerceType(1, Integer.class, 1);
		doTestCoerceType(1, BigDecimal.class, new BigDecimal(1));
		doTestCoerceType("1.1", double.class, 1.1d);

		doTestCoerceType(Arrays.asList(1, 2), ArrayList.class, Arrays.asList(1, 2));

		doTestCoerceType(new LinkedHashSet(Arrays.asList(1, 2)), ArrayList.class, Arrays.asList(1, 2));

		doTestCoerceType(Arrays.asList(1, 2).iterator(), Vector.class, new Vector(Arrays.asList(1, 2)));

		Map map = new LinkedHashMap();
		map.put(1, "x");
		map.put(2, "y");
		doTestCoerceType(map, LinkedHashSet.class, new LinkedHashSet(Arrays.asList("x", "y")));
	}

	private void doTestCoerceType(Object from, Class targetClass, Object expected) {
		Object result = ReflectUtils.coerceType(from, targetClass);

		Assert.assertEquals("cannot forward convert", expected, result);

		if (from != null && !ReflectUtils.isImplementingClass(targetClass, Iterable.class, true)) {
			result = ReflectUtils.coerceType(expected, from.getClass());
			Assert.assertEquals("cannot backward convert", from, result);
		}
	}
}
