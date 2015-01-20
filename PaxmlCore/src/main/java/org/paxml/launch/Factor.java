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
package org.paxml.launch;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The factor of a launch.
 * 
 * @author Xuetao Niu
 * 
 */
public class Factor {

    private final Set<Object> values = Collections.synchronizedSet(new LinkedHashSet<Object>());
    private volatile String name;
    private volatile boolean mergeGlobal;
    public Set<Object> getValues() {
        return values;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return values.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isMergeGlobal() {
        return mergeGlobal;
    }
    public void setMergeGlobal(boolean mergeGlobal) {
        this.mergeGlobal = mergeGlobal;
    }

}
