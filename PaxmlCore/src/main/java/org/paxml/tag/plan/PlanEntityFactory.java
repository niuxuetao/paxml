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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.paxml.annotation.RootTag;
import org.paxml.core.Context;
import org.paxml.core.IParserContext;
import org.paxml.launch.Factor;
import org.paxml.launch.Group;
import org.paxml.launch.LaunchModel;
import org.paxml.tag.AbstractPaxmlEntity;
import org.paxml.tag.AbstractPaxmlEntityFactory;
import org.paxml.tag.ScenarioEntityFactory.Scenario;

/**
 * The plan tag factory.
 * 
 * @author Xuetao Niu
 * 
 */
@RootTag(PlanEntityFactory.TAG_NAME)
public class PlanEntityFactory extends AbstractPaxmlEntityFactory {

	/**
	 * The root tag name.
	 */
	public static final String TAG_NAME = "plan";
	public static final String CONCURRENCY_CONST = "paxml.plan.concurrency";

	/**
	 * The plan tag.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	public static class Plan extends Scenario {

		/**
		 * Get the launch model from context's global internal object map.
		 * 
		 * @param context
		 *            the context
		 * @return the launch model, or null if not found.
		 */
		public static LaunchModel getLaunchModel(Context context) {
			return (LaunchModel) context.getInternalObject(LaunchModel.class, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object execute(Context context) {
			Object result = super.execute(context);
			finishUpLaunchModel(context);
			return result;
		}

		private void finishUpLaunchModel(Context context) {
			LaunchModel model = getLaunchModel(context);
			Integer concurrency = context.getConst(CONCURRENCY_CONST, true, Integer.class);
			model.setConcurrency(concurrency == null ? 0 : concurrency);
			// put the global properties
			Properties globalProps = model.getGlobalSettings().getProperties();
			final Set<String> propIds = context.getPropertyConstIds(false);
			for (Map.Entry<String, Object> entry : context.getIdMap(false, true).entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value == null || key == null || !propIds.contains(key)) {
					continue;
				}
				globalProps.put(key, value);
			}
			globalProps.remove(LaunchModel.class);
			System.getProperties().remove(LaunchModel.class);
			// merge local factors with global ones if needed
			final Map<String, Factor> globalFactors = model.getGlobalSettings().getFactors();
			for (Group group : model.getGroups().values()) {
				for (Factor factor : group.getSettings().getFactors().values()) {
					if (factor.isMergeGlobal()) {
						Factor globalFactor = globalFactors.get(factor.getName());
						if (globalFactor != null) {
							factor.getValues().addAll(globalFactor.getValues());
						}
					}
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractPaxmlEntity doCreate(OMElement root, IParserContext context) {
		Plan plan = new Plan();
		return plan;
	}

}
