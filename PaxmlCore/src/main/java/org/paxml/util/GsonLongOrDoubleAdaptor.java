package org.paxml.util;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;



public class GsonLongOrDoubleAdaptor extends com.google.gson.TypeAdapter<Number> {

	@Override
    public Number read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
	         reader.nextNull();
	         return null;
	       }
		String str = reader.nextString();
		if(str.contains(".")){
			return Double.parseDouble(str);
		}else{
			return Long.parseLong(str);
		}
    }

	@Override
    public void write(JsonWriter writer, Number obj) throws IOException {
	    writer.value(obj);
    }
	
}
