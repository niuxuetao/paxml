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

import java.util.Collection;

import org.apache.commons.jxpath.ExpressionContext;
import org.apache.commons.jxpath.Pointer;
import org.paxml.core.Context;
import org.paxml.util.ReflectUtils;

/**
 * The xpath functions.
 * 
 * @author Xuetao Niu
 * 
 */
public final class XpathFunctions {

    private XpathFunctions() {

    }

    /**
     * Check if a value matches a java class.
     * 
     * @param context
     *            the xpath context
     * @param className
     *            the class name
     * @param exact
     *            false to consider inheritance, true to only do exact class
     *            match.
     * @return true if matches, false if not match
     */
    public static boolean matchClass(ExpressionContext context, String className, boolean exact) {
        Pointer pointer = context.getContextNodePointer();
        if (pointer == null) {
            return false;
        }
        Object value = pointer.getValue();
        if (value == null) {
            return false;
        }
        if (exact) {
            return ReflectUtils.loadClassStrict(className, null).equals(value.getClass());
        } else {
            return ReflectUtils.loadClassStrict(className, null).isInstance(value);
        }
    }

    /**
     * Check if a value is in a collection.
     * 
     * @param context
     *            the xpath context
     * @param collection
     *            comma delimited string or collection
     * @return true if the comma delimited string is in
     */
    public static boolean in(ExpressionContext context, Object collection) {
        if (collection == null) {
            return false;
        }
        Pointer pointer = context.getContextNodePointer();
        if (pointer == null) {
            return false;
        }
        Object value = pointer.getValue();
        if (value == null) {
            return false;
        }

        if (collection instanceof Collection) {
            return UtilFunctions.in(value, (Collection) collection);
        } else {
            return UtilFunctions.in(value, UtilFunctions.breakString(collection.toString(), null));
        }
    }

    /**
     * Get the class of an object.
     * 
     * @param obj
     *            the obj
     * @return null if passed in null, otherwise, returns the class of the
     *         passed-in object.
     */
    public static Class getClass(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.getClass();
    }

    /**
     * Get the current node.
     * 
     * @param context
     *            the xpath context
     * @return the current node, null if no current node
     */
    public static Object current(ExpressionContext context) {
        Pointer pointer = context.getContextNodePointer();
        if (pointer == null) {
            return null;
        }
        return pointer.getValue();

    }

    /**
     * Get the id of the current node
     * 
     * @param obj
     * @return
     */
    public static String findId(ExpressionContext context) {

        Pointer pointer = context.getContextNodePointer();
        if (pointer == null) {
            return null;
        }
        Object obj = pointer.getValue();
        return Context.getCurrentContext().findConstId(obj, true, true);
    }
}
