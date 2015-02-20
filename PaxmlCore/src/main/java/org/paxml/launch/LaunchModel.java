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
package org.paxml.launch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import org.paxml.core.PaxmlResource;
import org.paxml.tag.plan.PlanEntityFactory.Plan;
import org.springframework.core.io.Resource;

/**
 * Launch model impl.
 * 
 * @author Xuetao Niu
 * 
 */
public class LaunchModel {
	/**
	 * Per-jvm/classloader increasing process id generator, starting from 1.
	 */
	private static final AtomicLong PID = new AtomicLong(1);

	private final StaticConfig config = new StaticConfig();
	private volatile Resource resource;
	private volatile String name;
	private final Map<String, Group> groups = Collections.synchronizedMap(new LinkedHashMap<String, Group>());
	private final Settings globalSettings = new Settings(null);
	private volatile List<LaunchPoint> launchPoints;
	private volatile int concurrency;
	private Plan planEntity;

	public Settings getGlobalSettings() {
		return globalSettings;
	}

	public Map<String, Group> getGroups() {
		return groups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(int concurrency) {
		this.concurrency = concurrency;
	}

	/**
	 * Get the selected groups to run.
	 * 
	 * @return the selected
	 */
	private Set<Group> getSelectedGroups() {
		Set<Group> set = new LinkedHashSet<Group>();
		for (Matcher groupMatcher : globalSettings.getGroupMatchers()) {
			for (Map.Entry<String, Group> groupEntry : groups.entrySet()) {
				if (groupMatcher.match(groupEntry.getKey())) {
					set.add(groupEntry.getValue());
				}
			}
		}
		return set;
	}

	/**
	 * Get launch points, where each point has a unique process id counting from
	 * 1. The process id reflects the order of the point to be submitted into
	 * the execution thread pool.
	 * 
	 * @param forceRefresh
	 *            false to use cached points if there are, true to always
	 *            reparse the points which is expensive.
	 * @return the launch points, never null
	 */
	public synchronized List<LaunchPoint> getLaunchPoints(boolean forceRefresh, long executionId) {
		if (forceRefresh || launchPoints == null) {
			List<Map<PaxmlResource, List<Settings>>> points = findLaunchPoints();

			launchPoints = new Vector<LaunchPoint>();
			for (Map<PaxmlResource, List<Settings>> map : points) {
				for (Map.Entry<PaxmlResource, List<Settings>> entry : map.entrySet()) {
					for (Settings s : entry.getValue()) {
						List<Properties> explodedFactors = explodeFactors(s);
						if (explodedFactors == null || explodedFactors.size() <= 0) {
							launchPoints.add(createLaunchPoint(entry.getKey(), s, null, PID.getAndIncrement(), executionId));
						} else {
							for (Properties factors : explodedFactors) {
								launchPoints.add(createLaunchPoint(entry.getKey(), s, factors, PID.getAndIncrement(), executionId));
							}
						}
					}
				}
			}
		}
		return launchPoints;
	}

	private LaunchPoint createLaunchPoint(PaxmlResource res, Settings settings, Properties factors, long processId, long executionId) {
		Properties props = new Properties();

		if (settings != null) {
			for (Map.Entry<Object, Object> entry : settings.getProperties().entrySet()) {
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				if (settings == globalSettings || !value.equals(globalSettings.getProperties().get(key))) {
					props.put(key, value);
				}
			}
		}

		return new LaunchPoint(this, res, settings.getGroup(), getGlobalSettings().getProperties(), props, factors, processId, executionId);
	}

	/**
	 * Execute a launch point.
	 * 
	 * @param point
	 *            the launch point
	 * @return the resource execution result
	 */
	public Object execute(LaunchPoint point) {
		Paxml paxml = new Paxml(point.getProcessId(), point.getExecutionId());
		paxml.addStaticConfig(config);
		return paxml.execute(point.getResource().getName(), System.getProperties(), point.getEffectiveProperties(false));
	}

	/**
	 * Execute a collection of launch points.
	 * 
	 * @param points
	 *            the points
	 * @return the list of results corresponding to each launch point.
	 */
	public List<Object> execute(Collection<LaunchPoint> points) {
		List<Object> results = new ArrayList<Object>(points.size());
		for (LaunchPoint point : points) {
			results.add(execute(point));
		}
		return results;
	}

	private void populateResourceMap(Map<PaxmlResource, List<Settings>> map, PaxmlResource res, Group group) {
		Settings settings = new Settings(group == null ? "" : group.getId());
		Properties properties = settings.getProperties();
		properties.putAll(getGlobalSettings().getProperties());
		if (group != null) {
			properties.putAll(group.getSettings().getProperties());
		}
		Map<String, Factor> factorMap = settings.getFactors();

		// merge the global factors
		for (Map.Entry<String, Factor> globalFactorEntry : getGlobalSettings().getFactors().entrySet()) {
			final String key = globalFactorEntry.getKey();
			Factor factor = factorMap.get(key);
			if (factor == null) {
				factor = new Factor();
				factor.setName(key);
				factorMap.put(key, factor);
			}
			factor.getValues().addAll(globalFactorEntry.getValue().getValues());
		}
		// copy my own factors
		if (group != null) {
			factorMap.putAll(group.getSettings().getFactors());
		}
		List<Settings> list = map.get(res);
		if (list == null) {
			list = new Vector<Settings>();
			map.put(res, list);
		}
		list.add(settings);
	}

	private List<Map<PaxmlResource, List<Settings>>> findLaunchPoints() {
		List<Map<PaxmlResource, List<Settings>>> result = new ArrayList<Map<PaxmlResource, List<Settings>>>();
		// first check all scenarios
		Map<PaxmlResource, List<Settings>> singleMap = new LinkedHashMap<PaxmlResource, List<Settings>>();

		for (Matcher matcher : globalSettings.getSingleMatchers()) {
			for (PaxmlResource selectedResource : config.getResources()) {
				if ((matcher.isMatchPath() && matcher.match(selectedResource.getPath()) || (!matcher.isMatchPath() && matcher.match(selectedResource.getName())))) {
					populateResourceMap(singleMap, selectedResource, null);
				}
			}
		}
		if (!singleMap.isEmpty()) {
			result.add(singleMap);
		}
		// then check all groups
		Set<Group> selectedGroups = getSelectedGroups();
		for (Group group : selectedGroups) {
			Map<PaxmlResource, List<Settings>> map = new LinkedHashMap<PaxmlResource, List<Settings>>();
			for (PaxmlResource selectedResource : config.getResources()) {

				if (group.matchPath(selectedResource.getPath()) || group.matchName(selectedResource.getName())) {
					populateResourceMap(map, selectedResource, group);
				}
			}
			if (!map.isEmpty()) {
				result.add(map);
			}
		}
		return result;
	}

	private static List<Properties> explodeFactors(Settings settings) {

		List<String> factorNames = new ArrayList<String>();

		List<List<Object>> factors = new ArrayList<List<Object>>();
		for (Map.Entry<String, Factor> entry : settings.getFactors().entrySet()) {
			factors.add(new ArrayList<Object>(entry.getValue().getValues()));
			factorNames.add(entry.getKey());
		}

		if (factorNames.size() < 1) {
			return null;
		} else {
			List<List<Object>> exploded = new ArrayList<List<Object>>();
			for (Object factor : factors.get(0)) {
				List<Object> item = new ArrayList<Object>();
				item.add(factor);
				exploded.add(item);
			}
			// make more combinations
			for (int i = 1; i < factors.size(); i++) {
				List<Object> more = factors.get(i);
				exploded = combineMoreFactors(exploded, more);
			}

			List<Properties> result = new ArrayList<Properties>();

			for (int i = 0; i < exploded.size(); i++) {
				List<Object> combination = exploded.get(i);
				Properties map = new Properties();
				for (int j = 0; j < factorNames.size(); j++) {
					map.put(factorNames.get(j), combination.get(j));
				}
				result.add(map);
			}

			return result;
		}
	}

	private static List<List<Object>> combineMoreFactors(List<List<Object>> list, List<Object> more) {
		if (more.size() <= 0) {
			return list;
		}
		List<List<Object>> result = new ArrayList<List<Object>>(0);

		for (int i = 0; i < more.size(); i++) {
			Object m = more.get(i);
			for (int j = 0; j < list.size(); j++) {
				List<Object> base = list.get(j);
				List<Object> newList = new ArrayList<Object>(base);
				newList.add(m);
				result.add(newList);
			}

		}
		return result;

	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public StaticConfig getConfig() {
		return config;
	}

	public Plan getPlanEntity() {
		return planEntity;
	}

	public void setPlanEntity(Plan planEntity) {
		this.planEntity = planEntity;
	}

}
