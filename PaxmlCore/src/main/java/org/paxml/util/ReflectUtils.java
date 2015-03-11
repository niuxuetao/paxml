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
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.StringUtils;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.beans.BeanUtils;

/**
 * Utility for reflection.
 * 
 * @author Xuetao Niu
 * 
 */
public final class ReflectUtils {

	/**
	 * Class visitor.
	 * 
	 * @author Xuetao Niu
	 * 
	 * @param <T>
	 *            the type of return value.
	 */
	public static interface IClassVisitor<T> {
		/**
		 * Visit a class and return a value.
		 * 
		 * @param clazz
		 *            the class
		 * @return the return value
		 */
		T onVisit(Class<?> clazz);
	}

	/**
	 * Traverse the inheritance tree of a class, including the class itself.
	 * 
	 * @param <T>
	 *            the type to return as traversal result.
	 * @param clazz
	 *            the class to traverse
	 * @param upTillClass
	 *            the top level class to stop at
	 * @param includeInterfaces
	 *            true to traverse interfaces, false not to
	 * @param visitor
	 *            the visitor to be called on visiting each class on the
	 *            inheritance tree. If the onVisit() method returns null, the
	 *            traversal will continue, otherwise it will stop and return the
	 *            value as overall result.
	 * @return the value returned by the visitor's.
	 */
	public static <T> T traverseInheritance(Class<?> clazz, Class<?> upTillClass, boolean includeInterfaces, IClassVisitor<T> visitor) {
		if (!includeInterfaces && clazz.isInterface()) {
			return null;
		}
		if (upTillClass != null && upTillClass.equals(clazz)) {
			return visitor.onVisit(clazz);
		}
		if (Object.class.equals(clazz)) {
			return null;
		}
		T result = visitor.onVisit(clazz);
		if (result != null) {
			return result;
		}
		for (Class<?> intf : clazz.getInterfaces()) {
			result = traverseInheritance(intf, upTillClass, includeInterfaces, visitor);
			if (result != null) {
				return result;
			}
		}
		Class<?> parentClass = clazz.getSuperclass();
		if (parentClass != null) {
			result = traverseInheritance(parentClass, upTillClass, includeInterfaces, visitor);
		}
		return result;
	}

	public static interface TraverseObjectCallback {
		boolean onElement(Object ele);
	}

	public static void traverseObject(Object obj, TraverseObjectCallback callback) {
		if (obj == null) {
			return;
		}
		Iterator it = null;
		if (obj instanceof Iterable) {
			it = ((Iterable) obj).iterator();
		} else if (obj instanceof Iterator) {
			it = (Iterator) obj;
		} else if (obj instanceof Enumeration) {
			it = Collections.list((Enumeration) obj).iterator();
		} else if (obj.getClass().isArray()) {
			it = new ArrayIterator(obj);
		} else if (obj instanceof String) {
			it = new ArrayIterator(StringUtils.split((String) obj));
		} else if (obj instanceof Map) {
			it = ((Map) obj).keySet().iterator();
		} else {
			it = Arrays.asList(obj).iterator();
		}
		while (it.hasNext()) {
			Object ele = it.next();
			if (!callback.onElement(ele)) {
				return;
			}
		}

	}

	/**
	 * Find annotation from a class and all super classes and interfaces, stop
	 * at the first encounter.
	 * 
	 * @param <A>
	 *            the annotation type
	 * @param clazz
	 *            the class
	 * @param annotationClass
	 *            the annotation class
	 * @return the annotation, or null if not from inhheritance tree.
	 */
	public static <A extends Annotation> A getAnnotation(Class<?> clazz, final Class<A> annotationClass) {
		return traverseInheritance(clazz, null, true, new IClassVisitor<A>() {

			public A onVisit(Class<?> clazz) {
				return clazz.getAnnotation(annotationClass);
			}

		});
	}

