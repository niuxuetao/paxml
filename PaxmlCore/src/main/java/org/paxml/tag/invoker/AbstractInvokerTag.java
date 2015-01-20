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

import org.paxml.annotation.Conditional;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.tag.AbstractTag;

/**
 * This is the root impl for all invoker classes.
 * 
 * @author Xuetao Niu
 * 
 */
@Conditional
@Tag(factory = InvokerTagFactory.class)
public abstract class AbstractInvokerTag extends AbstractTag {

    /**
     * 
     * {@inheritDoc} Makes a new context and pass on.
     * 
     */
    @Override
    protected Object doExecute(Context context) throws Exception {
        Context subContext = new Context(context);
        subContext.setAsCurrentThreadContext();
        try {
            
            // let the parameters put them into the new context
            executeChildren(subContext);

            // invoke the target scenario with the new context

            final Object result = invoke(subContext);
            
            return result;
        
        } finally {                     
            context.setAsCurrentThreadContext();           
        }
    }

    /**
     * Will be invoked if getTarget() returns this.
     * 
     * @param context
     *            the execution context
     * @return the execution result
     * @throws Exception
     *             any exception
     */
    protected abstract Object invoke(Context context) throws Exception;

}
