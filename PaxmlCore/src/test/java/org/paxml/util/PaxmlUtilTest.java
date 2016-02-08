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

import java.util.regex.Pattern;

import org.junit.Test;

import junit.framework.Assert;

public class PaxmlUtilTest {
	@Test
	public void testGlobTranslate(){
		testMatch("*","", true);
		testMatch("*","blabla", true);
		testMatch("a*c","asdifksdfkhc", true);
		testMatch("a*","blabla", false);
		testMatch("?","b", true);
		testMatch("?","ba", false);
		testMatch("b?","bax", false);
		testMatch("b?","ba", true);
		testMatch("b??x","banx", true);
		
		testMatch("b??x*","b\\ax", true);
		
	}
	private void testMatch(String glob, String str, boolean  match){
		String regex=PaxmlUtils.createRegexFromGlob(glob);
		Assert.assertEquals(match,Pattern.compile(regex).matcher(str).matches());
	}
}
