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
package org.paxml.tag.plan;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.launch.Factor;
import org.paxml.launch.LaunchModel;
import org.paxml.tag.AbstractTag;
import org.paxml.tag.ITag;
import org.paxml.tag.plan.PlanEntityFactory.Plan;

/**
 * The factor tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = FactorTag.TAG_NAME)
public class FactorTag extends BeanTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "factor";

    private boolean mergeGlobal = false;
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        if (StringUtils.isBlank(name)) {
            throw new PaxmlRuntimeException("The 'name' attribute is not given!");
        }
        Factor factor = new Factor();
        factor.setName(name);
        factor.setMergeGlobal(mergeGlobal);
        
        Object value = getValue();

        if (value instanceof List) {
            for (Object item : (List) value) {
                if (item != null) {
                    factor.getValues().add(item.toString());
                }
            }
        } else {
            
            for (String part : AbstractTag.parseDelimitedString(value == null ? null : value.toString(), null)) {
                if (!factor.getValues().add(part)) {
                    throw new PaxmlRuntimeException("Duplicated factor value given: " + part);
                }
            }
        }
        if (null == findParentScenarioTag()) {
            // set global factors
            LaunchModel model = Plan.getLaunchModel(context);
            if (null != model.getGlobalSettings().getFactors().put(factor.getName(), factor)) {
                throw new PaxmlRuntimeException("Conflicting global factor: " + factor.getName());
            }
        }
        return factor;
    }

    private ScenarioTag findParentScenarioTag() {
        ITag tag = this.getParent();

        while (tag != null) {
            if (tag instanceof ScenarioTag) {
                return (ScenarioTag) tag;
            }
            tag = tag.getParent();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMergeGlobal() {
        return mergeGlobal;
    }

    public void setMergeGlobal(boolean mergeGlobal) {
        this.mergeGlobal = mergeGlobal;
    }

}
