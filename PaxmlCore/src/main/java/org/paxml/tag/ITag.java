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

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.PaxmlResource;
import org.paxml.core.ResourceLocator;
import org.paxml.el.Condition;

/**
 * Prototype for tag.
 * 
 * @author Xuetao Niu
 * 
 */
public interface ITag {
    /**
     * Get a list of children tags.
     * @return the list
     */
    List<ITag> getChildren();
    /**
     * Add a child tag.
     * @param tag the child
     */
    void addChild(ITag tag);
    /**
     * Execute the tag.
     * @param context the execution context
     * @return the execution result
     */
    Object execute(Context context);
    /**
     * Get the tag name expressed in xml.
     * @return the tag name
     */
    String getTagName();
    /**
     * Set the xml tag name of the tag.
     * @param tagName the tag name
     */
    void setTagName(String tagName);
    /**
     * Get the parent tag.
     * @return the parent tag, null if it has no parent.
     */
    ITag getParent();
    /**
     * Set parent tag.
     * @param parent the parent tag.
     */
    void setParent(ITag parent);
    /**
     * Get the line number of the tag that is written in xml.
     * @return the line number. 0 if unknown.
     */
    int getLineNumber();
    /**
     * Set the line number of the tag.
     * @param num the line number
     */
    void setLineNumber(int num);
    /**
     * Get the paxml resource from which the tag is created.
     * @return the paxml resource, never null
     */
    PaxmlResource getResource();
    /**
     * Set the paxml resource from which the tag is created.
     * @param resource the paxml resource
     */
    void setResource(PaxmlResource resource);
    /**
     * Set the tag factory from which the tag is created.
     * @param factory the tag factory
     */
    void setFactory(ITagFactory<? extends ITag> factory);
    /**
     * Get the tag factory from which the tag is created.
     * @return the tag factory, never null.
     */
    ITagFactory<? extends ITag> getFactory();
    /**
     * Get the enclosing paxml entity of the tag.
     * @return the entity, never null.
     */
    IEntity getEntity();
    /**
     * Set the enclosing paxml entity of the tag.
     * @param entity the entity, cannot be null.
     */
    void setEntity(IEntity entity);
    /**
     * Get the id expression.
     * @return the id expression, null if no id given
     */
    IdExpression getIdExpression();
    /**
     * Set the id expression.
     * @param idExpression the id expression
     */
    void setIdExpression(IdExpression idExpression);
    /**
     * Get the associated resource locator.
     * @return the locator
     */
    ResourceLocator getResourceLocator();     
    /**
     * Set the associated resource locator.
     * @param resourceLocator the locator
     */
    void setResourceLocator(ResourceLocator resourceLocator);
    /**
     * Get the conditional construct.
     * @return the construct
     */
    Condition getCondition();
    /**
     * Set the conditional construct.
     * @param condition the construct
     */
    void setCondition(Condition condition);
    /**
     * Get the xml element of this tag.
     * @return the xml element
     */
    OMElement getXmlElement();
}
