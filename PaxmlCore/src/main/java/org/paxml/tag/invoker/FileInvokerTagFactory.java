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
package org.paxml.tag.invoker;

import org.paxml.core.IEntity;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.AbstractPaxmlEntityFactory;

/**
 * File invoker factory.
 * 
 * @author Xuetao Niu
 * 
 */
public class FileInvokerTagFactory extends InvokerTagFactory<FileInvokerTag> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final FileInvokerTag tag, IParserContext context) {

        super.populate(tag, context);

        PaxmlResource targetResource = getTargetResource(tag, context);

        tag.setTargetResource(targetResource);

        // make sure the target file is parsed and cached, so that at execution
        // time, the parser context can simply be passed as null.
        if (targetResource != null) {
            IParserContext newContext = AbstractPaxmlEntityFactory.createTargetParserContext(context, targetResource);
            getTargetEntity(tag, newContext);
            newContext.discard();
        } else {
            throw new PaxmlRuntimeException("Target resource not found");
        }

        return false;
    }

    /**
     * Get target entity from context.
     * 
     * @param tag
     *            the tag
     * @param context
     *            the context
     * @return target entity, never null
     */
    protected IEntity getTargetEntity(FileInvokerTag tag, IParserContext context) {
        IEntity target = context.getLocator().getParser().parse(context.getResource(), false, context);
        if (target == null) {
            throw new PaxmlRuntimeException("Unknown target resource: " + context.getResource());
        }
        return target;
    }

    /**
     * Get target resource from context.
     * 
     * @param tag
     *            the tag
     * @param context
     *            the context
     * @return the resource, null if not found.
     */
    protected PaxmlResource getTargetResource(FileInvokerTag tag, IParserContext context) {
        return context.getLocator().getResource(context.getElement().getLocalName());
    }

}