	/**
	 * Check if a class implements an interface class.
	 * 
	 * @param implementingClass
	 *            the implementing class
	 * @param interfaceClass
	 *            the interface class
	 * @param matchNameOnly
	 *            true to only do name string comparison, false also compares
	 *            the class loader.
	 * @return true if yes, false not
	 */
	public static boolean isImplementingClass(Class<?> implementingClass, final Class<?> interfaceClass, final boolean matchNameOnly) {
		if (!interfaceClass.isInterface() || implementingClass.isInterface() || implementingClass.equals(interfaceClass)) {
			return false;
		}
		Object result = traverseInheritance(implementingClass, interfaceClass, true, new IClassVisitor<Object>() {

			public Object onVisit(Class<?> clazz) {
				if (matchNameOnly) {
					return clazz.getName().equals(interfaceClass.getName()) ? new Object() : null;
				} else {
					return clazz.equals(interfaceClass) ? new Object() : null;
				}
			}

		});
		return result != null;
	}

	/**
	 * Check if a class is subclassing another class.
	 * 
	 * @param subClass
	 *            the sub class
	 * @param superClass
	 *            the super class
	 * @param matchNameOnly
	 *            true to only do name string comparison, false also compares
	 *            the class loader.
	 * @return true if yes, false not
	 */
	public static boolean isSubClass(Class<?> subClass, final Class<?> superClass, final boolean matchNameOnly) {
		if (subClass.equals(superClass)) {
			return false;
		}
		if (superClass.equals(Object.class)) {
			return true;
		}
		Object result = traverseInheritance(subClass, superClass, subClass.isInterface(), new IClassVisitor<Object>() {

			public Object onVisit(Class<?> clazz) {
				if (matchNameOnly) {
					return clazz.getName().equals(superClass.getName()) ? new Object() : null;
				} else {
					return clazz.equals(superClass) ? new Object() : null;
				}
			}

		});
		return result != null;
	}

