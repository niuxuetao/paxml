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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.launch.LaunchPoint;
import org.testng.Assert;

/**
 * paxml test case impl for testNG.
 * 
 * @author Xuetao Niu
 * 
 */
public class PaxmlTestCase extends AbstractPaxmlTestResult {

	private static final Log log = LogFactory.getLog(PaxmlTestCase.class);

	private static final AtomicInteger TOTAL = new AtomicInteger(0);
	private static final AtomicInteger SUCCEEDED = new AtomicInteger(0);
	private static final AtomicInteger FAILED = new AtomicInteger(0);
	private static volatile TestResultIndex RESULT_INDEX;
	private static final Object LOCK = new Object();
	private final LaunchPoint point;

	/**
	 * Construct from factors.
	 * 
	 * @param point
	 *            the launch point
	 * 
	 */
	public PaxmlTestCase(final LaunchPoint point, File outputDir, ResultType resultType) {
		super(point.getGroup(), point.getResource().getName(), point.getProcessId(), outputDir, resultType, point.getFactors());
		this.point = point;
	}

	private void logSummary(boolean success) {

		if (success) {
			if (log.isInfoEnabled()) {
				log.info("Test succeeded: " + getTitle() + ". Currently succeeded " + SUCCEEDED.incrementAndGet() + " and failed " + FAILED.get() + " of total " + TOTAL.get()
				        + " tests.");
			}
		} else {
			if (log.isErrorEnabled()) {
				log.error("Test failed: " + getTitle() + ". Currently succeeded " + SUCCEEDED.get() + " and failed " + FAILED.incrementAndGet() + " of total " + TOTAL.get()
				        + " tests.");
			}
		}
	}

	@Override
	protected Context getContext() {
		return Context.getCurrentContext();
	}

	@Override
	protected void onSummary(TestResultSummary s) {

		logSummary(s.isSuccessful());

		synchronized (LOCK) {
			RESULT_INDEX.setStop(System.currentTimeMillis());
			RESULT_INDEX.getSummary().add(s);
			writeReportFile(RESULT_INDEX, true);
		}
	}

	@Override
	protected String getThreadName() {
		return Thread.currentThread().getName();
	}

	@Override
	protected long getStartMs() {
		return point.getStartMs();
	}

	@Override
	protected long getStopMs() {
		return point.getStopMs();
	}

	/**
	 * The test method.
	 */
	@Override
	protected void doTest() {
		// this will only run for scenario, never for the plan file.
		// the plan file's execution will be done in the test case factory.
		try {
			Context.cleanCurrentThreadContext();

			if (log.isInfoEnabled()) {
				log.info("Starting test: " + getTitle() + " of totally " + TOTAL.get() + " tests.");
			}

			point.execute();
		} catch (Throwable t) {
			if (log.isErrorEnabled()) {
				log.error(t.getMessage(), t);
			}
			Assert.fail(t.getMessage());
		}
	}

	static void init(int totalTestCases, long startMs, String planEntityName) {
		synchronized (LOCK) {
			TOTAL.set(totalTestCases);
			FAILED.set(0);
			SUCCEEDED.set(0);
			RESULT_INDEX = new TestResultIndex();
			RESULT_INDEX.setStart(startMs);
			RESULT_INDEX.setPlanEntityName(planEntityName);
		}
	}
}
