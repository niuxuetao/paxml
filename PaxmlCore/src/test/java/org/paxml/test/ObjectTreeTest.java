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
import org.paxml.core.ObjectTree;

public class ObjectTreeTest {
    @Test
    public void testTraversalOrder() {
        ObjectTree tree = new ObjectTree();
        tree.put("2", 2);
        tree.put("1", 1);
        List<Object> list = new ArrayList<Object>(tree.values());
        Assert.assertEquals(list.get(0), 2);
        Assert.assertEquals(list.get(1), 1);
        // test that the ObjectTree preserves the traversal order the same as
        // the 1st time adding
        tree.put("3", 3);
        tree.put("2", 2);
        list = new ArrayList<Object>(tree.values());
        Assert.assertEquals(list.get(0), 2);
        Assert.assertEquals(list.get(1), 1);
        Assert.assertEquals(list.get(2), 3);

    }
}