	/**
	 * Load class from class name, swallowing possible ClassNotFoundException.
	 * 
	 * @param clazz
	 *            the class name
	 * @param cl
	 *            the classloader, set to null to use the current thread context
	 *            class loader.
	 * @return the loaded class, or null if class not found.
	 */
	public static Class loadClass(String clazz, ClassLoader cl) {
		cl = cl == null ? Thread.currentThread().getContextClassLoader() : cl;
		try {
			return cl.loadClass(clazz);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * Load a class, converting possible ClassNotFoundException into
	 * paxmlRuntimeException.
	 * 
	 * @param className
	 *            the class name
	 * @param cl
	 *            the classloader, set to null to use the current thread context
	 *            class loader.
	 * @return the loaded class.
	 * @throws PaxmlRuntimeException
	 *             if the load fails.
	 */
	public static Class<?> loadClassStrict(String className, ClassLoader cl) {
		cl = cl == null ? Thread.currentThread().getContextClassLoader() : cl;
		try {
			return cl.loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new PaxmlRuntimeException("Class not found: " + className, e);
		}
	}

	/**
	 * Check if a class is abstract class or interface.
	 * 
	 * @param clazz
	 *            the class
	 * @return true if yes, false not
	 */
	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * Create an object with the default constructor.
	 * 
	 * @param className
	 *            the name of the class to create object from
	 * @param cl
	 *            the class loader, set to null to use the current thread class
	 *            loader
	 * @param constructParams
	 *            the parameters to call constructor with
	 * @return the constructed object.
	 * @throws RuntimeException
	 *             if the construction fails.
	 */
	public static Object createObject(String className, ClassLoader cl, Object... constructParams) {
		return createObject(loadClassStrict(className, cl), constructParams);
	}

	/**
	 * Construct object from class using the default constructor.
	 * 
	 * @param <T>
	 *            the class type
	 * @param clazz
	 *            the class
	 * @param constructParams
	 *            the parameters to call constructor with
	 * @return the object
	 * @throws RuntimeException
	 *             if the construction fails.
	 */
	public static <T> T createObject(Class<? extends T> clazz, Object... constructParams) {
		Constructor<T> con = null;
		Class[] argTypes = null;
		for (Constructor c : clazz.getDeclaredConstructors()) {
			argTypes = c.getParameterTypes();
			if (argTypes.length == constructParams.length) {
				con = c;
				break;
			}
		}
		if (con == null) {
			throw new PaxmlRuntimeException("No constructor found with " + constructParams.length + " parameters!");
		}
		try {
			Object[] args = new Object[constructParams.length];
			for (int i = args.length - 1; i >= 0; i--) {
				args[i] = coerceType(constructParams[i], argTypes[i]);
			}
			con.setAccessible(true);
			return con.newInstance(args);
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot create instance from class: " + clazz.getName(), e);
		}
	}

	/**
	 * Coerce object type.
	 * 
	 * @param <T>
	 *            the expected type
	 * @param from
	 *            from object
	 * @param expectedType
	 *            to object type
	 * @return the to object
	 */
	public static <T> T coerceType(Object from, Class<? extends T> expectedType) {
		if (from == null) {
			return null;
		}
		Object targetValue = null;
		if (expectedType.isInstance(from)) {
			targetValue = from;
		} else if (expectedType.isEnum()) {
			for (Object e : expectedType.getEnumConstants()) {
				if (from.toString().equalsIgnoreCase(e.toString())) {
					targetValue = e;
					break;
				}
			}
			if (targetValue == null) {
				throw new PaxmlRuntimeException("No enum named '" + from + "' is defined in class: " + expectedType);
			}
		} else if (isImplementingClass(expectedType, Collection.class, false)) {
			try {
				targetValue = expectedType.newInstance();
			} catch (Exception e) {
				throw new PaxmlRuntimeException(e);
			}
			collect(from, (Collection) targetValue, true);
		} else if (Iterator.class.equals(expectedType)) {
			List list = new ArrayList();
			collect(from, list, true);
			targetValue = list.iterator();
		} else if (isImplementingClass(expectedType, Coerceable.class, false)) {

			try {
				Constructor c = expectedType.getConstructor(Object.class);

				targetValue = c.newInstance(from);
			} catch (Exception e) {
				throw new PaxmlRuntimeException(e);
			}
		} else {
			targetValue = ConvertUtils.convert(from.toString(), expectedType);
		}

		return (T) targetValue;
	}

	public static int collect(Iterator it, Collection col) {
		int i = 0;
		while (it.hasNext()) {
			i++;
			col.add(it.next());
		}
		return i;
	}

	public static int collect(Iterable it, Collection col) {
		return collect(it.iterator(), col);
	}
	/**
	 * Collect elements from source to target.
	 * @param obj the collectable source
	 * @param col the target collection
	 * @param collectSingle true to put single not collectable on the target collection, false not
	 * @return the number of elements collected
	 */
	public static int collect(Object obj, Collection col, boolean collectSingle) {

		if (obj instanceof Iterable) {
			return collect((Iterable) obj, col);
		} else if (obj instanceof Iterator) {
			return collect((Iterator) obj, col);
		} else if (obj instanceof Map) {
			return collect(((Map) obj).values(), col);
		} else if (obj.getClass().isArray()) {
			return collectArray(obj, col);
		} else if (obj instanceof Enumeration) {
			return collect((Enumeration) obj, col);
		} else if (obj instanceof CharSequence) {
			char[] seq = new char[((CharSequence) obj).length()];
			for (int i = 0; i < seq.length; i++) {
				seq[i] = ((CharSequence) obj).charAt(i);
			}
			return collectArray(seq, col);
		} else if (collectSingle) {
			col.add(obj);
			return 1;
		}
		return -1;
	}

	public static int collectArray(Object array, Collection col) {
		int len = Array.getLength(array);
		for (int i = 0; i < len; i++) {
			col.add(Array.get(array, i));
		}
		return len;
	}

	public static int collect(Enumeration e, Collection col) {
		int i = 0;
		while (e.hasMoreElements()) {
			i++;
			col.add(e.nextElement());
		}
		return i;
	}

	/**
	 * Put a value into a list if it is not a listable object.
	 * 
	 * @param obj
	 *            the value
	 * @return list, never null.
	 */
	public static List getList(Object obj) {
		List list = new ArrayList(0);
		for (Object v : new IterableObject(obj)) {
			list.add(v);
		}
		return list;
	}

	/**
	 * Set a property for a bean.
	 * 
	 * @param bean
	 *            the bean
	 * @param pd
	 *            the property descriptor
	 * @param value
	 *            the property value
	 */
	public static void callSetter(Object bean, PropertyDescriptor pd, Object value) {

		Method setter = pd.getWriteMethod();
		if (setter == null) {
			throw new PaxmlRuntimeException("Property '" + pd.getName() + "' is not settable on class: " + bean.getClass().getName());
		}

		value = coerceType(value, setter.getParameterTypes()[0]);
		try {
			setter.invoke(bean, value);
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot call setter on property: " + pd.getName(), e);
		}
	}

	/**
	 * Call a static method. If not found, exception will be thrown.
	 * 
	 * @param className
	 *            the class name
	 * @param method
	 *            the method name
	 * @param args
	 *            the args name
	 * @return the method return value.
	 * 
	 */
	public static Object callStaticMethod(String className, String method, Object[] args) {
		if (args == null) {
			args = new Object[0];
		}
		Class clazz = ReflectUtils.loadClassStrict(className, null);
		for (Method m : clazz.getMethods()) {
			if (!m.getName().equals(method)) {
				continue;
			}
			Class[] argTypes = m.getParameterTypes();
			if (argTypes.length == args.length) {
				Object[] actualArgs = new Object[args.length];
				for (int i = args.length - 1; i >= 0; i--) {
					actualArgs[i] = ReflectUtils.coerceType(args[i], argTypes[i]);
				}
				try {
					return m.invoke(null, actualArgs);
				} catch (Exception e) {
					throw new PaxmlRuntimeException(e);
				}
			}
		}
		throw new PaxmlRuntimeException("No method named '" + method + "' has " + args.length + " parameters from class: " + className);
	}

	/**
	 * Call a method on an object.
	 * 
	 * @param obj
	 *            the object
	 * @param method
	 *            the method name
	 * @param args
	 *            the arguments
	 * @return the return value
	 */
	public static Object callMethod(Object obj, String method, Object[] args) {

		Class clazz = obj.getClass();

		for (Method m : clazz.getMethods()) {
			if (!m.getName().equals(method)) {
				continue;
			}
			Class[] argTypes = m.getParameterTypes();
			if (argTypes.length == args.length) {
				Object[] actualArgs = new Object[args.length];
				for (int i = args.length - 1; i >= 0; i--) {
					actualArgs[i] = ReflectUtils.coerceType(args[i], argTypes[i]);
				}
				try {
					return m.invoke(obj, actualArgs);
				} catch (Exception e) {
					throw new PaxmlRuntimeException(e);
				}
			}
		}
		throw new PaxmlRuntimeException("No method named '" + method + "' has " + args.length + " parameters from class: " + clazz.getName());
	}
	/**
	 * Property descriptor type enum.
	 * @author Xuetao Niu
	 *
	 */
	public static enum PropertyDescriptorType {
		GETTER, SETTER
	}

	/**
	 * Get a specific type of property descriptors, except the "class" property.
	 * 
	 * @param clazz
	 *            the class
	 * @param type the type filter, null means no filtering
	 * @return the list of property descriptors.
	 */
	public static List<PropertyDescriptor> getPropertyDescriptors(Class clazz, PropertyDescriptorType type) {
		List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
		for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(clazz)) {
			if ("class".equals(pd.getName())) {
				continue;
			}
			switch (type) {
			case GETTER:
				if (pd.getReadMethod() != null) {
					list.add(pd);
				}
				break;
			case SETTER:
				if (pd.getWriteMethod() != null) {
					list.add(pd);
				}
				break;
			default:
				list.add(pd);
			}

		}
		return list;
	}
}
