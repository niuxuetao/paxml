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
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.bean.PropertiesTag.PropertiesObjectTree;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.launch.Factor;
import org.paxml.launch.Group;
import org.paxml.launch.LaunchModel;
import org.paxml.launch.Matcher;
import org.paxml.launch.Settings;
import org.paxml.tag.AbstractTag;
import org.paxml.tag.plan.PlanEntityFactory.Plan;

/**
 * The scenario tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = ScenarioTag.TAG_NAME)
public class ScenarioTag extends BeanTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "scenario";

    private String path;
    private String name;
    private String group;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        if (StringUtils.isBlank(group)) {
            throw new PaxmlRuntimeException("The 'group' attribute is not given!");
        }
        if (StringUtils.isBlank(name) && StringUtils.isBlank(path)) {
            throw new PaxmlRuntimeException("Neither the 'name' nor the 'path' attribute is not given!");
        }
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(path)) {
            throw new PaxmlRuntimeException("Cannot have both the 'name' and the 'path' attribute given!");
        }

        LaunchModel model = Plan.getLaunchModel(context);

        Group g = new Group(group);
        if (null != model.getGroups().put(group, g)) {
            throw new PaxmlRuntimeException("Group name conflict: " + group);
        }
        
        Settings s = g.getSettings();

        for (String pattern : AbstractTag.parseDelimitedString(name, null)) {

            Matcher m = new Matcher();
            m.setPattern(pattern.trim());
            m.setMatchPath(false);
            s.getMatchers().add(m);

        }
        for (String pattern : AbstractTag.parseDelimitedString(path, null)) {

            Matcher m = new Matcher();
            m.setPattern(pattern.trim());
            m.setMatchPath(true);
            s.getMatchers().add(m);

        }

        Object value = getValue();
        if (value instanceof List) {
            for (Object item : (List) value) {
                buildGroup(g, item);
            }
        } else {
            buildGroup(g, value);
        }
        return model;
    }

    private void buildGroup(Group g, Object obj) {
        if (obj instanceof Factor) {
            Factor f = (Factor) obj;
            if (null != g.getSettings().getFactors().put(f.getName(), f)) {
                throw new PaxmlRuntimeException("Conflicting factor name given under one <" + TAG_NAME + "> tag: "
                        + f.getName());
            }
        } else if (obj instanceof PropertiesObjectTree) {
            // must be properties
            Properties props = g.getSettings().getProperties();
            for (Map.Entry<String, Object> entry : ((PropertiesObjectTree) obj).entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    props.put(key, value);
                }
            }
        } else if (obj != null) {
            throw new PaxmlRuntimeException("Parameter type not supported: " + obj.getClass().getName());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
