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

import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.paxml.launch.LaunchModel;
import org.paxml.launch.Matcher;
import org.paxml.launch.Settings;
import org.paxml.tag.AbstractTag;
import org.paxml.tag.plan.PlanEntityFactory.Plan;

/**
 * The execute tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = ExecutionTag.TAG_NAME)
public class ExecutionTag extends BeanTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "execution";

    private String group;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        LaunchModel model = Plan.getLaunchModel(context);
        Settings settings = model.getGlobalSettings();
        
        for (String groupName : AbstractTag.parseDelimitedString(group, null)) {
            Matcher matcher = new Matcher();
            matcher.setMatchPath(false);
            matcher.setPattern(groupName);
            settings.getMatchers().add(matcher);
        }
        return null;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
