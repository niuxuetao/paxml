package org.paxml.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonLongOrDoubleDeserializer implements JsonDeserializer<Number> {

	@Override
    public Number deserialize(JsonElement ele, Type type, JsonDeserializationContext context) throws JsonParseException {
		String str=ele.getAsString();
		if(str.contains(".")){
			return Double.parseDouble(str);
		}else{
			return Long.parseLong(str);
		}
    }
	
}
