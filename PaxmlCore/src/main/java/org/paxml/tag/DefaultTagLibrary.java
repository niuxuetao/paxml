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
package org.paxml.tag;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.annotation.Util;
import org.paxml.core.Namespaces;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.IUtilFunctionsFactory;
import org.paxml.util.ReflectUtils;

/**
 * Default impl of tag library.
 * 
 * @author Xuetao Niu
 * 
 */
public class DefaultTagLibrary implements ITagLibrary {
	private static final Log log = LogFactory.getLog(DefaultTagLibrary.class);
	private final Map<String, Class<? extends IUtilFunctionsFactory>> utils = new ConcurrentHashMap<String, Class<? extends IUtilFunctionsFactory>>();
	private final Map<String, Class<? extends ITag>> tags = new ConcurrentHashMap<String, Class<? extends ITag>>(0);

	private final Set<String> analyzedMethods = new HashSet<String>();

	/**
	 * {@inheritDoc}
	 */
	public Class<? extends ITag> getTagImpl(String tagName) {
		return tags.get(tagName);
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<? extends IUtilFunctionsFactory> getUtilFunctionsFactory(String name) {
		return utils.get(name);
	}

	public void registerTag(Class clazz) {
		registerClassTag(clazz);
		registerMethodTag(clazz);
	}

	private void registerMethodTag(Class clazz) {
		
	}

	private void registerClassTag(Class clazz) {
		List<String> names = getTagNames(clazz);
		if (names == null || names.isEmpty()) {

			throw new PaxmlRuntimeException("Class " + clazz.getName() + " has no 'name' property given in its own nor its super classes/interfaces' @" + Tag.class.getSimpleName()
			        + " annotation");

		}
		for (String tagName : names) {
			tags.put(tagName, clazz);
		}
	}

	public void registerUtil(Class<? extends IUtilFunctionsFactory> clazz) {
		String utilName = getUtilName(clazz);
		if (StringUtils.isBlank(utilName)) {
			throw new PaxmlRuntimeException("Class " + clazz.getName() + " has no value given in the @" + Util.class.getSimpleName() + " annotation.");
		}
		Class<? extends IUtilFunctionsFactory> existing = utils.put(utilName, clazz);
		if (existing != null) {
			log.warn("Util functions named " + utilName + " from class " + existing.getName() + " is overridded by another one from class " + clazz.getName());
		}
	}

	private static String getUtilName(Class<? extends IUtilFunctionsFactory> clazz) {
		Util a = clazz.getAnnotation(Util.class);
		if (a != null) {
			return a.value();
		}
		return null;
	}

	private static List<String> getTagNames(Class clazz) {
		List<String> result = new ArrayList<String>(1);

		// find class level annotation
		Tag a = (Tag) clazz.getAnnotation(Tag.class);
		if (a != null) {
			result.add(a.name());
			String[] alias = a.alias();
			if (alias != null && alias.length > 0) {
				result.addAll(Arrays.asList(alias));
			}
		} else {
			Class c = clazz.getSuperclass();
			if (ReflectUtils.isImplementingClass(c, ITag.class, true)) {
				return getTagNames(c);
			} else {
				return null;
			}
		}

		return result;
	}

	public Set<String> getUtilFunctionsFactoryNames() {
		return Collections.unmodifiableSet(utils.keySet());
	}

	@Override
	public String getNamespaceUri() {
		return Namespaces.ROOT;
	}

}
