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

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.paxml.util.DBUtils;

public class SqlTagTest {
    @Test
    public void testBreakLines() {
        
        List<String> lines=Arrays.asList(
                "--; select",
                "create table1();",
                " ",
                " ",
                "",
                "create table2() ; ",
                " ",
                " -- aksdashdg;",
                " update xxx ;--what is that;",                
                "");
            
        List<String> broken = DBUtils.breakSql(StringUtils.join(lines, '\n'));
        
        Assert.assertEquals(Arrays.asList(
                "create table1()",
                "create table2()",
                "update xxx ;--what is that"
                ), broken);
    }

}
