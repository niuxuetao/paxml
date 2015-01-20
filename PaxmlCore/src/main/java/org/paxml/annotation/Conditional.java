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
 * The annotation for a conditional tag.
 * 
 * @author Xuetao Niu
 * 
 */
@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Conditional {
    /**
     * The value if a condition is not supported.
     */
    String NOT_SUPPORTED = "";
    /**
     * Default attribute name for the "positive" test.
     */
    String DEFAULT_IF_ATTRIBUTE = "if";
    /**
     * Default attribute name for the "negative" test.
     */
    String DEFAULT_UNLESS_ATTRIBUTE = "unless";
    /**
     * 
     * The "positive" test attribute name, defaults to "if". Set to empty string if this attribute is not supported.
     */
    String ifAttribute() default DEFAULT_IF_ATTRIBUTE;
    /**
     * 
     * The "negative" test attribute name, defaults to "unless". Set to empty string if this attribute is not supported.
     */
    String unlessAttribute() default DEFAULT_UNLESS_ATTRIBUTE;
}
