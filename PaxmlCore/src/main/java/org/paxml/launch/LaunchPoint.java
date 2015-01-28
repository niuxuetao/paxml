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

import java.util.Properties;

import org.paxml.core.PaxmlResource;

/**
 * A launch point is a set of information in order to launch a paxml resource.
 * Such information includes the properties to launch a paxml resource with, and
 * the paxml resource referece itself.
 * 
 * @author Xuetao Niu
 * 
 */
public class LaunchPoint {
	private final PaxmlResource resource;
	private final Properties properties;
	private final Properties globalProperties;
	private final Properties factors;
	private final long processId;
	private final long sessionId;
	private final String group;
	private final LaunchModel model;
	private volatile long startMs;
	private volatile long stopMs;

	/**
	 * Construct from factors.
	 * 
	 * @param model
	 *            the launch model
	 * @param resource
	 *            the paxml resource
	 * @param group
	 *            the launch group name
	 * @param globalProperties
	 *            the global properties
	 * @param properties
	 *            the local properties
	 * @param factors
	 *            the factors.
	 * @param processId
	 *            the process id
	 */
	public LaunchPoint(LaunchModel model, PaxmlResource resource, String group, Properties globalProperties, Properties properties,
			Properties factors, long processId, long sessionId) {
		this.model = model;
		this.resource = resource;
		this.properties = properties;
		this.globalProperties = globalProperties;
		this.factors = factors;
		this.processId = processId;
		this.sessionId = sessionId;
		this.group = group;
	}

	public PaxmlResource getResource() {
		return resource;
	}

	public Properties getProperties() {
		return properties;
	}

	public Properties getGlobalProperties() {
		return globalProperties;
	}

	public Properties getFactors() {
		return factors;
	}

	/**
	 * Get the merged launch properties.
	 * 
	 * @param includeSystemProperties
	 *            true to also include system properties, false not to.
	 * @return the merged launch properties, never null.
	 */
	public Properties getEffectiveProperties(boolean includeSystemProperties) {
		Properties props = new Properties();
		if (includeSystemProperties) {
			props.putAll(System.getProperties());
		}
		if (globalProperties != null) {
			props.putAll(globalProperties);
		}
		if (properties != null) {
			props.putAll(properties);
		}
		if (factors != null) {
			props.putAll(factors);
		}
		return props;
	}

	public long getProcessId() {
		return processId;
	}

	public Object execute() {
		stopMs = 0;
		startMs = System.currentTimeMillis();
		try {
			return model.execute(this);
		} finally {
			stopMs = System.currentTimeMillis();
		}
	}

	public long getStartMs() {
		return startMs;
	}

	public long getStopMs() {
		return stopMs;
	}

	public LaunchModel getModel() {
		return model;
	}

	public String getGroup() {
		return group;
	}

	public long getSessionId() {
		return sessionId;
	}

}
