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

/**
 * Definition for an paxml entity.
 * 
 * @author Xuetao Niu
 * 
 */
public interface IEntity {
    /**
     * Execute the entity.
     * 
     * @param context
     *            execution context
     * @return the result of execution
     */
    Object execute(Context context);

    /**
     * Get the resource from which the entity is created.
     * 
     * @return the resource
     */
    PaxmlResource getResource();
    
    /**
     * Print the tree structure of the entity in xml format.
     * @param indent the initial indentation
     * @return the xml string
     */
    String printTree(int indent);
    
    /**
     * Tells if the entity is cachable.
     * @return true being cachable, false not
     */
    boolean isCachable();
    /**
     * Tells if the entity is changed after it's parsed.
     * @return true yes, false not.
     */
    boolean isModified();
}
