<<<<<<< HEAD
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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.paxml.util.XmlUtils;

/**
 * The object tree which encloses object lists and non IObjectContainer objects.
 * 
 * @author Xuetao Niu
 * 
 */
public class ObjectTree extends LinkedHashMap<String, Object> implements IObjectContainer {

	private String id;
	private String name;

	private final Set<String> xmlAttributes = new HashSet<String>(0);

	private final List<Object> list = new ArrayList<Object>();

	/**
	 * Default constructor.
	 */
	public ObjectTree(String name) {
		this(name, null);
	}

	/**
	 * Construct from an existing map.
	 * 
	 * @param map
	 *            the map
	 * @param checkConflict
	 *            true to check id conflicts, false to convert conflicting id
	 *            values into a list and put the list as the value with that id.
	 */
	public ObjectTree(String name, final Map<?, ?> map) {

		super();
		this.name = name;
		if (map != null) {
			addValues(map);
		}

	}

	public Object shrink() {
		if (size() <= 0) {
			return null;
		}
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addValues(Map<?, ?> map) {
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			addValue(entry.getKey().toString(), entry.getValue());
		}
	}

	@Override
	public void addValue(String key, Object value) {
		list.add(value);
		Object existing = get(key);
		if (existing == null) {
			// put will do the copying work, if copy-able
			put(key, value);
			// getting a copy of it, if copied
			Object v = get(key);
			if (v instanceof ObjectList) {
				((ObjectList) v)._dynamic = false;
			}
		} else if (existing instanceof ObjectList) {
			ObjectList existingList = ((ObjectList) existing);
			if (existingList._dynamic) {
				ObjectList list = (ObjectList) existing;
				list.add(value);
			} else {
				ObjectList list = new ObjectList(key, true, existing, value);
				put(key, list);
			}

		} else {
			ObjectList list = new ObjectList(key, true, existing, value);
			put(key, list);
		}
	}

	private Object checkToCopy(Object value) {
		if (value instanceof IObjectContainer) {
			value = ((IObjectContainer) value).copy();
		}
		return value;
	}

	@Override
	public Object put(String key, Object value) {
		list.add(value);
		return super.put(key, checkToCopy(value));
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
			map.put(entry.getKey(), checkToCopy(entry.getValue()));
		}
		super.putAll(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectTree copy() {
		ObjectTree newTree = emptyCopy();
		newTree.xmlAttributes.addAll(xmlAttributes);
		for (Map.Entry<String, Object> entry : entrySet()) {
			Object item = entry.getValue();
			if (item instanceof IObjectContainer) {
				item = ((IObjectContainer) item).copy();
			}
			newTree.put(entry.getKey(), item);
		}
		return newTree;
	}

	protected ObjectTree emptyCopy() {
		return new ObjectTree(name);
	}
	public boolean isXmlAttribute(String propName){
		return xmlAttributes.contains(propName);
	}
	public void addXmlAttributes(Collection<String> attrNames){
		xmlAttributes.addAll(attrNames);
	}
	@Override
	public List<Object> list() {
		return Collections.unmodifiableList(list);
	}

	public String toXml(String rootName) {
		if (rootName == null) {
			rootName = name;
		}
		return XmlUtils.toXml(this, rootName, null);
	}

	@Override
	public String toXml() {
		return toXml(null);
	}

	@Override
	public String toJson() {

		return XmlUtils.toJson(this);
	}

	@Override
	public void loadXml(String xml) {

		String json = XmlUtils.xmlToJson(xml);
		Object obj = XmlUtils.fromJson(json, true);
		if (obj instanceof Map) {
			addValues((Map) obj);
		} else {
			throw new PaxmlRuntimeException("Cannot load xml: " + xml);
		}
	}

	@Override
	public void loadJson(String json) {
		Object obj = XmlUtils.fromJson(json, true);
		if (obj instanceof Map) {
			addValues((Map) obj);
		} else {
			throw new PaxmlRuntimeException("Cannot load json with no key: " + json);
		}
	}

	@Override
	public String name() {
		return name;
	}
		
//	@Override
//	public String toString() {
//		return toXml();
//	}
}
=======
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.paxml.util.XmlUtils;

/**
 * The object tree which encloses object lists and non IObjectContainer objects.
 * 
 * @author Xuetao Niu
 * 
 */
public class ObjectTree extends LinkedHashMap<String, Object> implements IObjectContainer {

