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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.launch.Paxml;
import org.paxml.tag.AbstractPaxmlEntityFactory;
import org.paxml.tag.ITagLibrary;
import org.paxml.tag.InternalTagLibrary;
import org.paxml.util.AxiomUtils;

/**
 * Parser for all paxml resources to convert them into paxml entities.
 * 
 * @author Xuetao Niu
 * 
 */
public class Parser {
	private static final Log log = LogFactory.getLog(Parser.class);
	private final ResourceLocator resourceLocator;
	private final EntityFactoryRegistry registry;
	private final Paxml paxml;
	private final LinkedList<ITagLibrary> tagLibraries = new LinkedList<ITagLibrary>();
	{
		// add the default tag library
		tagLibraries.add(InternalTagLibrary.INSTANCE);

	}

	/**
	 * Construct with given entity factory registry and paxml resource locator.
	 * 
	 * @param registry
	 *            the entity factory registry
	 * @param locator
	 *            the paxml resource locator
	 */
	public Parser(final Paxml paxml, final EntityFactoryRegistry registry, final ResourceLocator locator) {
		this.paxml = paxml;
		this.registry = registry;
		this.resourceLocator = locator;
		locator.setParser(this);
	}

	public Paxml getPaxml() {
		return paxml;
	}

	/**
	 * Add a tag library.
	 * 
	 * @param lib
	 *            the tag library
	 * @param last
	 *            true to append to lib list, false to put as 1st in list.
	 */
	public void addTagLibrary(ITagLibrary lib, boolean last) {
		if (last) {
			tagLibraries.addLast(lib);
		} else {
			tagLibraries.addFirst(lib);
		}
	}

	/**
	 * Remove all tag libs with a certain class.
	 * 
	 * @param libClass
	 *            the tag lib class
	 * @param inheritance
	 *            true to also consider subclasses of the given lib class, false
	 *            to do exact class match.
	 */
	public void removeTagLibrary(Class<? extends ITagLibrary> libClass, boolean inheritance) {
		for (Iterator<ITagLibrary> it = tagLibraries.iterator(); it.hasNext();) {
			ITagLibrary lib = it.next();
			if (inheritance) {
				if (libClass.isInstance(lib)) {
					it.remove();
				}
			} else {
				if (libClass.equals(lib.getClass())) {
					it.remove();
				}
			}
		}
	}

	public List<ITagLibrary> getTagLibraries() {
		return tagLibraries;
	}

	/**
	 * Parse paxml resource to construct paxml entity.
	 * 
	 * @param resource
	 *            the paxml resource
	 * @param forceRefresh
	 *            true to not use cached parse result, false to use cached parse
	 *            result.
	 * @param context
	 *            the parse context, set to null if the context is not known.
	 * @return the paxml resource, null if the root tag is unknown.
	 */
	public IEntity parse(final PaxmlResource resource, boolean forceRefresh, IParserContext context) {
		IEntity entity = null;
		if (context != null) {
			entity = detectCircle(context.getParserStack(), resource);
			if (entity != null) {
				return entity;
			}
		}

		final IEntity cached;
		if (forceRefresh || (cached = this.resourceLocator.getCachedPaxmlEntities().get(resource)) == null || cached.isModified()) {

			if (resource.getSpringResource().exists()) {
				InputStream in = null;
				try {

					in = resource.openInputStream();
					OMElement root = AxiomUtils.getRootElement(in);
					try {
						entity = parseXml(root, resource, context);
					} finally {
						root.close(false);
					}
					if (entity != null && entity.isCachable() && StringUtils.isNotBlank(resource.getName())) {
						resourceLocator.getCachedPaxmlEntities().put(resource, entity);
					}

				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		} else {

			entity = cached;
		}

		return entity;
	}

	/**
	 * Parse an xml root element.
	 * 
	 * @param root
	 *            the root ele
	 * @param resource
	 *            the resource containing the root ele
	 * @param context
	 *            the parser context
	 * @return the paxml entity, return null if root tag is unknown.
	 */
	public IEntity parseXml(OMElement root, PaxmlResource resource, IParserContext context) {
		if (context == null) {
			context = AbstractPaxmlEntityFactory.createParserContext(root, resource, getResourceLocator());
		}
		String tagName = root.getQName().getLocalPart();

		IEntityFactory factory = registry.lookup(tagName);
		if (factory == null) {
			if (log.isWarnEnabled()) {
				log.warn("Entity factory not found for root tag <" + tagName + "> in resource: " + resource);
			}
			return null;
		}

		IEntity entity = factory.create(root, context);

		return entity;
	}

	private IEntity detectCircle(LinkedList<IParserContext> stack, PaxmlResource resource) {
		for (IParserContext c : stack) {
			if (resource.equals(c.getResource())) {
				return c.getEntity();
			}
		}
		return null;
	}

	private List<IParserContext> findCircle(LinkedList<IParserContext> stack, PaxmlResource resource) {
		List<IParserContext> list = null;
		for (IParserContext context : stack) {
			PaxmlResource res = context.getResource();
			if (list == null && res.equals(resource)) {
				list = new ArrayList<IParserContext>(2);
			}
			if (list != null) {
				list.add(context);
			}
		}

		return list;
	}

	public ResourceLocator getResourceLocator() {
		return resourceLocator;
	}

	public EntityFactoryRegistry getRegistry() {
		return registry;
	}

}
