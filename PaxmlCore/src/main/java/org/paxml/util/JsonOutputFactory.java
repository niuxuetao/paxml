package org.paxml.util;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;

public class JsonOutputFactory extends MappedXMLOutputFactory {
	public JsonOutputFactory() {
		
		super(new Configuration());
		
	}
	
}