	private String id;
	private String name;

	private final Set<String> xmlAttributes = new LinkedHashSet<String>(0);

	private final List<Object> list = new ArrayList<Object>();

	/**
	 * Default constructor.
	 */
	public ObjectTree(String name) {
		this(name, null);
	}

	/**
	 * Construct from an existing map.
	 * 
	 * @param map
	 *            the map
	 * @param checkConflict
	 *            true to check id conflicts, false to convert conflicting id
	 *            values into a list and put the list as the value with that id.
	 */
	public ObjectTree(String name, final Map<?, ?> map) {

		super();
		this.name = name;
		if (map != null) {
			addValues(map);
		}

	}

	public Object shrink() {
		if (size() <= 0) {
			return null;
		}
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addValues(Map<?, ?> map) {
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			addValue(entry.getKey().toString(), entry.getValue());
		}
	}

	@Override
	public void addValue(String key, Object value) {
		list.add(value);
		Object existing = get(key);
		if (existing == null) {
			// put will do the copying work, if copy-able
			put(key, value);
			// getting a copy of it, if copied
			Object v = get(key);
			if (v instanceof ObjectList) {
				((ObjectList) v)._dynamic = false;
			}
		} else if (existing instanceof ObjectList) {
			ObjectList existingList = ((ObjectList) existing);
			if (existingList._dynamic) {
				ObjectList list = (ObjectList) existing;
				list.add(value);
			} else {
				ObjectList list = new ObjectList(key, true, existing, value);
				put(key, list);
			}

		} else {
			ObjectList list = new ObjectList(key, true, existing, value);
			put(key, list);
		}
	}

	private Object checkToCopy(Object value) {
		if (value instanceof IObjectContainer) {
			value = ((IObjectContainer) value).copy();
		}
		return value;
	}

	@Override
	public Object put(String key, Object value) {
		list.add(value);
		return super.put(key, checkToCopy(value));
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
			map.put(entry.getKey(), checkToCopy(entry.getValue()));
		}
		super.putAll(m);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectTree copy() {
		ObjectTree newTree = emptyCopy();
		newTree.xmlAttributes.addAll(xmlAttributes);
		for (Map.Entry<String, Object> entry : entrySet()) {
			Object item = entry.getValue();
			if (item instanceof IObjectContainer) {
				item = ((IObjectContainer) item).copy();
			}
			newTree.put(entry.getKey(), item);
		}
		return newTree;
	}

	protected ObjectTree emptyCopy() {
		return new ObjectTree(name);
	}
	public boolean isXmlAttribute(String propName){
		return xmlAttributes.contains(propName);
	}
	public void addXmlAttributes(Collection<String> attrNames){
		xmlAttributes.addAll(attrNames);
	}
	@Override
	public List<Object> getList() {
		return Collections.unmodifiableList(list);
	}

	public String toXml(String rootName) {
		if (rootName == null) {
			rootName = name;
		}
		return XmlUtils.toXml(this, rootName, null);
	}

	@Override
	public String toXml() {
		return toXml(null);
	}

	@Override
	public String toJson() {

		return XmlUtils.toJson(this);
	}

	@Override
	public void loadXml(String xml) {

		String json = XmlUtils.xmlToJson(xml);
		Object obj = XmlUtils.fromJson(json, true);
		if (obj instanceof Map) {
			addValues((Map) obj);
		} else {
			throw new PaxmlRuntimeException("Cannot load xml: " + xml);
		}
	}

	@Override
	public void loadJson(String json) {
		Object obj = XmlUtils.fromJson(json, true);
		if (obj instanceof Map) {
			addValues((Map) obj);
		} else {
			throw new PaxmlRuntimeException("Cannot load json with no key: " + json);
		}
	}

	@Override
	public String getName() {
		return name;
	}
		
//	@Override
//	public String toString() {
//		return toXml();
//	}
}
>>>>>>> 52015f93779e16712c25fb792946405b990961df
