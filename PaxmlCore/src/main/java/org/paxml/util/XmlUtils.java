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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONObject;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.beans.BeanUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;

public class XmlUtils {
	// @XmlRootElement
	public static class X {
		private String pri = "pvivate value";
		private Map m;
		private String y;
		private List objList = new LinkedList(Arrays.asList(new XX(), new XX()));

		public List getObjList() {
			return objList;
		}

		public void setObjList(List objList) {
			this.objList = objList;
		}

		public String getY() {
			return y;
		}

		public void setY(String y) {
			this.y = y;
		}

		public Map getM() {
			return m;
		}

		public void setM(Map m) {
			this.m = m;
		}

	}

	public static class XX {
		private int val;
		private Map xxMap = new LinkedHashMap();
		private List myList = new ArrayList();

		public XX() {
			xxMap.put(1, 10);
			xxMap.put(2, 20);

			Map m1 = new HashMap();
			m1.put("m1", Arrays.asList(1.0, 2.1));
			Map m2 = new HashMap();
			m2.put("m2", Arrays.asList(3, 4));

			myList.add(m1);
			myList.add(m2);
		}

		public Map getXxMap() {
			return xxMap;
		}

		public void setXxMap(Map xxMap) {
			this.xxMap = xxMap;
		}

		public int getVal() {
			return val;
		}

		public void setVal(int val) {
			this.val = val;
		}

		public List getMyList() {
			return myList;
		}

		public void setMyList(List myList) {
			this.myList = myList;
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.println(xmlToJson("<nn/>"));
		System.out.println(serializeGson(Arrays.asList("x", 'Y')));
		System.out.println(xmlToJson("<xml a='1'>x</xml>"));

		Map map1 = new LinkedHashMap();
		Map map2 = new LinkedHashMap();
		map2.put("deep", Arrays.asList("d1", "d2"));
		map1.put(1, 100);
		map1.put(2, map2);
		map1.put("li", Arrays.asList("li1", "li2"));
		map1.put("xx-val", new XX());
		X x = new X();
		x.setY("y-str");
		x.setM(map1);
		String xml1 = serializeXStream(x, "root", "ele");
		String xml2 = serializeXStream(parseXml(xml1), "root", "ele");
		System.out.println(xml1);
		System.out.println(xml2);
		// System.err.println(StringUtils.difference(xml1, xml2));
		// System.out.println(serializeXStream(Arrays.asList(map1, 2), "root",
		// "ele"));
		// System.out.println(serializeXStream(map1, "root", "ele"));
	}

	public static String serializeGson(Object obj) {
		return new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC).setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z")
		        .addSerializationExclusionStrategy(new ExclusionStrategy() {

			        @Override
			        public boolean shouldSkipField(FieldAttributes f) {
				        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(f.getDeclaringClass(), f.getName());
				        return pd == null || pd.getReadMethod() == null;
			        }

			        @Override
			        public boolean shouldSkipClass(Class<?> c) {
				        return false;
			        }
		        }).create().toJson(obj);
	}

	public static String serializeXStream(final Object obj, final String rootTag, String topCollectionTag) {

		XStream xstream = new XStream();
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

	public static Object parseXml(String xml) {
		return parseXml(xml, false);
	}

	public static Object parseXml(String xml, boolean keepSingleRoot) {
		String json = xmlToJson(xml);
		StringTokenizer st = new StringTokenizer(json);

		return parseJson(json, keepSingleRoot);
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

	public static Object parseJson(String json, boolean keepSingleRoot) {

		Gson gson = new GsonBuilder().create();
		String trimmed = json.trim();
		if (trimmed.startsWith("{")) {
			Map map = gson.fromJson(json, LinkedHashMap.class);
			if (!keepSingleRoot && map.size() == 1) {
				Object root = map.values().iterator().next();
				return root;
			}
			return map;
		} else if (trimmed.startsWith("[")) {
			return gson.fromJson(json, ArrayList.class);
		} else {
			return json;
		}
	}
}
