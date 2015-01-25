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

import org.apache.commons.io.FilenameUtils;

/**
 * A group represents a "scenario" tag in plan file. It is the grouping of a few
 * scenarios.
 * 
 * @author Xuetao Niu
 * 
 */
public class Group {
	private final String id;
	private final Settings settings;

	public Group(String id) {
		this.id = id;
		settings = new Settings(id);
	}

	public String getId() {
		return id;
	}

	public Settings getSettings() {
		return settings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * Check if a path matches with the wildcards.
	 * 
	 * @param path
	 *            the paxml resource path
	 * @return true matches, false not
	 */
	public boolean matchPath(String path) {
		String ext = FilenameUtils.getExtension(path);
		path = path.substring(0, path.length() - ext.length() - 1);

		for (Matcher matcher : getSettings().getGroupMatchers()) {

			if (matcher.isMatchPath() && matcher.match(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * Check if a name matches with the wildcards.
	 * 
	 * @param name
	 *            the paxml resource name
	 * @return true matches, false not
	 */
	public boolean matchName(String name) {
		for (Matcher matcher : getSettings().getGroupMatchers()) {

			if (!matcher.isMatchPath() && matcher.match(name)) {
				return true;
			}
		}
		return false;
	}
}
