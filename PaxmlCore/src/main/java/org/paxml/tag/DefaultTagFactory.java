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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.axiom.om.OMElement;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlParseException;

/**
 * This is the annotation-based impl of a tag factory.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the type of tag this factory produces.
 */
public abstract class DefaultTagFactory<T extends ITag> extends AbstractTagFactory<T> {
	private final ConcurrentMap<String, Class<? extends ITag>> tagImpl = new ConcurrentHashMap<String, Class<? extends ITag>>();

	public void registerTag(String tagName, Class<? extends ITag> impl) {
		tagImpl.put(tagName, impl);
	}

	@Override
	protected T createTagInstance(OMElement ele, IParserContext context) {
		Class<? extends ITag> clazz = tagImpl.get(ele.getLocalName());
		if (clazz == null) {
			// detect from the actual generic type
			try {
				TypeVariable[] types = getClass().getTypeParameters();
				clazz = (Class) types[0].getBounds()[0];
			} catch (Exception e) {

				ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
				clazz = (Class) genericSuperclass.getActualTypeArguments()[0];
			}
		}
		try {
			return (T) clazz.newInstance();
		} catch (Exception e) {
			throw new PaxmlParseException("Cannot create instance from class: " + clazz.getName(), e);
		}
	}

}
