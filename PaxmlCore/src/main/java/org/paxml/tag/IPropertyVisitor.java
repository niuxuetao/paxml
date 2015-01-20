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
package org.paxml.tag;
/**
 * The property visitor for an object.
 * @author Xuetao Niu
 *
 * @param <T> the type context object for the visiting process.
 * @param <R> the visitation result type
 */
public interface IPropertyVisitor<T,R> {
    /**
     * Event handler for visiting a property of an object.
     * @param context the context, can be null if not needed
     * @param obj the object
     * @param propertyName the property name 
     * @param index the index of the property
     * @param propertyValue the property value
     * 
     * @return the visitation result which is dealt with by caller
     */
    R visit(T context, Object obj, Object propertyName, int index, Object propertyValue);
}
