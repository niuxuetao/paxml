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

import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
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
	private String scenario;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInvoke(Context context) throws Exception {
		LaunchModel model = Plan.getLaunchModel(context);
		Settings settings = model.getGlobalSettings();
		if (StringUtils.isNotBlank(group)) {
			for (String groupName : AbstractTag.parseDelimitedString(group, null)) {
				Matcher matcher = new Matcher();
				matcher.setMatchPath(false);
				matcher.setPattern(groupName);
				settings.getGroupMatchers().add(matcher);
			}
		}
		if(StringUtils.isNotBlank(scenario)){
			for (String scenarioName : AbstractTag.parseDelimitedString(scenario, null)) {
				Matcher matcher = new Matcher();
				matcher.setMatchPath(false);
				matcher.setPattern(scenarioName);
				settings.getSingleMatchers().add(matcher);
			}
		}
		return null;
	}

	@Override
	protected void afterPropertiesInjection(Context context) {
		super.afterPropertiesInjection(context);
		if (StringUtils.isNotBlank(scenario) && StringUtils.isNotBlank(group)) {
			throw new PaxmlRuntimeException("Either the 'scenario' or the 'group' attribute should be specified for an <execution> tag, not both.");
		}
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

}
