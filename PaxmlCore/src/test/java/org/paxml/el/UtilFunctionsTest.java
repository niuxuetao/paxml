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
package org.paxml.el;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.paxml.el.UtilFunctions;

public class UtilFunctionsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public  void testFormatString_1String() throws Exception {

        String formattedString = "String {0}";
        String [] replaceables = {"String"};
        String stringReplaceables = "String";
        String result = UtilFunctions.formatString(formattedString, replaceables);

        assertEquals("String String" , result);
    }

    @Test
    public  void testFormatString_2Strings() throws Exception {

        String formattedString = "String {0} {1}";
        String [] replaceables = {"String1", "String2"};

        String result = UtilFunctions.formatString(formattedString, replaceables);

        assertEquals("String String1 String2" , result);
    }

    @Test
    public  void testFormatString_dubbelStrings() throws Exception {

        String formattedString = "String {0} {0}";
        String [] replaceables = {"String"};
        String result = UtilFunctions.formatString(formattedString, replaceables);

        assertEquals("String String String" , result);
    }

}
