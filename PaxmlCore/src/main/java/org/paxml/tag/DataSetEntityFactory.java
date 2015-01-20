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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.paxml.annotation.RootTag;
import org.paxml.core.Context;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlResource;
import org.paxml.tag.ScenarioEntityFactory.Scenario;

/**
 * The data set entity factory.
 * 
 * @author Xuetao Niu
 * 
 */
@RootTag("dataSet")
public class DataSetEntityFactory extends AbstractPaxmlEntityFactory {

    /**
     * The parameter name of scope.
     */
    public static final String SCOPE = "scope";

    /**
     * The scope parameter name's value for local consts.
     */
    public static final String SCOPE_LOCAL = "local";
    /**
     * The scope parameter name's value for global consts.
     */
    public static final String SCOPE_GLOBAL = "global";

    /**
     * Private keys.
     * 
     * @author Xuetao Niu
     * 
     */
    private static enum PrivateKeys {
        LOADED_GLOBAL_RESOURCES
    }

    /**
     * The data set entity impl.
     * 
     * @author Xuetao Niu
     * 
     */
    public static class DataSet extends Scenario {
        /**
         * {@inheritDoc}
         */
        @Override
        protected Object doExecute(Context context) {
            // keep the existing ids in order to skip, because they are not
            // newly created by the data set.
            Set<String> existingIds = new HashSet<String>(context.getConstIds());

            Object result = super.doExecute(context);

            Object scope = context.getConst(SCOPE, false);

            Context targetContext;

            if (SCOPE_GLOBAL.equals(scope)) {
                Set<PaxmlResource> loaded = (Set<PaxmlResource>) context.getInternalObject(
                        PrivateKeys.LOADED_GLOBAL_RESOURCES, true);
                if (loaded == null) {
                    loaded = new HashSet<PaxmlResource>();
                    context.setInternalObject(PrivateKeys.LOADED_GLOBAL_RESOURCES, loaded, true);
                }

                if (loaded.contains(getResource())) {
                    return null;
                }
                loaded.add(getResource());
                // put all into global scope.
                targetContext = context.getRootContext();
            } else {
                // treat as local and set it in case it will be used and not
                // given.
                context.setConst(SCOPE, null, SCOPE_LOCAL, false);

                // set the caller's context
                targetContext = context.findCallerContext();
                if (targetContext == null) {
                    targetContext = context.getCurrentEntityContext();
                }
            }

            Map<String, Object> consts = getDataMap(context);

            Map<String, String> mapping = getDataNameMap(context, consts);

            for (Map.Entry<String, Object> entry : consts.entrySet()) {
                String id = entry.getKey();
                if (!existingIds.contains(id)) {
                    targetContext.setConst(id, mapping.get(id), entry.getValue(), true);
                }
            }

            return result;
        }
        
        /**
         * Get the id to tag name mapping for xpath selection use.
         * 
         * @param context
         *            the context
         * @param dataMap
         *            the data map.
         * @return the map where keys are the ids, and values are the tag names,
         *         never returns null.
         */
        protected Map<String, String> getDataNameMap(Context context, Map<String, Object> dataMap) {
            return context.getConstIdToRootNameMapping();
        }

        /**
         * Get the data map from context.
         * 
         * @param context
         *            the context
         * @return the data map, where keys are ids, never returns null.
         */
        protected Map<String, Object> getDataMap(Context context) {
            return context.getConsts();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractPaxmlEntity doCreate(OMElement root, IParserContext context) {
        return new DataSet();
    }

}
