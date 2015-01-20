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

import org.paxml.tag.ITagFactory;

/**
 * Annotation for tag impl class.
 * 
 * @author Xuetao Niu
 * 
 */
@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Tag {
    /**
     * The tag name, defaults to "" meaning not applicable. This could be used
     * on an abstract impl class.
     * 
     */
    String name() default "";

    /**
     * The tag factory class which must be a class with default constructor.
     * This value defaults to ITagFactory.class which is an invalid one.
     * Omitting this value means to search for super class or implemented
     * interfaces of the annotated class for the factory declerartion.
     * 
     */
    Class<? extends ITagFactory> factory() default ITagFactory.class;
    
}
