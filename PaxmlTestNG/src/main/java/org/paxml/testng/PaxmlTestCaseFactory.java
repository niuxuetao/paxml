/**
 * This file is part of PaxmlTestNG.
 *
 * PaxmlTestNG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlTestNG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlTestNG.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.testng;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.Modifier;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.launch.LaunchModel;
import org.paxml.launch.LaunchPoint;
import org.paxml.launch.Matcher;
import org.paxml.launch.Paxml;
import org.paxml.tag.AbstractTag;
import org.paxml.testng.AbstractPaxmlTestResult.ResultType;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * paxml test case factory for TestNG.
 * 
 * @author Xuetao Niu
 * 
 */
public class PaxmlTestCaseFactory {
	private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
	/**
	 * The paxml launch plan file path.
	 */
	public static final String PARAM_NAME_PLANFILE = "paxmlTestPlanFile";
	public static final String PARAM_NAME_SUPPRESS_GROUPS = "paxmlSuppressGroups";
	public static final String PARAM_NAME_RESULT_DIR = "paxmlTestResultDir";
	public static final String PARAM_NAME_RESULT_TYPE = "paxmlTestResultType";
	public static final String PARAM_NAME_PREFIX_WITH_PID = "paxmlTestNamePrefixWithPid";

	private static final Log log = LogFactory.getLog(PaxmlTestCaseFactory.class);
	private static final Object LOCK = new Object();

	// this hashmap is accessed only from synchronization block, so no need to
	// have extra synchronization nor be concurrent hash map.
	private static final Map<String, Constructor<? extends PaxmlTestCase>> CACHE = new HashMap<String, Constructor<? extends PaxmlTestCase>>();

	public static interface ILockedOperation<T> {
		T perform();
	}

	/**
	 * The factory method.
	 * 
	 * @param planFile
	 *            the plan file
	 * @param suppressedGroups
	 *            the execution groups to suppress
	 * @param outputDir
	 *            the dir to output results
	 * @param resultType
	 *            the format of results
	 * @return the test objects.
	 */
	@Factory
	@Parameters({ PARAM_NAME_PLANFILE, PARAM_NAME_SUPPRESS_GROUPS, PARAM_NAME_RESULT_DIR, PARAM_NAME_RESULT_TYPE, PARAM_NAME_PREFIX_WITH_PID })
	public Object[] create(final String planFile, @Optional("") final String suppressedGroups, @Optional("./target/surefire-reports/paxml/results") final String outputDir,
	        @Optional("JSON") final String resultType, @Optional("false") final boolean prefixNameWithPid) {
		final List<Matcher> suppression = new ArrayList<Matcher>(0);
		for (String groupName : AbstractTag.parseDelimitedString(suppressedGroups, null)) {
			Matcher matcher = new Matcher();
			matcher.setMatchPath(false);
			matcher.setPattern(groupName);
			suppression.add(matcher);
		}
		return performLocked(new ILockedOperation<Object[]>() {

			@Override
			public Object[] perform() {

				File resultFolder = null;
				ResultType rt = null;
				final long start = System.currentTimeMillis();
				try {
					File dir = new File(outputDir);
					rt = ResultType.valueOf(resultType.toUpperCase());
					resultFolder = new File(dir, SEQUENCE.getAndIncrement() + "/");
					Object[] cases = createTestCases(planFile, suppression.isEmpty() ? null : suppression, resultFolder, rt, prefixNameWithPid);
					PaxmlTestCase.init(cases.length, start, FilenameUtils.getBaseName(planFile));

					// clean the paxml thread context and log into the default
					// file
					Context.cleanCurrentThreadContext();
					if (log.isInfoEnabled()) {
						log.info("Launching totally " + cases.length + " tests ...");
					}

					return cases;
				} catch (Throwable e) {
					if (log.isErrorEnabled()) {
						log.error("Cannot create test cases", e);
					}
					return new Object[] { new PaxmlPlanFileFailure(e, planFile, resultFolder, rt, Context.getCurrentContext(), Thread.currentThread().getName(), start,
					        System.currentTimeMillis()) };
				}
			}

		});
	}

	/**
	 * Let the shared context be propagated to other threads.
	 * 
	 * @param <T>
	 * @param op
	 * @return
	 */
	public static <T> T performLocked(ILockedOperation<T> op) {
		synchronized (LOCK) {
			return op.perform();
		}
	}

	private Object[] createTestCases(String planFile, List<Matcher> suppression, File outputDir, ResultType resultType, boolean prefixPid) {

		LaunchModel model = Paxml.executePlanFile(planFile, null);

		List<LaunchPoint> points = model.getLaunchPoints(false, -1);

		List<Object> result = new LinkedList<Object>();
		for (LaunchPoint p : points) {
			if (isSuppressed(suppression, p.getGroup())) {
				if (log.isInfoEnabled()) {
					log.info("This scenario '" + p.getResource().getName() + "' will not run because its group is suppressed: " + p.getGroup());
				}
			} else {
				result.add(createTestCase(p, outputDir, resultType, prefixPid ? points.size() : null));
			}

		}
		if (result.isEmpty()) {

			if (log.isWarnEnabled()) {
				log.warn("No scenarios will run from plan file:" + planFile);
			}
		}
		return result.toArray(new Object[result.size()]);

	}

	private static Object createTestCase(LaunchPoint p, File outputDir, ResultType resultType, Integer total) {
		String className = p.getResource().getName();

		if (StringUtils.isNoneBlank(p.getGroup())) {
			className = p.getGroup() + "." + className;
		}
		if (total != null) {
			// left pad with 0 because testng sorts test FQN alphabetically 
			className = "PID_" + StringUtils.leftPad("" + p.getProcessId(), total.toString().length(), '0') + "." + className;
		}
		try {
			Constructor<? extends PaxmlTestCase> constructor = CACHE.get(className);
			if (constructor == null) {
				ClassPool pool = ClassPool.getDefault();
				if (log.isInfoEnabled()) {
					log.info("Generating test class proxy:" + className);
				}
				CtClass testclass = pool.makeClass(className);
				final CtClass superClass = pool.get(PaxmlTestCase.class.getName());
				testclass.setSuperclass(superClass);
				testclass.setModifiers(Modifier.PUBLIC);

				// Add a constructor which will call super( ... );
				CtClass[] params = new CtClass[] { pool.get(LaunchPoint.class.getName()), pool.get(File.class.getName()), pool.get(ResultType.class.getName()) };
				final CtConstructor ctor = CtNewConstructor.make(params, null, CtNewConstructor.PASS_PARAMS, null, null, testclass);
				testclass.addConstructor(ctor);

				Class<? extends PaxmlTestCase> c = testclass.toClass();
				constructor = c.getConstructor(new Class[] { LaunchPoint.class, File.class, ResultType.class });
				CACHE.put(className, constructor);
			}
			return constructor.newInstance(new Object[] { p, outputDir, resultType });

		} catch (Exception e) {
			throw new RuntimeException("Could not create test case: " + className, e);
		}
	}

	private boolean isSuppressed(List<Matcher> suppression, String groupName) {
		if (suppression == null || suppression.isEmpty()) {
			return false;
		}
		for (Matcher m : suppression) {
			if (m.match(groupName)) {
				return true;
			}
		}
		return false;
	}
}
