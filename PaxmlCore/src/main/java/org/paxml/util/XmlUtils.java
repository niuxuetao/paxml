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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.paxml.core.PaxmlRuntimeException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XmlUtils {
	public static class FilterConverter implements Converter {
		private final List<Pattern> includes = new ArrayList();
		private final List<Pattern> excludes = new ArrayList();

		public FilterConverter(String[] includes, String[] excludes) {

			for (String r : includes) {
				this.includes.add(Pattern.compile(createRegexFromGlob(r)));
			}
			for (String r : excludes) {
				this.excludes.add(Pattern.compile(createRegexFromGlob(r)));
			}
		}

		public static String createRegexFromGlob(String glob) {
			String out = "^";
			for (int i = 0; i < glob.length(); ++i) {
				final char c = glob.charAt(i);
				switch (c) {
				case '*':
					out += ".*";
					break;
				case '?':
					out += '.';
					break;
				case '.':
					out += "\\.";
					break;
				case '\\':
					out += "\\\\";
					break;
				default:
					out += c;
				}
			}
			out += '$';
			return out;
		}

		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			return null;
		}

		@Override
		public boolean canConvert(Class clazz) {
			String cn = clazz.getName();
			boolean yes = true;
			for (Pattern p : includes) {
				if (p.matcher(cn).matches()) {
					yes = false;
					break;
				}
			}
			for (Pattern p : excludes) {
				if (p.matcher(cn).matches()) {
					yes = true;
					break;
				}
			}
			return yes;
		}
	}

	public static String serializeJaxb(Object obj) {
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FRAGMENT, true);
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(obj, out);
			return out.toString("UTF-8");
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}
	}

	public static String serializeXStream(Object obj) {
		XStream xstream = new XStream();

		xstream.registerConverter(new FilterConverter(
				new String[]{"java.lang.*", "org.paxml.*", "java.util.*"},
				new String[]{}), Integer.MIN_VALUE);

		return xstream.toXML(obj);
	}
}
