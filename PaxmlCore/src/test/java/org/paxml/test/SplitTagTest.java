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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.paxml.bean.SplitTag;

public class SplitTagTest {
	@Test
	public void testCapitalSplit() {

		doTestCapitalSplit("thisIsMe", "this", "Is", "Me");

		doTestCapitalSplit("ThisIsMe", "This", "Is", "Me");

		doTestCapitalSplit("ThisDIYMe", "This", "DIY", "Me");

		doTestCapitalSplit("ThisDIY", "This", "DIY");

		doTestCapitalSplit("this", "this");

		doTestCapitalSplit("This", "This");

		doTestCapitalSplit("DIY", "DIY");
		
		doTestCapitalSplit("DIYthis", "DI","Ythis");
		
		doTestCapitalSplit("DIYThis", "DIY","This");
		
		doTestCapitalSplit("DIY1", "DIY1");
		
		doTestCapitalSplit("DIY1e", "DI","Y1e");
		
		doTestCapitalSplit("D1", "D1");
		
		doTestCapitalSplit("d1", "d1");
		
		doTestCapitalSplit("This1DIY", "This1","DIY");
		
		doTestCapitalSplit("This1DIY2", "This1","DIY2");
		
		doTestCapitalSplit("This1DIY2x", "This1","DI","Y2x");
		
	}

	private void doTestCapitalSplit(String from, String... to) {
		List<String> list = new ArrayList<String>();
		SplitTag.splitByCapital(from, list);
		Assert.assertEquals(to.length, list.size());
		for (int i = 0; i < to.length; i++) {
			Assert.assertEquals(to[i], list.get(i));
		}
	}
}
