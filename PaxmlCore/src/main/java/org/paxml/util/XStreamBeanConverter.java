package org.paxml.util;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;
import com.thoughtworks.xstream.converters.javabean.JavaBeanProvider;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class XStreamBeanConverter extends JavaBeanConverter {

	private final boolean includeClass;

	public XStreamBeanConverter(boolean includeClass, Mapper mapper) {
		super(mapper);
		this.includeClass = includeClass;
	}
	
	

	@Override
    public boolean canConvert(Class type) {
	    if(Object.class.equals(type)){
	    	return false;
	    }
	    return super.canConvert(type);
    }



	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final String classAttributeName = includeClass ? mapper.aliasForSystemAttribute("class") : null;
		beanProvider.visitSerializableProperties(source, new JavaBeanProvider.Visitor() {
			public boolean shouldVisit(String name, Class definedIn) {
				return mapper.shouldSerializeMember(definedIn, name);
			}

			public void visit(String propertyName, Class fieldType, Class definedIn, Object newObj) {
				if (newObj != null) {
					writeField(propertyName, fieldType, newObj, definedIn);
				}
			}

			private void writeField(String propertyName, Class fieldType, Object newObj, Class definedIn) {
				context.put(XStreamBeanConverter.class, propertyName);
				if (XStreamMapColConverter.isCollection(fieldType)) {
					context.convertAnother(newObj);
				} else {

					Class actualType = newObj.getClass();
					Class defaultType = mapper.defaultImplementationOf(fieldType);
					String serializedMember = mapper.serializedMember(source.getClass(), propertyName);
					ExtendedHierarchicalStreamWriterHelper.startNode(writer, serializedMember, actualType);
					if (!actualType.equals(defaultType) && classAttributeName != null) {
						writer.addAttribute(classAttributeName, mapper.serializedClass(actualType));
					}

					context.convertAnother(newObj);

					writer.endNode();
				}
			}
		});
	}

	static String getCurrentPropertyName(MarshallingContext context) {
		return (String) context.get(XStreamBeanConverter.class);
	}
}