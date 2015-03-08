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
package org.paxml.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.util.stax.dialect.StAXDialect;
import org.paxml.core.PaxmlRuntimeException;

/**
 * The axiom utils.
 * 
 * @author Xuetao Niu
 * 
 */
public final class AxiomUtils {
	public static enum MediaType {
		XML, JSON
	}

	public static final String STAX_INPUT_FACT_KEY = "javax.xml.stream.XMLInputFactory";
	public static final String STAX_OUTPUT_FACT_KEY = "javax.xml.stream.XMLOutputFactory";

	private static <T> T doForMediaType(MediaType type, Callable<T> cal) {
		if (MediaType.JSON != type) {
			try {
				return cal.call();
			} catch (Exception e) {
				throw new PaxmlRuntimeException(e);
			}
		} else {

			final String oldR = System.getProperty(STAX_INPUT_FACT_KEY);
			final String oldW = System.getProperty(STAX_OUTPUT_FACT_KEY);
			try {

				System.setProperty(STAX_INPUT_FACT_KEY, JsonInputFactory.class.getName());
				System.setProperty(STAX_OUTPUT_FACT_KEY, JsonOutputFactory.class.getName());

				return cal.call();
			} catch (Exception e) {
				throw new PaxmlRuntimeException(e);
			} finally {
				System.setProperty(STAX_INPUT_FACT_KEY, oldR);
				System.setProperty(STAX_OUTPUT_FACT_KEY, oldW);
			}
		}
	}

	/**
	 * Parse input stream to get document.
	 * 
	 * @param in
	 *            input stream
	 * @return the document
	 */
	public static OMDocument getDocument(final InputStream in, MediaType type) {
		return doForMediaType(type, new Callable<OMDocument>() {

			@Override
			public OMDocument call() throws Exception {
				// create the builder
				OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(new StAXParserConfiguration() {

					@Override
					public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
						return factory;
					}

				}, in);

				// get the root element
				OMDocument doc = builder.getDocument();

				return doc;
			}

		});

	}

	public static String serialize(final OMElement ele, final boolean consume, MediaType type) {
		return doForMediaType(type, new Callable<String>() {

			@Override
			public String call() throws Exception {

				return consume ? ele.toStringWithConsume() : ele.toString();
			}

		});
	}

	/**
	 * Parse input stream to get root element.
	 * 
	 * @param in
	 *            the input stream
	 * @return the root element
	 */
	public static OMElement getRootElement(InputStream in, MediaType type) {

		return getDocument(in, type).getOMDocumentElement();

	}

	/**
	 * Get attribute value with local name.
	 * 
	 * @param ele
	 *            the element
	 * @param name
	 *            the attribute local name.
	 * @return the attribute value
	 */
	public static String getAttribute(OMElement ele, String name) {

		return ele.getAttributeValue(new QName(name));
	}

	/**
	 * Create text node.
	 * 
	 * @param text
	 *            the text
	 * @return the text node
	 */
	public static OMText createTextNode(String text) {
		return AxiomUtils.getOMFactory().createOMText(text);
	}

	/**
	 * Get attributes as list. This will force the attributes to be parsed.
	 * 
	 * @param ele
	 *            the owning element
	 * @return the list, never null.
	 */
	public static List<OMAttribute> getAttributes(OMElement ele) {
		List<OMAttribute> list = new ArrayList<OMAttribute>();
		for (OMAttribute attr : new Attributes(ele)) {
			list.add(attr);
		}
		return list;
	}

	/**
	 * Get the factory.
	 * 
	 * @return factory
	 */
	public static OMFactory getOMFactory() {
		return OMAbstractFactory.getOMFactory();
	}

	/**
	 * Create a new empty document.
	 * 
	 * @return the doc
	 */
	public static OMDocument newDocument() {
		return getOMFactory().createOMDocument();
	}

	/**
	 * Create a new empty document with root tag.
	 * 
	 * @param rootTagName
	 *            the root tag name
	 * @return the doc
	 */
	public static OMElement newDocument(String rootTagName) {
		return newDocument(rootTagName, "", null);
	}

	/**
	 * Create a new empty document with root tag and namespace.
	 * 
	 * @param rootTagName
	 *            the root tag
	 * @param namespaceURI
	 *            the namespace
	 * @param prefix
	 *            the prefix of namespace
	 * @return the doc
	 */
	public static OMElement newDocument(String rootTagName, String namespaceURI, String prefix) {
		OMDocument doc = newDocument();
		OMElement root = getOMFactory().createOMElement(rootTagName, namespaceURI, prefix);
		doc.setOMDocumentElement(root);
		return root;
	}

	/**
	 * Get all children nodes of an element into a list. This will force the
	 * children be parsed.
	 * 
	 * @param ele
	 *            the element
	 * @return the list, never null.
	 */
	public static List<OMNode> getNodes(OMElement ele) {
		List<OMNode> list = new ArrayList<OMNode>();
		for (OMNode child : new Nodes(ele)) {
			list.add(child);
		}
		return list;
	}

	/**
	 * Get all children elements into a list. This will force the children be
	 * parsed.
	 * 
	 * @param ele
	 *            the owning element
	 * @param filter
	 *            the local name filter
	 * @return the list, never null
	 */
	public static List<OMElement> getElements(OMElement ele, String filter) {
		List<OMElement> list = new ArrayList<OMElement>();
		for (OMElement child : new Elements(ele, filter)) {
			list.add(child);
		}
		return list;
	}

	/**
	 * Get the 1st child element with name filter.
	 * 
	 * @param ele
	 *            the owning element
	 * @param filter
	 *            the local name filter
	 * @return the 1st child element matchig the local name, or null if not
	 *         found.
	 */
	public static OMElement getFirstChildElement(OMElement ele, String filter) {
		if (null == filter) {
			return ele.getFirstElement();
		}
		Iterator<OMElement> it = ele.getChildrenWithLocalName(filter);
		if (it.hasNext()) {
			return it.next();
		} else {
			return null;
		}
	}
}
