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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.paxml.core.IEntityExecutionListener;
import org.paxml.core.IExecutionListener;
import org.paxml.core.ITagExecutionListener;
import org.paxml.core.PaxmlResource;
import org.paxml.tag.ITagLibrary;

/**
 * Static paxml config that can be shared between threads.
 * 
 * @author Xuetao Niu
 * 
 */
public class StaticConfig {
	private final Set<PaxmlResource> resources = Collections.synchronizedSet(new LinkedHashSet<PaxmlResource>());
	private final List<Class<? extends ITagLibrary>> tagLibs = new Vector<Class<? extends ITagLibrary>>(0);
	private final List<Class<? extends IExecutionListener>> executionListeners = new Vector<Class<? extends IExecutionListener>>(0);
	private final List<Class<? extends IEntityExecutionListener>> entityListeners = new Vector<Class<? extends IEntityExecutionListener>>(0);
	private final List<Class<? extends ITagExecutionListener>> tagListeners = new Vector<Class<? extends ITagExecutionListener>>(0);

	public List<Class<? extends ITagLibrary>> getTagLibs() {
		return tagLibs;
	}

	public List<Class<? extends IExecutionListener>> getExecutionListeners() {
		return executionListeners;
	}

	public List<Class<? extends IEntityExecutionListener>> getEntityListeners() {
		return entityListeners;
	}

	public List<Class<? extends ITagExecutionListener>> getTagListeners() {
		return tagListeners;
	}

	public Set<PaxmlResource> getResources() {
		return resources;
	}

	public void add(StaticConfig fromAnother) {
		resources.addAll(fromAnother.getResources());
		tagLibs.addAll(fromAnother.getTagLibs());
		executionListeners.addAll(fromAnother.getExecutionListeners());
		entityListeners.addAll(fromAnother.getEntityListeners());
		tagListeners.addAll(fromAnother.getTagListeners());
	}
}
