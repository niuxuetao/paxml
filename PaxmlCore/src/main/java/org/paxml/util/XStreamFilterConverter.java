package org.paxml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XStreamFilterConverter implements Converter{
	private final List<Pattern> includes = new ArrayList();
	private final List<Pattern> excludes = new ArrayList(0);

	public XStreamFilterConverter(String[] includes, String[] excludes) {

		for (String r : includes) {
			this.includes.add(Pattern.compile(PaxmlUtils.createRegexFromGlob(r)));
		}
		if (excludes != null) {
			for (String r : excludes) {
				this.excludes.add(Pattern.compile(PaxmlUtils.createRegexFromGlob(r)));
			}
		}
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
