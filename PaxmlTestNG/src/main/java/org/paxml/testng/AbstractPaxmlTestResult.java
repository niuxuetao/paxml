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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.core.Context.Stack.IStackTraverser;
import org.paxml.core.IEntity;
import org.paxml.selenium.rc.SeleniumTag;
import org.paxml.selenium.rc.XSelenium;
import org.paxml.selenium.rc.XSelenium.SnapshotInfo;
import org.paxml.tag.ITag;
import org.paxml.testng.PaxmlTestCaseFactory.ILockedOperation;
import org.paxml.util.XmlUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * paxml test case impl for testNG.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class AbstractPaxmlTestResult {

	public static enum ResultType {
		JSON("js"), XML("xml");

		private final String ext;

		private ResultType(String ext) {
			this.ext = ext;
		}

		public String getExt() {
			return ext;
		}

	}

	private static final Log log = LogFactory.getLog(AbstractPaxmlTestResult.class);

	private final long processId;
	private final File outputDir;
	private final ResultType resultType;
	private final String entityName;
	private final String group;
	private boolean success = false;
	private final Map<?, ?> initialProps;

	/**
	 * Construct from factors.
	 * 
	 * @param point
	 *            the launch point
	 * 
	 */
	public AbstractPaxmlTestResult(String group, String entityName, long processId, File outputDir, ResultType resultType, Map<?, ?> initialProps) {

		if (resultType == null) {
			resultType = ResultType.JSON;
		}
		this.group = group;
		this.initialProps = initialProps;
		this.entityName = entityName;
		this.resultType = resultType;
		this.outputDir = outputDir;
		this.processId = processId;

	}

	@Override
	public String toString() {
		return getClass().getName() + ":" + getProcessId();
	}

	protected void addMap(Map<?, ?> map, List<Const> consts) {
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Const c = new Const();
			c.setName(String.valueOf(entry.getKey()));
			Object value = entry.getValue();
			c.setValue(String.valueOf(value));
			if (value != null) {
				c.setType(value.getClass().getName());
			}
			consts.add(c);
		}
		Collections.sort(consts, new Comparator<Const>() {

			@Override
			public int compare(Const o1, Const o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
	}

	protected abstract Context getContext();

	protected abstract void onSummary(TestResultSummary s);

	protected abstract void doTest() throws Throwable;

	protected abstract String getThreadName();

	protected abstract long getStartMs();

	protected abstract long getStopMs();

	@Test
	public void executeTest() throws Throwable {
		doTest();
		success = true;
	}

	@AfterMethod
	public void populateReport(final ITestResult result) throws Exception {
		try {
			PaxmlTestCaseFactory.performLocked(new ILockedOperation<Object>() {

				@Override
				public Object perform() {
					try {
						doPopulateReport(result);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return null;
				}

			});
		} catch (Throwable t) {
			log.error("Cannot generate test result", t);
		}
	}

	private void doPopulateReport(ITestResult result) throws Exception {

		final Context context = getContext();
		final Context exceptionContext = context == null ? null : context.getExceptionContext();

		final TestResultSummary s = new TestResultSummary();

		try {
			final TestResult r = new TestResult();

			r.setProcessId(processId);
			r.setEntityName(entityName);
			r.setGroup(group);
			r.setThreadName(getThreadName());
			r.setSuccessful(success);
			r.setStart(getStartMs());
			r.setStop(getStopMs());

			s.setProcessId(r.getProcessId());
			s.setGroup(r.getGroup());
			s.setEntityName(r.getEntityName());
			s.setThreadName(r.getThreadName());
			s.setSuccessful(r.isSuccessful());
			s.setStart(r.getStart());
			s.setStop(r.getStop());

			ContextDump dump = new ContextDump();
			r.setContextDump(dump);

			for (SnapshotInfo si : XSelenium.getSnapshots(context)) {
				ScreenshotInfo info = new ScreenshotInfo();
				r.getScreenshots().add(info);
				info.setFileName(si.getFile().getName());

				for (XSelenium.CallStack cs : si.getCallStack()) {
					CallStack _cs = new CallStack();
					_cs.setEntityName(cs.getEntity().getResource().getName());
					_cs.setTagName(cs.getTag().getTagName());
					_cs.setLine(cs.getTag().getLineNumber());
					info.getCallStack().add(_cs);
				}

			}

			addMap(System.getProperties(), r.getSystemProperties());

			if (initialProps != null) {
				addMap(initialProps, r.getInitialProperties());
				s.getInitialProperties().addAll(r.getInitialProperties());
			}

			if (success) {
				if (context != null) {
					addMap(context.getIdMap(true, false), dump.getContextValue());
				}
			} else {
				r.setErrorMessage(result.getThrowable().getMessage());
				s.setErrorMessage(r.getErrorMessage());
				if (exceptionContext != null) {
					addMap(exceptionContext.getIdMap(true, false), dump.getContextValue());

					final File file = SeleniumTag.getErrorSnapshot(exceptionContext);
					if (file != null) {
						r.setErrorScreenshotName(file.getName());
					} else {
						if (log.isWarnEnabled()) {
							log.warn(getTitle() + " failed but has no selenium snapshots attached. It may have failed before opening the browser.");
						}
					}

					// compose error stack
					exceptionContext.getStack().traverse(new IStackTraverser() {

						@Override
						public boolean onItem(IEntity entity, ITag tag) {
							CallStack c = new CallStack();
							r.getErrorStack().add(c);
							c.setLine(tag.getLineNumber());
							c.setEntityName(entity.getResource().getName());
							c.setTagName(tag.getTagName());
							return true;
						}
					});
				}
			}

			// write the log detached to from the current test case.
			Context.cleanCurrentThreadContext();

			writeReportFile(r, false);
		} finally {
			onSummary(s);
		}
	}

	public long getProcessId() {
		return processId;
	}

	public File getOutputDir() {
		return outputDir;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getTitle() {
		return processId + ":" + entityName + "@" + getThreadName();
	}

	public File writeReportFile(Object tr, boolean index) {
		String str = null;
		if (resultType == ResultType.JSON) {
			str = XmlUtils.toJson(tr);
		} else if (resultType == ResultType.XML) {
			str = serializeXml(tr);
		} else {
			throw new RuntimeException("Unknown result type: " + resultType);
		}
		if (null != outputDir) {
			final String fn = index ? "index" : new Long(processId).toString();
			final File file = new File(outputDir, fn + "." + resultType.getExt());
			if (log.isInfoEnabled()) {
				if (index) {
					log.info("Outputting test result index to: " + file.getAbsolutePath());
				} else {
					log.info("Outputting test result to: " + file.getAbsolutePath());

				}

			}

			outputDir.mkdirs();
			try {
				FileUtils.writeStringToFile(file, str, "UTF-8");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return file;
		} else {
			if (log.isWarnEnabled()) {
				if (index) {
					log.warn("No output dir specified, therefore no test result index file will be saved.");
				} else {
					log.warn("No output dir specified, therefore no test result file will be saved after each test case.");

				}

			}
			return null;
		}

	}


	public static String serializeXml(Object obj) {
		final String encoding = "UTF-8";
		try {
			JAXBContext c = JAXBContext.newInstance(obj.getClass());
			Marshaller m = c.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, encoding);
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(obj, out);
			return out.toString(encoding);
		} catch (Exception e) {
			throw new RuntimeException("Cannot serialize jaxb object of class: " + obj.getClass(), e);
		}
	}

}
