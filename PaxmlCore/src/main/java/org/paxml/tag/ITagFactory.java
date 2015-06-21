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

import org.apache.axiom.om.OMElement;
import org.paxml.core.IParserContext;
/**
 * Prototype for tag factories.
 * @author Xuetao Niu
 *
 * @param <T> the type of the tag that is to be produced by this factory.
 */
public interface ITagFactory<T extends ITag> {
      
    /**
     * Create the tag impl object from parse context.
     * @param tagImplClass the tag impl class
     * @param context the parse context
     * @return the tag object, never null
     */
    //TagCreationResult<T> create(Class<? extends T> tagImplClass, IParserContext context);
    
    TagCreationResult<T> create(OMElement ele, IParserContext context);
    
    
    
}
