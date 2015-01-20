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

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.paxml.tag.sql.DdlScript;

public class DdlScriptTest {
    @Test
    public void testOrdering() {
        Assert.assertEquals(0, doCompare("xxx-create-ddl-1.0.0.sql", "xxx-create-ddl-1.0.0.sql"));
        Assert.assertEquals(0, doCompare("yyy-create-ddl-1.0.0.sql", "xxx-create-ddl-1.0.0.sql"));
        Assert.assertEquals(-1, doCompare("xxx-create-ddl-1.0.0.sql", "xxx-create-data-1.0.0.sql"));
        Assert.assertEquals(1, doCompare("xxx-update-ddl-1.0.1.sql", "xxx-update-data-1.0.0.sql"));
        Assert.assertEquals(1, doCompare("xxx-update-ddl-1.0.11.sql", "xxx-update-ddl-1.0.2.sql"));
        Assert.assertEquals(1, doCompare("xxx-update-ddl-1.0.11.sql", "xxx-update-ddl-1.0.sql"));
        Assert.assertEquals(1, doCompare("xxx-update-ddl-1.0.11.sql", "xxx-update-data-1.0.sql"));
        Assert.assertEquals(1, doCompare("xxx-create-ddl-2.0.0.sql", "xxx-update-data-1.0.9999.sql"));
    }

    private int doCompare(String f1, String f2) {
        return new DdlScript(new File("."), f1).compareTo(new DdlScript(new File("."), f2));
    }
}
