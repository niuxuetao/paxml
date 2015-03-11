package org.paxml.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;



public class GsonLongOrDoubleAdaptorFactory implements com.google.gson.TypeAdapterFactory {

	@Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<T> rawType = (Class<T>) type.getRawType();
		System.out.println(rawType.getName());
		return null;
    }

	
}
