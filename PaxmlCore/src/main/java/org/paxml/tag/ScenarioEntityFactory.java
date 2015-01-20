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
import org.paxml.annotation.RootTag;
import org.paxml.core.Context;
import org.paxml.core.IParserContext;

/**
 * The scenario tag factory.
 * @author Xuetao Niu
 *
 */
@RootTag(ScenarioEntityFactory.TAG_NAME)
public class ScenarioEntityFactory extends AbstractPaxmlEntityFactory {
    /**
     * The root tag name.
     */
    public static final String TAG_NAME = "scenario";
    /**
     * The scenario tag.
     * @author Xuetao Niu
     *
     */
    public static class Scenario extends AbstractPaxmlEntity {        
        /**
         * {@inheritDoc}
         */
        @Override
        public Object execute(Context context) {
            // set the entity immediately
            context.setEntity(this);
            return super.execute(context);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object doExecute(Context context) {

            executeChildren(context);

            Object result = context.getInvocationResult();
                        
            context.setInvocationResult(null);
            
            return result;

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractPaxmlEntity doCreate(OMElement root, IParserContext context) {
        Scenario scenario = new Scenario();        
        return scenario;
    }

}
