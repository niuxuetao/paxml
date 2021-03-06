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
package org.paxml.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.paxml.el.UtilFunctions;
import org.paxml.util.ReflectUtils;
import org.paxml.util.XmlUtils;

import sun.security.action.PutAllAction;

/**
 * The object list.
 * 
 * @author Xuetao Niu
 * 
 */
public class ObjectList extends ArrayList<Object> implements IObjectContainer {
	private String name;
	private String id;
	boolean _dynamic;

	/**
	 * Construct from a list of existing objects.
	 * 
	 * @param existing
	 *            the objects to add to initialize the list with, by calling
	 *            justAdd().
	 */
	public ObjectList(String name, boolean dynamic, final Object... existing) {
		super(existing.length);
		this.name = name;
		this._dynamic = dynamic;
		for (Object e : existing) {
			add(e);
		}
	}

	@Override
	public List<Object> list() {
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public ObjectList copy() {
		ObjectList newList = new ObjectList(name, _dynamic);
		for (Object item : this) {
			if (item instanceof IObjectContainer) {
				item = ((IObjectContainer) item).copy();
			}
			newList.add(item);
		}
		return newList;
	}

	@Override
	public void addValue(String key, Object value) {
		ObjectTree tree = new ObjectTree(key);
		tree.put(key, checkToCopy(value));
		add(tree);
	}

	private Object checkToCopy(Object value) {
		if (value instanceof IObjectContainer) {
			value = ((IObjectContainer) value).copy();
		}
		return value;
	}

	@Override
	public Object set(int index, Object element) {
		return super.set(index, checkToCopy(element));
	}

	@Override
	public boolean add(Object e) {
		return super.add(checkToCopy(e));
	}

	@Override
	public void add(int index, Object element) {
		super.add(index, checkToCopy(element));
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		ArrayList<Object> col = new ArrayList<Object>(c.size());
		for (Object v : c) {
			col.add(checkToCopy(v));
		}
		return super.addAll(col);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		ArrayList<Object> col = new ArrayList<Object>(c.size());
		for (Object v : c) {
			col.add(checkToCopy(v));
		}
		return super.addAll(index, col);
	}

	public Object shrink() {
		if (size() <= 0) {
			return null;
		} else if (size() == 1) {
			return get(0);
		} else {
			return this;
		}
	}

	@Override
	public String toXml() {
		return toXml(null, null);
	}

	public String toXml(String rootName, String rootElementName) {
		if (rootName == null) {
			rootName = name;
		}
		if (rootElementName == null) {
			rootElementName = name;
		}
		return XmlUtils.toXml(this, rootName, rootElementName);
	}

	@Override
	public String toJson() {

		return XmlUtils.toJson(this);
	}

	@Override
	public void loadXml(String xml) {
		ReflectUtils.collect(XmlUtils.fromXml(xml), this, true);
	}

	@Override
	public void loadJson(String json) {
		ReflectUtils.collect(XmlUtils.fromJson(json, false), this, true);
	}
	@Override
	public String name() {
		return name;
	}
//	@Override
//	public String toString(){
//		return toJson();
//	}

}
