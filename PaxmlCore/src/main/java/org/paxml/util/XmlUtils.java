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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.mapped.Configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class XmlUtils {
	// @XmlRootElement
	public static class X {
		private String pri = "pvivate value";
		private Map m;
		private String y;

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
		private List myList = Arrays.asList("ml1", "ml2");

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
		System.out.println(serializeXStream(x, "root", "ele", MediaType.XML));
		System.out.println(serializeXStream(Arrays.asList(map1, 2), "root", "ele", MediaType.XML));
		System.out.println(serializeXStream(map1, "root", "ele", MediaType.XML));
	}

	public static enum MediaType {
		XML, JSON
	}



	public static String serializeXStream(final Object obj, final String rootTag, String topCollectionTag, MediaType type) {
		final HierarchicalStreamDriver driver;
		if (type == MediaType.JSON) {
			Configuration conf = new Configuration();

			if (rootTag == null) {
				conf.setDropRootElement(true);
			}
			driver = new JettisonMappedXmlDriver(conf);
		} else {
			driver = new XppDriver();
		}
		XStream xstream = new XStream(driver);
		xstream.alias(rootTag, obj.getClass());
		xstream.alias(rootTag, Map.class);
		xstream.registerConverter(new XStreamMapColConverter(topCollectionTag));
		xstream.registerConverter(new XStreamFilterConverter(new String[] { "java.lang.*", "java.util.*", "org.paxml.*" }, null), Integer.MIN_VALUE);
		xstream.registerConverter(new XStreamBeanConverter(false, xstream.getMapper()),-20);

		return xstream.toXML(obj);

	}
}
