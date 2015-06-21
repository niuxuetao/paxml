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

import java.util.LinkedList;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.IEntity;
import org.paxml.core.IEntityFactory;
import org.paxml.core.IParserContext;
import org.paxml.core.Namespaces;
import org.paxml.core.Parser;
import org.paxml.core.PaxmlParseException;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.core.ResourceLocator;
import org.paxml.launch.Paxml;
import org.paxml.tag.invoker.FileInvokerTagFactory;
import org.paxml.util.Elements;

/**
 * The base impl for paxml entity factories.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class AbstractPaxmlEntityFactory implements IEntityFactory {

	private static final Log log = LogFactory.getLog(AbstractPaxmlEntityFactory.class);
	
	/**
	 * Default impl of the parser context.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static final class ParserContext implements IParserContext {
		private final LinkedList<IParserContext> stack;
		private OMElement element;
		private PaxmlResource resource;
		private ResourceLocator locator;
		private IEntity entity;
		private ITag parentTag;

		private ParserContext(final LinkedList<IParserContext> stack, final OMElement element, final PaxmlResource resource, final ResourceLocator locator, final IEntity entity,
		        final ITag parentTag) {

			this.stack = stack == null ? new LinkedList<IParserContext>() : stack;
			this.stack.push(this);
			this.element = element;
			this.resource = resource;
			this.locator = locator;
			this.entity = entity;
			this.parentTag = parentTag;

		}

		public LinkedList<IParserContext> getParserStack() {
			return stack;
		}

		public void discard() {
			stack.pop();
		}

		public OMElement getElement() {
			return element;
		}

		public void setElement(OMElement element) {
			this.element = element;
		}

		public PaxmlResource getResource() {
			return resource;
		}

		public void setResource(PaxmlResource resource) {
			this.resource = resource;
		}

		public ResourceLocator getLocator() {
			return locator;
		}

		public void setLocator(ResourceLocator locator) {
			this.locator = locator;
		}

		public IEntity getEntity() {
			return entity;
		}

		public void setEntity(IEntity entity) {
			this.entity = entity;
		}

		public ITag getParentTag() {
			return parentTag;
		}

		public void setParentTag(ITag parentTag) {
			this.parentTag = parentTag;
		}

		public Parser getParser() {
			return locator.getParser();
		}

		public ParserContext copy() {

			return (ParserContext) createParserContext(element, parentTag, resource, locator, entity, stack);
		}

		@Override
		public String toString() {
			return element.toString();
		}

	}

	/**
	 * Create a new parser context from parent context.
	 * 
	 * @param context
	 *            parent parser context which cannot be null.
	 * @param targetResource
	 *            the target paxml resource
	 * @return the new parser context which is not null
	 */
	public static IParserContext createTargetParserContext(IParserContext context, PaxmlResource targetResource) {
		ParserContext contextImpl = (ParserContext) context;
		ParserContext newContext = contextImpl.copy();
		contextImpl.getParserStack().push(newContext);
		newContext.setResource(targetResource);
		return newContext;
	}

	/**
	 * Create a new parser context from factors.
	 * 
	 * @param ele
	 *            the xml element
	 * @param resource
	 *            the paxml resource
	 * @param locator
	 *            the paxml resource locator
	 * @return the new parser context which is not null
	 */
	public static IParserContext createParserContext(OMElement ele, PaxmlResource resource, ResourceLocator locator) {
		return createParserContext(ele, null, resource, locator, null, null);
	}

	/**
	 * Create a new parser context from factors.
	 * 
	 * @param ele
	 *            the xml element
	 * @param parentTag
	 *            the parent tag
	 * @param resource
	 *            the paxml resource
	 * @param locator
	 *            the paxml resource locator
	 * @param entity
	 *            the paxml entity
	 * @param stack
	 *            the stack of parser contexts
	 * @return the new parser context which is not null
	 */
	public static IParserContext createParserContext(OMElement ele, ITag parentTag, PaxmlResource resource, ResourceLocator locator, IEntity entity,
	        LinkedList<IParserContext> stack) {
		return new ParserContext(stack, ele, resource, locator, entity, parentTag);
	}

	/**
	 * Create paxml entity from xml root tag.
	 * 
	 * @param root
	 *            the root tag
	 * @param context
	 *            the parser context
	 * @return the new entity, never null
	 */
	protected abstract AbstractPaxmlEntity doCreate(OMElement root, IParserContext context);

	/**
	 * {@inheritDoc}
	 */
	public final IEntity create(OMElement root, IParserContext context) {

		ParserContext contextImpl = (ParserContext) context;
		final ResourceLocator locator = contextImpl.getLocator();
		final PaxmlResource resource = contextImpl.getResource();

		contextImpl.setElement(root);
		contextImpl.setResource(resource);
		contextImpl.setLocator(locator);

		try {

			final AbstractPaxmlEntity entity = doCreate(root, contextImpl);

			entity.setTimestamp(resource.getLastModified());
			entity.setTagName(root.getLocalName());
			entity.setLineNumber(root.getLineNumber());
			entity.setResource(resource);
			entity.setEntity(entity);

			contextImpl.setEntity(entity);
			contextImpl.setParentTag(entity);

			DefaultTagFactory.processExpressions(entity, contextImpl);

			createChildren(contextImpl);

			contextImpl.discard();

			return entity;
		} catch (Exception e) {
			IParserContext deepest = contextImpl.getParserStack().getFirst();
			OMElement ele = deepest.getElement();
			throw new PaxmlParseException("Cannot parse tag <" + ele.getLocalName() + "> at line: " + ele.getLineNumber() + ", resource: " + deepest.getResource()
			        + "\r\nBecause: " + Paxml.getCause(e), e);
		}

	}

	/**
	 * Check if a tag is created by a tag factory type
	 * 
	 * @param ele
	 *            the tag
	 * @param context
	 *            the parser context
	 * @param expectedType
	 *            the type of tag factory
	 * @return true yes, false no.
	 */
	public static boolean isCreatedByTagFactory(OMElement ele, IParserContext context, Class<? extends ITagFactory> expectedType) {
		ParserContext contextImpl = (ParserContext) context;
		final PaxmlResource res = contextImpl.getResource();
		ITagFactory fact = getTagFactory(ele, contextImpl);
		contextImpl.setResource(res);
		return expectedType.isInstance(fact);

	}

	/**
	 * Get the tag impl class from xml tag.
	 * 
	 * @param ele
	 *            the xml tag
	 * @param context
	 *            the parser context. The resource of the context may be changed
	 *            if the tag impl is an invoker. In this case the resource of
	 *            the context will be changed to the target resource.
	 * @return never null.
	 */
	private static ITagFactory getTagFactory(OMElement ele, ParserContext context) {

		final String tagName = ele.getLocalName();
		ITagFactory fact = null;

		final String ns = ele.getNamespaceURI();
		if (null == ns) {
			fact = getTagFactoryFromTagLibs(context, null, tagName);
			if (fact == null) {
				fact = getTagFactoryFromResources(context, null, tagName);
			}
		} else if (StringUtils.isBlank(ns) || Namespaces.DATA.equals(ns)) {
			fact = InternalTagLibrary.CONST_TAG_FACTORY;
		} else if (Namespaces.COMMAND.equals(ns)) {
			fact = getTagFactoryFromTagLibs(context, null, tagName);
		} else if (Namespaces.FILE.equals(ns)) {
			fact = getTagFactoryFromResources(context, null, tagName);
		} else {
			// plugins
			// first load from java class
			fact = getTagFactoryFromTagLibs(context, ns, tagName);
			if (fact == null) {
				// if not found, load from resource
				fact = getTagFactoryFromResources(context, ns, tagName);
			}
		}

		if (fact == null) {
			if (StringUtils.isBlank(ns)) {
				throw new PaxmlRuntimeException("Cannot recognize tag '" + ele.getLocalName());
			} else {
				throw new PaxmlRuntimeException("Cannot recognize tag '" + ele.getLocalName() + "' under namespace: " + ns);
			}
		}
		return fact;
	}

	private static ITagFactory getTagFactoryFromTagLibs(ParserContext context, String ns, String tagName) {
		// search from tag libs
		Parser parser = context.getParser();
		for (ITagLibrary lib : parser.getTagLibraries()) {
			if (ns != null && !ns.equals(lib.getNamespaceUri())) {
				continue;
			}
			ITagFactory fact = lib.getFactory(tagName);

			if (fact != null) {
				return fact;
			}
		}
		return null;
	}

	private static ITagFactory getTagFactoryFromResources(ParserContext context, String ns, String tagName) {
		ITagFactory fact = null;
		ResourceLocator locator = context.getLocator();
		// search from resources
		PaxmlResource tagRes = locator.getResource(tagName);
		if (tagRes == null) {
			// this tag is unknown, regarded as a const tag
			fact = InternalTagLibrary.CONST_TAG_FACTORY;
		} else {
			// this is a xml defined tag, create a file invoker
			fact = InternalTagLibrary.FILE_INVOKER_TAG_FACTORY;
			context.setResource(tagRes);
		}
		return fact;
	}

	private void createChildren(ParserContext context) {

		for (OMElement child : new Elements(context.getElement())) {
			final ParserContext newContext = context.copy();
			newContext.setElement(child);
			ITagFactory fact = getTagFactory(child, newContext);
			createTagObjectFull(fact, child, newContext);
			newContext.discard();
		}
	}

	private ITag createTagObjectFull(ITagFactory fact, OMElement ele, IParserContext context) {

		ParserContext implContext = (ParserContext) context;

		final TagCreationResult<ITag> result = fact.create(ele, context);
		final ITag obj = result.getTagObject();

		if (!result.isChildrenParsed()) {
			ParserContext newContext = implContext.copy();
			newContext.setParentTag(obj);
			createChildren(newContext);
			newContext.discard();
		}

		return obj;
	}

}
