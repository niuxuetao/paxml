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
package org.paxml.core;

import java.util.List;

/**
 * Prototype for value container.
 * 
 * @author Xuetao Niu
 * 
 */
public interface IObjectContainer {
    /**
     * Make a deep copy of the current container.
     * 
     * @return a different container that is of the same class, with the values
     *         deeply copied. Deeply copied means all IObjectContainer members
     *         and members of members are copied, but non-IObjectContainer members
     *         are just referred to, not cloned.
     */
    IObjectContainer copy();
    String getId();
    void setId(String id);
    void addValue(String key, Object value);
    Object shrink();
    List<Object> getList();
    String toXml(String rootName);
    String toJson();
    void fromXml(String xml);
    void fromJson(String json);
}
