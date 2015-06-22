/**
 * This file is part of PaxmlWeb.
 *
 * PaxmlWeb is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlWeb is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlWeb.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.web;

import java.util.ArrayList;
import java.util.List;

import org.paxml.core.IObjectContainer;

public class HtmlBuilder {
	private final List from;

	public HtmlBuilder(Object from) {
		if (from instanceof IObjectContainer) {
			this.from = ((IObjectContainer) from).list();
		} else if (from instanceof List) {
			this.from = (List) from;
		} else {
			this.from = new ArrayList();
			this.from.add(from);
		}
	}

	public String build() {
		return null;
	}
}
