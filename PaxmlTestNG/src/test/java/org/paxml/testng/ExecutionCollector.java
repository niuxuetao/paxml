/**
 * This file is part of PaxmlTestNG.
 *
 * PaxmlTestNG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlTestNG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlTestNG.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.testng;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ExecutionCollector {
    private static final Map<String, Integer> files = new HashMap<String, Integer>();

    public static synchronized Map<String, Integer> getExecutedFiles() {
        return new HashMap<String, Integer>(files);
    }

    public static synchronized String runFile(String file) {
        Integer count = files.get(file);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }

        files.put(file, count);
        return file + ":" + count;
    }
    
    @Test
    public void verify() {
        Map<String, Integer> expected = new HashMap<String, Integer>();        
        expected.put("testfile4", 4);
        expected.put("3testfile", 10);
        expected.put("testfile2", 2);
        expected.put("testfile1", 1);
        Assert.assertEquals(getExecutedFiles(), expected);
    }
}
