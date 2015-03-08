package org.paxml.util;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;

public class JsonInputFactory extends MappedXMLInputFactory {
	public JsonInputFactory() {
		
			super(new Configuration());
		
	}

	@Override
    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
	    
    }
	
}
