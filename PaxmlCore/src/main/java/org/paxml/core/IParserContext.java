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

import java.util.LinkedList;

import org.apache.axiom.om.OMElement;
import org.paxml.tag.ITag;
/**
 * The prototype for parser context.
 * @author Xuetao Niu
 *
 */
public interface IParserContext {
    /**
     * The current xml element being parsed.
     * @return the xml element
     */
    OMElement getElement();
    /**
     * The current paxml resource being parsed.
     * @return the resource
     */
    PaxmlResource getResource();
    /**
     * The paxml resource locator of the parsing context.
     * @return the locator
     */
    ResourceLocator getLocator();
    /**
     * The paxml parser of the parsing context.
     * @return the parser
     */
    Parser getParser();
    /**
     * The current paxml entity being parsed.
     * @return the paxml entity. null if the entity is not constructed yet.
     */
    IEntity getEntity();
    /**
     * The current tag's parent tag.
     * @return parent tag, never null
     */
    ITag getParentTag();
    /**
     * The parser context stack.
     * @return parser context stack, never null
     */
    LinkedList<IParserContext> getParserStack();
    
    /**
     * Drop the resources acquired by the context.
     */
    void discard();
    
}   
