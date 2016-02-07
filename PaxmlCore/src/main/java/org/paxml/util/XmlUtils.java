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

import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;
import org.paxml.core.IObjectContainer;
import org.paxml.core.PaxmlRuntimeException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XmlUtils {
	
	public static String toJson(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot convert to json", e);
		}

	}
	public static String toXml(final Object obj) {
		return toXml(obj, null, null);
	}
	public static String toXml(final Object obj, final String rootTag, String topCollectionTag) {
		if(obj==null){
			return null;
		}
		String rt=rootTag;
		if (rt == null && obj instanceof IObjectContainer) {
			rt = ((IObjectContainer) obj).getName();
		}
		if (rt == null) {
			rt = "xml-fragment";
		}
		if(topCollectionTag==null){
			topCollectionTag="item";
		}

		XStream xstream = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("&#36;", "_")));		
		xstream.alias(rootTag, obj.getClass());
		xstream.alias(rootTag, Map.class);
		xstream.registerConverter(new XStreamMapColConverter(topCollectionTag));
		// xstream.registerConverter(new XStreamFilterConverter(new String[] {
		// "java.lang.*", "java.util.*", "org.paxml.*" }, null),
		// Integer.MIN_VALUE);
		xstream.registerConverter(new XStreamBeanConverter(false, xstream.getMapper()), -20);

		return xstream.toXML(obj);

	}

	public static String xmlToJson(String xml) {
		JSONObject json = JsonXml.toJSONObject(xml);
		if (json.length() <= 0) {
			throw new PaxmlRuntimeException("Invalid xml: " + xml);
		}
		return json.toString(4);
	}

	public static Object fromXml(String xml) {
		return fromXml(xml, false);
	}

	public static Object fromXml(String xml, boolean keepSingleRoot) {
		String json = xmlToJson(xml);
		return fromJson(json, keepSingleRoot);
	}

	public static Object extractSingleMapRoot(Map map) {

		if (map.size() == 1) {
			Object root = map.values().iterator().next();
			return root;
		}
		return map;
	}

	public static boolean isSingleRootMap(Object obj) {
		if (!(obj instanceof Map)) {
			return false;
		}
		return ((Map) obj).size() == 1;
	}

	public static Object parseJsonOrXmlOrString(String jsonOrXmlOrString) {
		Object r = jsonOrXmlOrString;
		String trimmed = jsonOrXmlOrString.trim();
		if (trimmed.startsWith("<")) {
			try {
				r = fromXml(jsonOrXmlOrString);
			} catch (Exception e) {
				// keep silent
			}
		} else if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
			try {
				r = fromJson(jsonOrXmlOrString, true);
			} catch (Exception e) {
				// keep silent
			}
		}
		return r;
	}

	public static Object fromJson(String json) {
		return fromJson(json, true);
	}

	public static Object fromJson(String json, boolean keepSingleRoot) {

		ObjectMapper mapper = new ObjectMapper();

		try {
			return mapper.readValue(json, new TypeReference<Object>() {
			});

		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot parse from json", e);
		}
	}
}
