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
package org.paxml.el;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.paxml.annotation.Util;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.InMemoryResource;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.launch.Paxml;
import org.paxml.security.Secret;
import org.paxml.tag.AbstractTag;
import org.paxml.util.ReflectUtils;

/**
 * The default util functions.
 * 
 * @author Xuetao Niu
 * 
 */
@Util("util")
public class UtilFunctions implements IUtilFunctionsFactory {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getUtilFunctions(Context context) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getXpathUtilFunctions(Context context) {
		return XpathFunctions.class;
	}

	/**
	 * Merge a list of objects into one list.
	 * 
	 * @param objs
	 *            if an object is a Collection, all its' elements will be used,
	 *            not the Collection itself.
	 * @return the list , never null
	 */
	public static List list(Object... objs) {
		if (objs.length == 1) {
			List r = new ArrayList();
			ReflectUtils.collect(objs[0], r, true);
			return r;
		}
		return new ArrayList(Arrays.asList(objs));

	}

	/**
	 * Compose a map with keys and values.
	 * 
	 * @param keysAndValues
	 *            each key and value pair should be given consecutively.
	 * @return the map
	 */
	public static Map map(Object... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new PaxmlRuntimeException("Keys and values should be pairs");
		}
		Map map = new LinkedHashMap();
		for (int i = 0; i < keysAndValues.length - 1; i += 2) {
			map.put(keysAndValues[i], keysAndValues[i + 1]);
		}
		return map;
	}

	/**
	 * Get the apache commons collection utils.
	 * 
	 * @return the utils object
	 */
	public static CollectionUtils getCollection() {
		return new CollectionUtils();
	}

	/**
	 * Get the Math utils.
	 * 
	 * @return the utils object
	 */
	public static Math getMath() {

		return ReflectUtils.createObject(Math.class);
	}

	/**
	 * Select xpath objects. @see Context.xpathSelect(xpath, alwaysList)
	 * 
	 * @param xpath
	 *            xpath
	 * @param alwaysList
	 *            always list
	 * @return the object
	 */
	public static Object xpathSelect(String xpath, boolean alwaysList) {
		return Context.getCurrentContext().xpathSelect(null, xpath, alwaysList);
	}

	/**
	 * Select xpath objects. @see Context.xpathSelect(xpath)
	 * 
	 * @param xpath
	 *            xpath
	 * 
	 * @return the object
	 */
	public static Object xpathSelect(String xpath) {
		return Context.getCurrentContext().xpathSelect(null, xpath);
	}

	/**
	 * Select xpath objects. @see Context.xpathSelect(from, xpath, alwaysList)
	 * 
	 * @param from
	 *            from object
	 * @param xpath
	 *            xpath
	 * @param alwaysList
	 *            always list
	 * @return the object
	 */
	public static Object xpathSelectFrom(Object from, String xpath, boolean alwaysList) {
		return Context.getCurrentContext().xpathSelect(from, xpath, alwaysList);
	}

	/**
	 * Select xpath objects. @see Context.xpathSelect(from, xpath)
	 * 
	 * @param from
	 *            from object
	 * @param xpath
	 *            xpath
	 * 
	 * @return the object
	 */
	public static Object xpathSelectFrom(Object from, String xpath) {
		return Context.getCurrentContext().xpathSelect(from, xpath);
	}

	/**
	 * Check if an id exists in current context.
	 * 
	 * @param id
	 *            the id
	 * @param searchParent
	 *            true to search the parent context, false not to.
	 * @return true if exists, false if not.
	 */
	public static boolean hasConst(String id, boolean searchParent) {
		return Context.getCurrentContext().hasConstId(id, searchParent);
	}

	/**
	 * Get a copy of const ids for the current thread context.
	 * 
	 * @return the copy
	 */
	public static Set<String> getConstIds() {
		return new HashSet<String>(Context.getCurrentContext().getConstIds());
	}

	/**
	 * Get a value with id from current context. NB, this will not cause
	 * exception if the id doesn't exist!
	 * 
	 * @param id
	 *            the id
	 * @param searchParent
	 *            true to search the parent context, false not to.
	 * @return true the value or null if not found.
	 */
	public static Object getConst(String id, boolean searchParent) {
		return Context.getCurrentContext().getConst(id, searchParent);
	}

	/**
	 * Set a const in context.
	 * 
	 * @param id
	 *            the const id
	 * @param value
	 *            the const
	 * @param top
	 *            true to set on root context, false to set on its own context
	 * @return the existing const if any.
	 */
	public static Object setConst(String id, Object value, boolean top) {
		Context context = Context.getCurrentContext();
		context = top ? context.getRootContext() : context.getCurrentEntityContext();

		return context.setConst(id, value == null ? null : value.getClass().getName(), value, true);
	}

	/**
	 * Set const in caller's context.
	 * 
	 * @param id
	 *            the const id
	 * @param value
	 *            the const value
	 * @return the existing const in caller's const, null if no existing const.
	 */
	public static Object setConstForCaller(String id, Object value) {
		Context context = Context.getCurrentContext();
		context = context.findCallerContext();
		if (context == null) {
			throw new PaxmlRuntimeException("The current file has no caller!");
		}
		return context.setConst(id, value == null ? null : value.getClass().getName(), value, true);
	}

	/**
	 * Select values by class name.
	 * 
	 * @param className
	 *            the class name
	 * @param inheritance
	 *            true to also include objects whose parent class matches the
	 *            given name, false to do exact name match.
	 * @param excludesParentContext
	 *            true not to search in parent contexts, false to search in
	 *            parent contexts.
	 * @param mergeWithParentContext
	 *            this parameter only has effect when searching from parent
	 *            contexts. true to merge the values from parent context, false
	 *            to overrule values from parent upon identical id.
	 * @return a list of values, never null
	 * @throws Exception
	 *             any Exception
	 */
	public static List<Object> classSelect(String className, boolean inheritance, boolean excludesParentContext, boolean mergeWithParentContext) throws Exception {
		Context context = Context.getCurrentContext();

		List<Object> list = new ArrayList<Object>(0);
		Map<String, Object> map = excludesParentContext ? context.getIdConstsMap() : context.getIdMap(mergeWithParentContext, true);
		if (inheritance) {
			Class clazz = Class.forName(className);
			for (Object value : map.values()) {
				if (clazz.isInstance(value)) {
					list.add(value);
				}
			}
		} else {
			for (Object value : map.values()) {
				if (value != null && value.getClass().getName().equals(className)) {
					list.add(value);
				}
			}
		}
		return list;
	}

	/**
	 * Create a random number between two double bounds.
	 * 
	 * @param low
	 *            the low bound
	 * @param high
	 *            the high bound
	 * @return the random number
	 */
	public static double random(double low, double high) {
		if (low == high) {
			return low;
		} else if (low > high) {
			double tmp = low;
			low = high;
			high = tmp;
		}
		double range = high - low;
		return low + Math.random() * range;
	}

	/**
	 * Create a random number between two long bounds.
	 * 
	 * @param low
	 *            the low bound
	 * @param high
	 *            the high bound
	 * @return the random number
	 */
	public static long random(long low, long high) {
		return Math.round(random(low + 0.0, high));
	}

	/**
	 * Get the current system time.
	 * 
	 * @return the system time in ms.
	 */
	public static long getSystemTime() {
		return System.currentTimeMillis();
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
		return ReflectUtils.callStaticMethod(className, method, args);
	}

	/**
	 * Call a static method. If not found, exception will be thrown.
	 * 
	 * @param className
	 *            the class name
	 * @param method
	 *            the method name
	 * 
	 * @return the method return value.
	 * 
	 */
	public static Object callStaticMethod(String className, String method) {
		return ReflectUtils.callStaticMethod(className, method, null);
	}

	/**
	 * Call a method on an object.
	 * 
	 * @param obj
	 *            the method owner
	 * @param method
	 *            the method name
	 * 
	 * @return the return value of the method call
	 */
	public static Object callMethod(Object obj, String method) {
		return ReflectUtils.callMethod(obj, method, null);
	}

	/**
	 * Call a method on an object.
	 * 
	 * @param obj
	 *            the method owner
	 * @param method
	 *            the method name
	 * @param args
	 *            the args
	 * @return the return value of the method call
	 */
	public static Object callMethod(Object obj, String method, Object[] args) {
		return ReflectUtils.callMethod(obj, method, args);
	}

	/**
	 * Check if an object's string value equals to any item's string value in a
	 * collection.
	 * 
	 * @param value
	 *            the object
	 * @param collection
	 *            the collection
	 * @return true if yes, false no
	 */
	public static boolean in(Object value, Collection collection) {
		if (value == null) {
			return false;
		}
		value = value.toString();

		for (Object item : collection) {
			if (String.valueOf(item).equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Break a string to parts.
	 * 
	 * @param str
	 *            the string
	 * @param delimiters
	 *            delimiters applicable for StringTokenizer. If null given,
	 *            ", \r\n\f\t" will be used.
	 * @return the order set containing the parts
	 */
	public static Set<String> breakString(String str, String delimiters) {
		return AbstractTag.parseDelimitedString(str, delimiters);
	}

	/**
	 * Load a class from class name.
	 * 
	 * @param clazz
	 *            the class name
	 * @return the loaded class, never null
	 */
	public static Class loadClass(String clazz) {
		return ReflectUtils.loadClassStrict(clazz, null);
	}

	/**
	 * Quote a pattern literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return the quoted literal to be used in pattern
	 */
	public static String quotePattern(String literal) {
		return Pattern.quote(literal);
	}

	/**
	 * Get today.
	 * 
	 * @return today
	 */
	public static Date today() {
		return DateUtils.truncate(new Date(), Calendar.DATE);
	}

	/**
	 * Count how many elements is in the given
	 * list/map/iterator/enumeration/array.
	 * 
	 * @param obj
	 *            list/map/iterator/enumeration/array
	 * @return number of elements
	 */
	public static int count(Object obj) {

		if (obj == null) {
			return 0;
		} else if (obj instanceof Collection || obj instanceof Map || obj instanceof Iterator || obj instanceof Enumeration || obj.getClass().isArray()) {
			return CollectionUtils.size(obj);
		} else if (obj instanceof Iterable) {
			return count(((Iterable) obj).iterator());
		} else {
			return 1;
		}

	}

	/**
	 * Call a tag.
	 * 
	 * @param name
	 *            the tag name
	 * @param args
	 *            the arguments map
	 * @return the result
	 */
	public static Object call(String name, Map<String, Object> args) {
		final Context context = Context.getCurrentContext();
		final IEntity tag = context.getPaxml().getEntity(name);
		if (tag == null) {
			throw new PaxmlRuntimeException("No tag defined as: " + name);
		}

		final Context subContext = new Context(context);
		subContext.setAsCurrentThreadContext();
		try {
			if (args != null) {
				for (Map.Entry<String, Object> entry : args.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					subContext.addConst(key, key, value, false);
				}
			}
			return tag.execute(subContext);
		} finally {
			context.setAsCurrentThreadContext();
		}

	}

	/**
	 * Make a string from ascii code.
	 * 
	 * @param ascii
	 *            ascii codes
	 * @return the string
	 */
	public static String makeString(byte... ascii) {
		StringBuilder sb = new StringBuilder();
		for (byte a : ascii) {
			sb.append((char) a);
		}
		return sb.toString();
	}

	/**
	 * Get the caller's paxml resource.
	 * 
	 * @return the resource
	 */
	public static PaxmlResource getCallerResource() {
		return Context.getCurrentContext().findCallerEntity().getResource();
	}

	/**
	 * Get the current paxml resource.
	 * 
	 * @return the resource
	 */
	public static PaxmlResource getCurrentyResource() {
		return Context.getCurrentContext().getCurrentEntity().getResource();
	}

	/*
	 * public static ITag getCurrentTag() { return
	 * Context.getCurrentContext().getCurrentTag(); }
	 * 
	 * public static IpaxmlEntity getCurrentEntity() { return
	 * Context.getCurrentContext().getCurrentEntity(); }
	 * 
	 * public static void addpaxmlResource(String[] pathWithPrefixs) { for
	 * (String p : pathWithPrefixs) {
	 * Context.getCurrentContext().getpaxml().addResources
	 * (paxmlResource.createFromPath(p)); } }
	 * 
	 * public static void addpaxmlTagLibrary(String[] classNames) { for (String
	 * cn : classNames) { Class<? extends ITagLibrary> clazz = (Class<? extends
	 * ITagLibrary>) ReflectUtils.loadClassStrict(cn, null);
	 * Context.getCurrentContext().getpaxml().addTagLibrary(clazz); } }
	 * 
	 * public static void addpaxmlListener(String[] classNames) throws Exception
	 * { paxml paxml = Context.getCurrentContext().getpaxml(); for (String cn :
	 * classNames) {
	 * 
	 * Class<?> clazz = ReflectUtils.loadClassStrict(cn, null); if
	 * (ReflectUtils.isImplementingClass(clazz, IpaxmlExecutionListener.class,
	 * true)) { paxml.addpaxmlExecutionListener(((Class<? extends
	 * IpaxmlExecutionListener>) clazz).newInstance()); } else if
	 * (ReflectUtils.isImplementingClass(clazz, IEntityExecutionListener.class,
	 * true)) { paxml.addEntityExecutionListener(((Class<? extends
	 * IEntityExecutionListener>) clazz).newInstance()); } else if
	 * (ReflectUtils.isImplementingClass(clazz, ITagExecutionListener.class,
	 * true)) { paxml.addTagExecutionListener(((Class<? extends
	 * ITagExecutionListener>) clazz).newInstance()); } else { throw new
	 * paxmlRuntimeException("Unknown listener type: " + clazz.getName()); } } }
	 */
	/**
	 * Run a random paxml xml string.
	 * 
	 * @param paxml
	 *            the paxml xml string
	 * @return the execution result
	 */
	public static Object runPaxml(String paxml) {
		if (StringUtils.isBlank(paxml)) {
			return null;
		}
		paxml = "<scenario>" + paxml.trim() + "</scenario>";
		Context context = Context.getCurrentContext();
		Paxml _paxml = context.getPaxml();
		IEntity entity = _paxml.getParser().parse(new InMemoryResource(paxml), true, null);

		return _paxml.execute(entity, context, false, false);

	}

	/**
	 * Check if a tag name is callable.
	 * 
	 * @param tagName
	 *            the name of the tag
	 * @return true callable, false not
	 */
	public static boolean isCallable(String tagName) {
		return null != Context.getCurrentContext().getPaxml().getResourceLocator().getResource(tagName);
	}

	/**
	 * Find const id by given value.
	 * 
	 * @param obj
	 *            the given value
	 * @param strict
	 *            true to do pointer comparison, false to do object equality
	 *            comparison
	 * @param searchParent
	 *            true to look also in parent contexts, false only look in the
	 *            current context
	 * @param excludes
	 *            ids to ignore
	 * @return the id or null if no found.
	 */
	public static String findConstIdExcept(Object obj, boolean strict, boolean searchParent, String[] excludes) {
		return Context.getCurrentContext().getParent().findConstId(obj, strict, searchParent, excludes == null ? new String[] {} : excludes);
	}

	/**
	 * Find const id by given value.
	 * 
	 * @param obj
	 *            the given value
	 * @param strict
	 *            true to do pointer comparison, false to do object equality
	 *            comparison
	 * @param searchParent
	 *            true to look also in parent contexts, false only look in the
	 *            current context
	 * 
	 * @return the id or null if no found.
	 */
	public static String findConstId(Object obj, boolean strict, boolean searchParent) {
		return findConstIdExcept(obj, strict, searchParent, null);
	}

	/**
	 * Find const ids by given value collection.
	 * 
	 * @param col
	 *            the value collection
	 * @param strict
	 *            true to do pointer comparison, false to do object equality
	 *            comparison
	 * @param searchParent
	 *            searchParent true to look also in parent contexts, false only
	 *            look in the current context
	 * @return a list of ids corresponding to each element in the given
	 *         collection. Not found values will return null id in the list.
	 */
	public static List<String> findConstIds(Collection col, boolean strict, boolean searchParent) {
		List<String> ids = new ArrayList<String>();
		for (Object obj : col) {
			ids.add(findConstId(obj, strict, searchParent));
		}
		return ids;
	}

	/**
	 * Check if an object is empty. An object is empty if it is either of the
	 * following case: - null - empty string - empty collection - empty map -
	 * empty array.
	 * 
	 * @param obj
	 *            the object to examine
	 * @return true empty, false not
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof String) {
			return ((String) obj).isEmpty();

		} else if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		} else if (obj instanceof Map) {
			return ((Map) obj).isEmpty();
		} else if (obj.getClass().isArray()) {
			return 0 == Array.getLength(obj);
		}

		return false;
	}

	/**
	 * Check if a value is logically equivalent to true, where the check logic
	 * is like javascript boolean syntax.
	 * 
	 * @param obj
	 *            the value
	 * @return
	 */
	public static boolean yes(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Number) {
			if (0 == new BigDecimal(((Number) obj).toString()).compareTo(new BigDecimal(0))) {
				return false;
			}
		} else if (obj instanceof Collection) {
			if (((Collection) obj).isEmpty()) {
				return false;
			}
		} else if (obj instanceof Map) {
			if (((Map) obj).isEmpty()) {
				return false;
			}
		} else if (obj.getClass().isArray()) {
			if (0 == Array.getLength(obj)) {
				return false;
			}
		}
		final String str = String.valueOf(obj);
		if ("".equals(str) || "false".equals(str) || "null".equals(str)) {
			return false;
		}

		return true;

	}

	/**
	 * The opposite of yes.
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean no(Object obj) {
		return !yes(obj);
	}

	/**
	 * Get the current process id.
	 * 
	 * @return the pid
	 */
	public static long getProcessId() {
		return Context.getCurrentContext().getProcessId();
	}

	/**
	 * Get the 1st non-empty object.
	 * 
	 * @param any
	 *            all candidates
	 * @return the 1st non-empty object, or null if none found.
	 */
	public static Object any(Object... any) {
		for (Object obj : any) {

			if (obj != null && StringUtils.isNotEmpty(obj.toString())) {
				return obj;
			}
		}
		return null;
	}

	/**
	 * Formats a String by replacing the {[0-9]+} placeholder.
	 * 
	 * @param formattedString
	 *            - String that needs to be formatted with pattern
	 *            "string {0} and {1}".
	 * @param stringReplaceables
	 *            . an array formating parameters.
	 * @return the formatted String.
	 */
	public static String formatString(String formattedString, String[] stringReplaceables) {

		int i = 0;
		for (String replaceable : stringReplaceables) {
			String regexPattern = "\\{" + i + "\\}";
			formattedString = formattedString.replaceAll(regexPattern, replaceable);
			i++;
		}
		return formattedString;
	}

	/**
	 * Print date/time into xml format.
	 * 
	 * @param cal
	 *            the calendar or date that represents the datetime. If null
	 *            given, it will take the current system time.
	 * @param includeTime
	 *            true to include time part, false exclude time part
	 * @return the xml standard date/time string
	 */
	public static String printXmlDateTime(Object cal, boolean includeTime) {
		Calendar c = null;
		if (cal == null) {
			c = new GregorianCalendar();
		} else if (cal instanceof Calendar) {
			c = (Calendar) cal;
		} else if (cal instanceof Date) {
			c = new GregorianCalendar();
			c.setTime((Date) cal);
		} else {
			throw new PaxmlRuntimeException("Unsupported date time of type " + cal.getClass().getName() + ": " + cal);
		}
		return includeTime ? DatatypeConverter.printDateTime(c) : DatatypeConverter.printDate(c);

	}

	/**
	 * Parse the standard xml date time.
	 * 
	 * @param str
	 *            the date/time string
	 * @return the calendar obj
	 */
	public static Calendar parseXmlDateTime(String str) {
		return DatatypeConverter.parseDateTime(str);

	}

	public static String transform(String template, Object obj) {
		return null;
	}

	public static Secret getSecret(String name) {
		return Context.getCurrentContext().getSecret(name);

	}

	public static void setSecret(String name, String value) {
		Context.getCurrentContext().setSecret(name, value);
	}

	public static String ask(String question, boolean mask) {
		class DummyFrame extends JFrame {
			DummyFrame(String title) {
				super(title);
				setUndecorated(true);
				setVisible(true);
				setLocationRelativeTo(null);
			}
		}
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(new DummyFrame(question), pf, question, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (okCxl == JOptionPane.OK_OPTION) {
			return new String(pf.getPassword());
		}
		return null;
	}

	public static List sort(Collection list) {
		return sort(list, true);
	}

	public static List sort(Collection list, boolean asc) {
		List li = new ArrayList(list);
		if (asc) {
			Collections.sort(li);
		} else {
			Collections.reverse(li);
		}
		return li;
	}

	public static Object compress(Collection col) {
		switch (col.size()) {
		case 0:
			return null;
		case 1:
			return col.iterator().next();
		}
		return col;
	}

	public static List collect(Object... objs) {
		List list = new ArrayList();
		for (Object obj : objs) {
			ReflectUtils.collect(obj, list, true);
		}
		return list;
	}

	public static Object compactCollect(Object... objs) {
		return compress(collect(objs));
	}

	public static boolean confirm(String... args) {
		String msg;
		if (args.length == 0) {

			msg = "Do you want to contibue?";
		} else {
			msg = args[0];
		}
		String[] yes;
		if (args.length > 1) {
			yes = new String[args.length - 1];
			System.arraycopy(args, 1, yes, 0, yes.length);
		}else{
			yes = new String[] { "y", "yes" };
			msg += " (y/n)";
		}
		System.out.println(msg);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			throw new PaxmlRuntimeException("Cannot read from console", e);
		}
		for (String y : yes) {
			if (y.trim().equalsIgnoreCase(line.trim())) {
				return true;
			}
		}
		return false;
	}

}
