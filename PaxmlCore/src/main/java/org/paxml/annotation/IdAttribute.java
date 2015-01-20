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
package org.paxml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for a tag that has id expression support. Do not annotate a
 * class or interface if the id support should not be given. Make the value
 * empty string to cancel the support if a class's super class or interface
 * class already gets annotated.
 * 
 * @author Xuetao Niu
 * 
 */
@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface IdAttribute {
    /**
     * Default attribute name for the "id" attribute.
     */
    String DEFAULT_VALUE = "id";
    /**
     * The value if id support should be removed.
     */
    String NO_ID_SUPPORT = "";

    /**
     * 
     * The "positive" test attribute name, defaults to "if". Set to empty string
     * if this attribute is not supported.
     */
    String value() default DEFAULT_VALUE;

}
