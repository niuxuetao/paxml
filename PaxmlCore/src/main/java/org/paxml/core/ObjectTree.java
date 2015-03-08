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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;

/**
 * The object tree which encloses object lists and non IObjectContainer objects.
 * 
 * @author Xuetao Niu
 * 
 */
public class ObjectTree extends LinkedHashMap<String, Object> implements IObjectContainer {

	private String id;

	private final List<Object> list = new ArrayList<Object>();

	private final OMElement ele;

	/**
	 * Default constructor.
	 */
	public ObjectTree() {
		this((OMElement) null);
	}

	public ObjectTree(OMElement ele) {
		super();
		this.ele = ele;		
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
	public ObjectTree(final Map<?, ?> map) {

		this();
		addValues(map);

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
			put(key, value);
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
				ObjectList list = new ObjectList(true, existing, value);
				put(key, list);
			}

		} else {
			ObjectList list = new ObjectList(true, existing, value);
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
		return new ObjectTree();
	}

	@Override
	public List<Object> getList() {
		return Collections.unmodifiableList(list);
	}

	@Override
    public String toXml() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public String toJson() {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public void fromXml(String xml) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void fromJson(String json) {
	    // TODO Auto-generated method stub
	    
    }

}
