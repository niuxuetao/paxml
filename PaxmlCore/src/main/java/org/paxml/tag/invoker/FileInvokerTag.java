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

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;

/**
 * The invoker impl for file tags.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(factory = FileInvokerTagFactory.class)
public class FileInvokerTag extends AbstractInvokerTag {
    private PaxmlResource targetResource;

    public PaxmlResource getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(PaxmlResource targetResource) {
        this.targetResource = targetResource;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object invoke(Context subContext) throws Exception {

        // the target resource should have been parsed during "factory time",
        // just find it with no parser context
        IEntity paxml = getResourceLocator().getEntity(targetResource.getName(), null);
        if (paxml != null) {
            final Object existingResult = subContext.getInvocationResult();

            Object result = paxml.execute(subContext);

            subContext.setInvocationResult(existingResult);

            return result;

        } else {
            throw new PaxmlRuntimeException("Target resource not found: " + targetResource.getName());
        }

    }

}
