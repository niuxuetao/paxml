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

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.jxpath.BasicVariables;
import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.Functions;
import org.apache.commons.jxpath.IdentityManager;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.Variables;
import org.apache.commons.jxpath.ri.model.beans.BeanPointerFactory;
import org.apache.commons.jxpath.ri.model.beans.NullPointer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Util;
import org.paxml.el.IUtilFunctionsFactory;
import org.paxml.file.IFile;
import org.paxml.launch.Paxml;
import org.paxml.tag.ITag;
import org.paxml.tag.ITagLibrary;
import org.paxml.tag.invoker.FileInvokerTag;
import org.paxml.user.UserKeyRepository;
import org.paxml.util.CryptoUtils;
import org.paxml.util.PaxmlUtils;
import org.paxml.util.ReflectUtils;
import org.paxml.util.XmlUtils;
import org.springframework.core.io.Resource;

/**
 * The execution context.
 * 
 * @author Xuetao Niu
 * 
 */
public class Context implements IdentityManager {
	private static final Log log = LogFactory.getLog(Context.class);

	/**
	 * The scope definition of data.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	public static enum Scope {

		/**
		 * The local scope.
		 */
		LOCAL,
		/**
		 * The parameter scope.
		 */
		PARAMETER
	}

	/**
	 * The private context keys.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static enum PrivateKeys {
		/**
		 * The paxml listener key.
		 */
		PAXML_LISTENER,
		/**
		 * The entity listener key.
		 */
		ENTITY_LISTENER,
		/**
		 * The tag listener key.
		 */
		TAG_LISTENER,
		/**
		 * The invocation result.
		 */
		RESULT,
		/**
		 * Stack.
		 */
		STACK,

		/**
		 * Paxml.
		 */
		PAXML,
		/**
		 * Context where the exception was created.
		 */
		EXCEPTION_CONTEXT,
		/**
		 * The locale.
		 */
		LOCALE,
		/**
		 * The const overwritable flag.
		 */
		CONST_OVERWRITABLE,
		/**
		 * The const names that are loaded as properties.
		 */
		PROPERTIES,

		/**
		 * The key for Closeables.
		 */
		CLOSEABLES,

		/**
		 * The key for all files
		 */
		FILES
	}

	/**
	 * This is the representation of execution stack.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	public static class Stack extends LinkedList<ITag> {
		/**
		 * Interface for traversing stack.
		 * 
		 * @author Xuetao Niu
		 * 
		 */
		public static interface IStackTraverser {
			/**
			 * Called upon visiting each tag and entity
			 * 
			 * @param entity
			 *            the entity
			 * @param tag
			 *            the tag
			 * @return true to continue traversal, false to stop traversal
			 */
			boolean onItem(IEntity entity, ITag tag);
		}

		private boolean dying = false;

		/**
		 * Print out the stack.
		 * 
		 * @param out
		 *            the target to print out
		 * @throws IOException
		 *             thrown while accessing the stream
		 */
		public void print(PrintStream out) throws IOException {
			for (ITag tag : this) {
				out.println(tag.toString());
			}
		}

		public boolean isDying() {
			return dying;
		}

		/**
		 * Mark global termination, so that no more tags will be executed.
		 * 
		 */
		public void die() {
			this.dying = true;
		}

		/**
		 * Visit each tag & entity combination of the stack.
		 * 
		 * @param t
		 *            the event handler
		 */
		public void traverse(IStackTraverser t) {
			IEntity entity = null;
			for (ITag tag : this) {
				if (!(tag instanceof IEntity)) {
					if (entity != tag.getEntity() || tag instanceof FileInvokerTag) {
						entity = tag.getEntity();
						if (!t.onItem(entity, tag)) {
							break;
						}
					}

				}
			}
		}

	}

	/**
	 * The name of the default parameter.
	 */
	public static final String DEFAULT_VALUE_NAME = "value";

	/**
	 * The global xpath variable name.
	 */
	public static final String XPATH_NAME_GLOBAL_VAR = "global";
	/**
	 * The local xpath variable name.
	 */
	public static final String XPATH_NAME_LOCAL_VAR = "local";

	private static final ThreadLocal<Context> THREAD_CONTEXT = new ThreadLocal<Context>();

	private IEntity entity;

	private final long processId;
	private final long id;
	private boolean returning = false;

	private final ObjectTree idConstsMap = new ObjectTree(null);
	private final Map<String, String> idToTagName = new HashMap<String, String>();
	private final Map<Object, Object> localMap = new LinkedHashMap<Object, Object>(0);
	private final Map<Object, Object> globalMap;
	private final Context parent;
	private final Context root;

	/**
	 * Create from parent context.
	 * 
	 * @param parent
	 *            the parent context, null is not allowed.
	 * 
	 */
	public Context(final Context parent) {
		if (parent == null) {
			throw new PaxmlRuntimeException("Parent context not given!");
		}
		this.parent = parent;
		root = parent.root;
		this.processId = root.processId;
		this.id = parent.id + 1;
		globalMap = parent.globalMap;

	}

	/**
	 * Create a root context.
	 * 
	 * @param initialProperties
	 *            initial properties, can be null
	 * @param processId
	 *            the virtual processId which is just a label. It depends on the
	 *            calling application to assign meaning.
	 */
	public Context(Properties initialProperties, long processId) {
		root = this;
		parent = null;
		this.processId = processId;
		this.id = 0;
		globalMap = new LinkedHashMap<Object, Object>(0);

		addProperties(initialProperties);

	}

	/**
	 * Add properties to this level of context.
	 * 
	 * @param properties
	 *            the properties where string keys will be taken as constants,
	 *            object keys will be global internal objects
	 */
	public void addProperties(Properties properties) {
		if (properties == null) {
			return;
		}
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			Object key = entry.getKey();
			if (key instanceof String) {
				addConst((String) key, (String) key, entry.getValue(), true);
			} else {
				setInternalObject(key, entry.getValue(), true);
			}
		}
	}

	/**
	 * Get the virtual process id. A virtual process id is uniquely identfying
	 * an execution of a out-most scenario.
	 * 
	 * @return the virtual process id.
	 */
	public long getProcessId() {
		return processId;
	}

	/**
	 * Get the context associated with the thread context.
	 * 
	 * @return the context, null if not associated.
	 */
	public static Context getCurrentContext() {
		return THREAD_CONTEXT.get();
	}

	/**
	 * Set the current thread context to null.
	 */
	public static void cleanCurrentThreadContext() {
		THREAD_CONTEXT.set(null);
	}

	/**
	 * Set this context as the current thread context.
	 */
	public void setAsCurrentThreadContext() {
		THREAD_CONTEXT.set(this);
	}

	/**
	 * Add a data object with id and tag name for xpath usage.
	 * 
	 * @param id
	 *            the id of the object
	 * @param rootTagName
	 *            the root tag name for xpath usage.
	 * @param c
	 *            the data object
	 * @param checkConflict
	 *            true to prevent id conflicts, false to overwrite the existing
	 *            value with the same id.
	 * 
	 */
	public void addConst(String id, String rootTagName, Object c, boolean checkConflict) {
		if (checkConflict && idConstsMap.get(id) != null) {
			throw new PaxmlRuntimeException("Const with id '" + id + "' already exists!");
		}
		idConstsMap.addValue(id, c);
		if (null != rootTagName) {
			idToTagName.put(id, rootTagName);
		}

	}

	/**
	 * Set a data object in context, overwriting existing const by id.
	 * 
	 * @param id
	 *            the id of the const to set and overwrite if exists.
	 * @param rootTagName
	 *            the root tag name of the new const to set.
	 * @param c
	 *            the new const
	 * @param checkConflict
	 *            true to check id conflicts if there is conflict, exception
	 *            will be thrown, false not to.
	 * @return the existing data, or null if there isn't existing data with the
	 *         id.
	 */
	public Object setConst(String id, String rootTagName, Object c, boolean checkConflict) {
		final Object existing = idConstsMap.put(id, c);
		if (existing != null && checkConflict) {
			throw new PaxmlRuntimeException("Id conflict: " + id);
		}
		if (null != rootTagName) {
			idToTagName.put(id, rootTagName);
		}
		return existing;
	}

	/**
	 * Set a bunch of consts into the current context.
	 * 
	 * @param map
	 *            the map containing the consts
	 * @param rootTags
	 *            the root tags for the map parameter.
	 * @param checkConflict
	 *            true to assert no ids conflicts before setting, false not to
	 *            assert.
	 */
	public void setConsts(Map<String, Object> map, Map<String, String> rootTags, boolean checkConflict) {
		if (checkConflict) {
			Collection<String> overlap = CollectionUtils.intersection(map.keySet(), idConstsMap.keySet());
			if (overlap.size() > 0) {
				throw new PaxmlRuntimeException("The followng id conflicts detected: " + overlap);
			}
		}
		idConstsMap.putAll(map);
		if (rootTags != null) {
			idToTagName.putAll(rootTags);
		}
	}

	/**
	 * Remove an added const.
	 * 
	 * @param id
	 *            the id
	 * @return the previously existing const, or null if no such value existed.
	 */
	public Object removeConst(String id) {
		idToTagName.remove(id);
		return idConstsMap.remove(id);
	}

	/**
	 * Get the internal object with key. An internal object is searched always
	 * from the root ancestor context. Internal object can be used as extension
	 * in tag execution. It is highly recommended to use private enum constants
	 * as keys.
	 * 
	 * @param key
	 *            the key.
	 * @param global
	 *            true to get from global scope, false to get from the current
	 *            context scope.
	 * @return the value. null if not found.
	 */
	public Object getInternalObject(Object key, boolean global) {
		if (global) {
			return globalMap.get(key);
		} else {
			return localMap.get(key);
		}
	}

	/**
	 * Set an internal object's value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param global
	 *            true to set in global scope, false to set in the current
	 *            context scope.
	 * @return the existing value, null if no existing value.
	 */
	public Object setInternalObject(Object key, Object value, boolean global) {
		if (global) {
			return globalMap.put(key, value);
		} else {
			return localMap.put(key, value);
		}
	}

	/**
	 * Get the local object.
	 * 
	 * @param key
	 *            the object key
	 * @param searchParents
	 *            true to search parent, false not to
	 * @return the object or null.
	 */
	public Object getLocalInternalObject(Object key, boolean searchParents) {
		Object value = getInternalObject(key, false);
		if (value == null && searchParents && parent != null) {
			value = parent.getLocalInternalObject(key, searchParents);
		}
		return value;
	}

	/**
	 * Remove an internal object.
	 * 
	 * @param key
	 *            the key
	 * @param global
	 *            true to from from global scope, false to from from the current
	 *            context scope.
	 * @return null if nothing exists with the key, otherwise the removed
	 *         object.
	 */
	public Object removeInternalObject(Object key, boolean global) {
		if (global) {
			return globalMap.remove(key);
		} else {
			return localMap.remove(key);
		}
	}

	/**
	 * Get a data object with id.
	 * 
	 * @param id
	 *            the id
	 * @param searchParent
	 *            true to search in parent contexts, false not to.
	 * @return null if not found, otherwise the data object.
	 */
	public Object getConst(String id, boolean searchParent) {
		if (idConstsMap.containsKey(id)) {
			return idConstsMap.get(id);
		}
		if (searchParent && parent != null) {
			return parent.getConst(id, true);
		}

		return null;
	}

	/**
	 * 
	 * Find const id by given value.
	 * 
	 * @param obj
	 *            the given value
	 * @param strict
	 *            true to do pointer comparison, false to do object equality
	 *            comparison
	 * @param searchParent
	 *            true to look also in parent contexts, false only look in the
	 *            current context
	 * @param excludes
	 *            the ids to exclude
	 * @return the id or null if no found.
	 * 
	 */
	public String findConstId(Object obj, boolean strict, boolean searchParent, String... excludes) {
		final Set<String> ex = new HashSet<String>(Arrays.asList(excludes));
		if (strict) {
			for (Map.Entry<String, Object> entry : idConstsMap.entrySet()) {
				if (entry.getValue() == obj && !ex.contains(entry.getKey())) {
					return entry.getKey();
				}
			}
		} else {
			for (Map.Entry<String, Object> entry : idConstsMap.entrySet()) {
				Object v = entry.getValue();
				final boolean exclude = ex.contains(entry.getKey());
				if (v == null && obj == null && !exclude) {
					return entry.getKey();
				}
				if (v != null && v.equals(obj) && !exclude) {
					return entry.getKey();
				}
			}
		}

		if (searchParent && parent != null) {
			return parent.findConstId(obj, strict, true, excludes);
		}
		return null;
	}

	/**
	 * Get a data object with id and expected type.
	 * 
	 * @param <T>
	 *            the expected type to convert to
	 * @param id
	 *            the id
	 * @param searchParent
	 *            true to search in parent contexts, false not to.
	 * @param clazz
	 *            the expected class
	 * @return null if not found, otherwise the data in expected class
	 */
	public <T> T getConst(String id, boolean searchParent, Class<? extends T> clazz) {
		Object c = getConst(id, searchParent);
		return (T) ReflectUtils.coerceType(c, clazz);
	}

	/**
	 * Check the existence of an data object.
	 * 
	 * @param id
	 *            the id
	 * @param searchParent
	 *            true to search in parent contexts, false not to.
	 * @return true if exists, false if not
	 */
	public boolean hasConstId(String id, boolean searchParent) {
		if (idConstsMap.containsKey(id)) {
			return true;
		}
		if (searchParent && parent != null) {
			return parent.hasConstId(id, searchParent);
		}
		return false;
	}

	/**
	 * Get a data object list with an id.
	 * 
	 * @param id
	 *            the id
	 * @param searchParent
	 *            true to search in parent contexts, false not to.
	 * @return null if no such data object exists with given id, otherwise,
	 *         always a list of the data object. If the data object itself is
	 *         not a list, then the object will be put into a new list to
	 *         return. Nothing in the context will be changed.
	 */
	public List<?> getConstWithIdAsList(String id, boolean searchParent) {
		Object obj = getConst(id, searchParent);
		if (obj == null) {
			return null;
		}
		if (obj instanceof List) {
			return (List<?>) obj;
		}
		List<Object> list = new ArrayList<Object>(1);
		list.add(obj);
		return list;
	}

	public Context getRootContext() {
		return root;
	}

	/**
	 * Get the paxml process execution listeners list.
	 * 
	 * @param autoCreate
	 *            true to create one if not existing yet, false to return null
	 *            if not existing yet.
	 * @return the list.
	 */
	public List<IExecutionListener> getPaxmlExecutionListeners(boolean autoCreate) {
		List<IExecutionListener> list = (List<IExecutionListener>) getInternalObject(PrivateKeys.PAXML_LISTENER, true);
		if (list == null && autoCreate) {
			list = new ArrayList<IExecutionListener>();
			setPaxmlExecutionListeners(list);
		}
		return list;
	}

	/**
	 * Set paxml process execution listeners list.
	 * 
	 * @param paxmlExecutionListeners
	 *            the list, null to remove the list.
	 * @return the existing list.
	 */
	public List<IExecutionListener> setPaxmlExecutionListeners(List<IExecutionListener> paxmlExecutionListeners) {
		if (paxmlExecutionListeners == null) {
			return (List<IExecutionListener>) removeInternalObject(PrivateKeys.PAXML_LISTENER, true);
		} else {
			return (List<IExecutionListener>) setInternalObject(PrivateKeys.PAXML_LISTENER, paxmlExecutionListeners, true);
		}
	}

	/**
	 * Get the entity execution listeners list.
	 * 
	 * @param autoCreate
	 *            true to create one if not existing yet, false to return null
	 *            if not existing yet.
	 * @return the list.
	 */
	public List<IEntityExecutionListener> getEntityExecutionListeners(boolean autoCreate) {
		List<IEntityExecutionListener> list = (List<IEntityExecutionListener>) getInternalObject(PrivateKeys.ENTITY_LISTENER, true);
		if (list == null && autoCreate) {
			list = new ArrayList<IEntityExecutionListener>();
			setEntityExecutionListeners(list);
		}
		return list;
	}

	/**
	 * Set paxml entity execution listeners list.
	 * 
	 * @param entityExecutionListeners
	 *            the list, null to remove the list.
	 * @return the existing list.
	 */
	public List<IEntityExecutionListener> setEntityExecutionListeners(List<IEntityExecutionListener> entityExecutionListeners) {
		if (entityExecutionListeners == null) {
			return (List<IEntityExecutionListener>) removeInternalObject(PrivateKeys.ENTITY_LISTENER, true);
		} else {
			return (List<IEntityExecutionListener>) setInternalObject(PrivateKeys.ENTITY_LISTENER, entityExecutionListeners, true);
		}
	}

	/**
	 * Get the tag execution listeners list.
	 * 
	 * @param autoCreate
	 *            true to create one if not existing yet, false to return null
	 *            if not existing yet.
	 * @return the list.
	 */
	public List<ITagExecutionListener> getTagExecutionListeners(boolean autoCreate) {
		List<ITagExecutionListener> list = (List<ITagExecutionListener>) getInternalObject(PrivateKeys.TAG_LISTENER, true);
		if (list == null && autoCreate) {
			list = new ArrayList<ITagExecutionListener>();
			setTagExecutionListeners(list);
		}
		return list;
	}

	/**
	 * Set paxml tag execution listeners list.
	 * 
	 * @param tagExecutionListeners
	 *            the list, null to remove the list.
	 * @return the existing list.
	 */
	public List<ITagExecutionListener> setTagExecutionListeners(List<ITagExecutionListener> tagExecutionListeners) {
		if (tagExecutionListeners == null) {
			return (List<ITagExecutionListener>) removeInternalObject(PrivateKeys.TAG_LISTENER, true);
		} else {
			return (List<ITagExecutionListener>) setInternalObject(PrivateKeys.TAG_LISTENER, tagExecutionListeners, true);
		}
	}

	/**
	 * Get the ids of the consts.
	 * 
	 * @return a read only id set, never null
	 */
	public Set<String> getConstIds() {
		return Collections.unmodifiableSet(idConstsMap.keySet());
	}

	/**
	 * Get a copy of the map keyed with tag names.
	 * 
	 * @param mergeParents
	 *            true to merge with the map from parent context, false not to.
	 * @param includesRoot
	 *            true to include the map from the root context, false not to.
	 *            If the current is already root context, set to false will
	 *            result in empty map being returned.
	 * @return the map copy.
	 */
	public ObjectTree getNameMap(boolean mergeParents, boolean includesRoot) {
		ObjectTree tree = new ObjectTree(null);
		if (includesRoot || !isRoot()) {
			for (Map.Entry<String, Object> entry : idConstsMap.entrySet()) {
				Object value = entry.getValue();
				String id = entry.getKey();

				String tagName = idToTagName.get(id);
				if (tagName != null) {
					tree.addValue(tagName, value);
				}

			}
		}

		if (mergeParents && parent != null) {

			tree.addValues(parent.getNameMap(mergeParents, includesRoot));

		}
		return tree;
	}

	/**
	 * Get a copy of the map keyed with ids.
	 * 
	 * @param mergeParents
	 *            true to merge with the map from parent context, false not to.
	 * @param includesRoot
	 *            true to include the map from the root context, false not to.
	 *            If the current is already root context, set to false will
	 *            result in empty map being returned.
	 * @return the map copy.
	 */
	public Map<String, Object> getIdMap(boolean mergeParents, boolean includesRoot) {
		if (mergeParents) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (parent != null && (includesRoot || parent != root)) {
				map.putAll(parent.getIdMap(mergeParents, includesRoot));
			}
			if (includesRoot || !isRoot()) {
				map.putAll(idConstsMap);
			}
			return map;
		} else {
			if (includesRoot || !isRoot()) {
				return new LinkedHashMap<String, Object>(idConstsMap);
			} else {
				return new LinkedHashMap<String, Object>(0);
			}

		}
	}

	/**
	 * Check if the current context is a root context which has no parent.
	 * 
	 * @return true yes, false no.
	 */
	public boolean isRoot() {
		return this == root;
	}

	/**
	 * Select objects with xpath.
	 * 
	 * @param from
	 *            the object to select properties from, null to select from
	 *            entire context.
	 * @param xpath
	 *            the xpath
	 * @return either a list of objects that satisfies the xpath, or the object
	 *         itself if the xpath results in one object to be selected.
	 */
	public Object xpathSelect(Object from, String xpath) {
		return xpathSelect(from, xpath, false);
	}

	/**
	 * Select objects with xpath.
	 * 
	 * @param xpath
	 *            the xpath
	 * @return either a list of objects that satisfies the xpath, or the object
	 *         itself if the xpath results in one object to be selected.
	 */
	public Object xpathSelect(String xpath) {
		return xpathSelect(null, xpath);
	}

	/**
	 * Select objects with xpath.
	 * 
	 * 
	 * @param xpath
	 *            the xpath
	 * @param alwaysList
	 *            true to return a list with one item inside if the xpath
	 *            results in one object to be selected.
	 * @return either a list of objects that satisfies the xpath, or the object
	 *         itself if the xpath results in one object to be selected and the
	 *         "alwaysList" parameter is false.
	 */
	public Object xpathSelect(String xpath, boolean alwaysList) {
		return xpathSelect(null, xpath, alwaysList);
	}

	/**
	 * Select objects with xpath.
	 * 
	 * @param from
	 *            the object to select properties from, null to select from
	 *            entire context.
	 * @param xpath
	 *            the xpath
	 * @param alwaysList
	 *            true to return a list with one item inside if the xpath
	 *            results in one object to be selected.
	 * @return either a list of objects that satisfies the xpath, or the object
	 *         itself if the xpath results in one object to be selected and the
	 *         "alwaysList" parameter is false.
	 */
	public Object xpathSelect(Object from, String xpath, boolean alwaysList) {
		Variables vars = new BasicVariables();

		if (from == null) {
			ObjectTree nameGlobal = getRootContext().getNameMap(false, true);
			ObjectTree nameLocal = getNameMap(true, false);

			vars.declareVariable(XPATH_NAME_GLOBAL_VAR, nameGlobal);
			vars.declareVariable(XPATH_NAME_LOCAL_VAR, nameLocal);

			ObjectTree nameAuto = new ObjectTree(null, nameGlobal);
			nameAuto.addValues(nameLocal);

			from = nameAuto;
		}

		JXPathContext xpathContext = JXPathContext.newContext(from);
		xpathContext.setVariables(vars);
		xpathContext.setIdentityManager(this);

		setXpathFunctions(xpathContext);

		try {
			Object selected = xpathContext.iterate(xpath);

			List<Object> list = new ArrayList<Object>(1);
			if (selected instanceof Iterator) {
				final Iterator<?> it = (Iterator<?>) selected;
				while (it.hasNext()) {
					Object obj = it.next();
					list.add(getXpathResultObject(obj));
				}
				if (list.size() == 1) {
					selected = list.get(0);
				} else {
					selected = list;
				}
			} else {

				selected = getXpathResultObject(selected);

				if (selected != null && alwaysList) {
					list.add(selected);
				}
			}
			if (alwaysList) {
				return list;
			} else {
				if (selected instanceof List) {
					list = (List) selected;
					final int size = list.size();
					if (size == 0) {
						return null;
					} else if (size == 1) {
						return list.get(0);
					}
				}
				return selected;
			}
		} catch (NullPointerException e) {
			// when jxpath throws null pointer exception, it has problem
			// searching non-existing paths
			return null;
		}
	}

	/**
	 * {@inheritDoc}. Find a const's id.
	 */
	@Override
	public Pointer getPointerByID(JXPathContext xpc, String id) {

		Object value = getConst(id, true);
		if (value == null) {
			return new NullPointer(null, id);
		} else {
			return new BeanPointerFactory().createNodePointer(null, value, null);
			// return new BeanPointer(null, value,
			// JXPathIntrospector.getBeanInfo(value.getClass()), null);
		}
	}

	private void setXpathFunctions(JXPathContext xpathContext) {
		Functions existing = xpathContext.getFunctions();
		final FunctionLibrary funcLib;
		if (existing == null) {
			funcLib = new FunctionLibrary();
		} else if (existing instanceof FunctionLibrary) {
			funcLib = (FunctionLibrary) existing;
		} else {
			funcLib = new FunctionLibrary();
			funcLib.addFunctions(existing);
		}

		for (ITagLibrary lib : getPaxml().getParser().getTagLibraries()) {
			for (String name : lib.getUtilFunctionsFactoryNames()) {
				Class<? extends IUtilFunctionsFactory> clazz = lib.getUtilFunctionsFactory(name);
				Class<?> xpathFunClass = ReflectUtils.createObject(clazz).getXpathUtilFunctions(this);
				if (xpathFunClass == null) {
					// skip this one
					continue;
				}
				Util util = ReflectUtils.getAnnotation(clazz, Util.class);
				if (util == null) {
					throw new PaxmlRuntimeException("Internal error: util function factory is not annotated: " + clazz.getName());
				}
				funcLib.addFunctions(new ClassFunctions(xpathFunClass, util.value()));
			}
		}

		xpathContext.setFunctions(funcLib);
	}

	private Object getXpathResultObject(Object obj) {
		if (obj instanceof Pointer) {
			obj = ((Pointer) obj).getValue();
		}

		return obj;
	}

	/**
	 * Push an tag into execution stack.
	 * 
	 * @param tag
	 *            the tag
	 */
	public void pushStack(ITag tag) {

		getStack().push(tag);
	}

	/**
	 * Pop a tag from execution stack.
	 * 
	 */
	public void popStack() {
		getStack().pop();
	}

	/**
	 * Get the current execution stack from context.
	 * 
	 * @return the current stack from the execution context, never null
	 */
	public Stack getStack() {
		Stack stack = (Stack) getInternalObject(PrivateKeys.STACK, true);

		if (stack == null) {
			stack = new Stack();
			setInternalObject(PrivateKeys.STACK, stack, true);
		}
		return stack;
	}

	/**
	 * Set the invocation result.
	 * 
	 * @param obj
	 *            the result
	 */
	public void setInvocationResult(Object obj) {
		setInternalObject(PrivateKeys.RESULT, obj, true);
	}

	/**
	 * Get the current invocation result.
	 * 
	 * @return the current result
	 */
	public Object getInvocationResult() {
		return getInternalObject(PrivateKeys.RESULT, true);
	}

	/**
	 * Get the associated paxml object from global context.
	 * 
	 * @return the paxml, or null if not found.
	 */
	public Paxml getPaxml() {
		return (Paxml) getInternalObject(PrivateKeys.PAXML, true);
	}

	/**
	 * Set the associated paxml object in global context.
	 * 
	 * @param paxml
	 *            the paxml
	 */
	public void setPaxml(Paxml paxml) {
		setInternalObject(PrivateKeys.PAXML, paxml, true);
	}

	/**
	 * Set the context which originally throws the exception.
	 * 
	 * @param context
	 *            the context
	 */
	public void setExceptionContext(Context context) {
		setInternalObject(PrivateKeys.EXCEPTION_CONTEXT, context, true);
	}

	/**
	 * Get the context where the exception was originally created.
	 * 
	 * @return the context, or null if no exceptions
	 */
	public Context getExceptionContext() {
		return (Context) getInternalObject(PrivateKeys.EXCEPTION_CONTEXT, true);
	}

	/**
	 * Get the locale associated with the context and its parents.
	 * 
	 * @return the associated locale, null if not associated.
	 */
	public Locale getLocale() {
		Locale loc = (Locale) getInternalObject(PrivateKeys.LOCALE, true);
		return loc;
	}

	/**
	 * Set the locale associated with the context and its parents.
	 * 
	 * @param locale
	 *            the locale
	 */
	public void setLocale(Locale locale) {
		setInternalObject(PrivateKeys.LOCALE, locale, true);
	}

	/**
	 * Get all consts in a map.
	 * 
	 * @return a read only map
	 */
	public Map<String, Object> getConsts() {
		return Collections.unmodifiableMap(idConstsMap);
	}

	/**
	 * Get the mapping from const ids to root tag names.
	 * 
	 * @return a read only map, where keys are const ids, values are
	 *         corresponding root tag names.
	 */
	public Map<String, String> getConstIdToRootNameMapping() {
		return Collections.unmodifiableMap(idToTagName);
	}

	/**
	 * Get the internal map for holding the consts. NB! be careful when
	 * modifying it.
	 * 
	 * @return the map
	 */
	public ObjectTree getIdConstsMap() {
		return idConstsMap;
	}

	/**
	 * Get the paxml entity for which the context is created.
	 * 
	 * @return the paxml entity or null if it is root context
	 */

	public IEntity getEntity() {
		return entity;
	}

	/**
	 * Set the paxml entity that the context is created for.
	 * 
	 * @param entity
	 *            the paxml entity
	 */
	public void setEntity(IEntity entity) {
		this.entity = entity;
	}

	/**
	 * Find the context created for the paxml entity. The search order is from
	 * the current context up till the root context.
	 * 
	 * @param e
	 *            the paxml entity
	 * @return the context created for the given entity
	 */
	public Context findContextForEntity(final IEntity e) {

		Context context = this;

		do {
			if (e == context.entity) {
				return context;
			}
			context = context.parent;
		} while (context != null);

		return null;
	}

	/**
	 * Find the caller's entity.
	 * 
	 * @return the caller file's entity, null if has no caller.
	 */
	public IEntity findCallerEntity() {
		final IEntity e = getCurrentTag().getEntity();
		Context context = findContextForEntity(e);
		if (context == null) {
			return null;
		}
		context = context.parent;
		while (!context.isRoot()) {
			if (null != context.entity) {
				return context.entity;
			}
			context = context.parent;
		}
		return null;
	}

	/**
	 * Find the caller entity's context.
	 * 
	 * @return the caller entity's context, null if has no caller.
	 */
	public Context findCallerContext() {
		final IEntity e = getCurrentTag().getEntity();
		Context context = findContextForEntity(e);
		if (context == null) {
			return null;
		}
		context = context.parent;
		while (!context.isRoot()) {
			if (null != context.entity) {
				return context;
			}
			context = context.parent;
		}
		return null;
	}

	/**
	 * Get the current tag being executed.
	 * 
	 * @return the current tag, never null.
	 */
	public ITag getCurrentTag() {
		Stack stack = getStack();
		if (stack.isEmpty()) {
			throw new PaxmlRuntimeException("No current tag!");
		}
		return stack.getFirst();
	}

	/**
	 * Get the current entity being executed.
	 * 
	 * @return the current entity, never null
	 */
	public IEntity getCurrentEntity() {
		return getCurrentTag().getEntity();
	}

	/**
	 * Get the current entity's context.
	 * 
	 * @return the context, never null
	 */
	public Context getCurrentEntityContext() {
		return findContextForEntity(getCurrentEntity());
	}

	/**
	 * Get the default parameter named "value".
	 * 
	 * @return the default param or null if not given.
	 */
	public Object getDefaultParameter() {
		return getConst(DEFAULT_VALUE_NAME, false);
	}

	/**
	 * Check if it a const is overwritable in the current context.
	 * 
	 * @return true if overwritable, false not.
	 */
	public boolean isConstOverwritable() {
		Boolean yes = (Boolean) getLocalInternalObject(PrivateKeys.CONST_OVERWRITABLE, false);
		return yes != null && yes;
	}

	/**
	 * Set in the current context for a const being overwritable.
	 * 
	 * @param yes
	 *            true overwritable, false not
	 */
	public void setConstOverwritable(boolean yes) {
		setInternalObject(PrivateKeys.CONST_OVERWRITABLE, yes, false);
	}

	/**
	 * Get the const ids that are loaded as property.
	 * 
	 * @param searchParent
	 *            true to search parent context, false not to
	 * @return the set of ids, never null.
	 */
	public Set<String> getPropertyConstIds(boolean searchParent) {
		Set<String> set = getLocalPropertyConstIds();
		if (searchParent && !isRoot()) {
			set.addAll(parent.getPropertyConstIds(searchParent));
		}
		return set;
	}

	/**
	 * Add a const id as property const.
	 * 
	 * @param id
	 *            the id
	 */
	public void addPropertyConstId(String id) {
		getLocalPropertyConstIds().add(id);
	}

	private Set<String> getLocalPropertyConstIds() {
		Set<String> set = (Set<String>) getLocalInternalObject(PrivateKeys.PROPERTIES, false);
		if (set == null) {
			set = new LinkedHashSet<String>();
			setInternalObject(PrivateKeys.PROPERTIES, set, false);
		}
		return set;
	}

	/**
	 * Get the parent context.
	 * 
	 * @return the parent
	 */
	public Context getParent() {
		return this.parent;
	}

	public boolean isReturning() {
		return returning;
	}

	public void setReturning(boolean returning) {
		this.returning = returning;
	}

	public void registerCloseable(Closeable... Closeables) {
		List<Closeable> list = (List) getInternalObject(PrivateKeys.CLOSEABLES, true);
		if (list == null) {
			list = new ArrayList<Closeable>();
			setInternalObject(PrivateKeys.CLOSEABLES, list, true);
		}
		for (Closeable c : Closeables) {
			list.add(c);
		}
	}

	public void closeAllCloseables() {
		List<Closeable> list = (List) getInternalObject(PrivateKeys.CLOSEABLES, true);
		if (list != null) {
			for (Iterator<Closeable> it = list.iterator(); it.hasNext();) {
				Closeable c = it.next();
				try {
					if (c instanceof Flushable) {
						((Flushable) c).flush();
					}
				} catch (IOException e) {
					// do nothing because it could be already closed.
				} finally {
					try {
						c.close();
						it.remove();
					} catch (Exception e) {
						// say nothing about it because it could be already
						// closed
						// by anything else
					}
				}
			}
		}
	}

	/**
	 * Get the only file in the context.
	 * 
	 * @return the file or null if no file at all or more than 1 file is in the
	 *         context
	 */
	public IFile getOnlyFile() {
		Map<String, IFile> map = getFiles();
		if (map.size() == 1) {
			return map.values().iterator().next();
		}
		return null;

	}

	private Map<String, IFile> getFiles() {
		Map<String, IFile> map = (Map<String, IFile>) getInternalObject(PrivateKeys.FILES, true);
		if (map == null) {
			map = new LinkedHashMap<String, IFile>();
			setInternalObject(PrivateKeys.FILES, map, true);
		}
		return map;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public IFile getFile(Resource res) {
		String key = PaxmlUtils.getResourceFile(res);
		return getFiles().get(key);
	}

	public IFile getFile(File file) {
		return getFile(new FileSystemResource(file).getSpringResource());
	}

	public IFile addFile(Resource res, IFile file) {
		String key = PaxmlUtils.getResourceFile(res);
		return getFiles().put(key, file);
	}

	public long getId() {
		return id;
	}

	public String getSecret(String name) {
		String pwd = UserKeyRepository.getCurrentUserMasterKey();
		if (pwd == null) {
			throw new PaxmlRuntimeException("No key store password given!");
		}
		return CryptoUtils.getKey(null, pwd, name, null);
	}

	public void setSecret(String name, String value) {
		String pwd = UserKeyRepository.getCurrentUserMasterKey();
		if (pwd == null) {
			throw new PaxmlRuntimeException("No key store password given!");
		}
		CryptoUtils.setKey(null, pwd, name, null, value);
	}

	public String dump() {
		return XmlUtils.toXml(this, "context", null);
	}

}
