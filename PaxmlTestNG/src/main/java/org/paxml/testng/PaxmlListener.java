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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.xml.XmlSuite;

/**
 * Listener to prepare report viewer.
 * 
 * @author Xuetao Niu
 * 
 */
public class PaxmlListener extends TestListenerAdapter implements IReporter {
	public static final String REPORTER_DIR = (PaxmlListener.class.getPackage()
			.getName().replace('.', '/') + "/report");
	private static final ClassPathResource RES = new ClassPathResource(
			REPORTER_DIR + "/frame.html");

	private static final AtomicLong SEQUENCE = new AtomicLong(0);
	private static final Log log = LogFactory.getLog(PaxmlListener.class);

	static {
		final String[] files = { "index.html", "blankDetail.html",
				"detail.html", "screenshots.html", "jquery-1.9.1.min.js",
				"jquery.imagesloaded.min.js", "jquery.scrollTo-1.4.3.1-min.js",
				"common.js", "style.css" };
		// before all tests, copy the viewer to the target dir
		File dest = new File("target/surefire-reports/paxml/reportViewer");
		dest.mkdirs();
		try {
			for (String file : files) {
				copyFile(file, dest);
				if (log.isDebugEnabled()) {
					log.debug("Copied report generic viewer to: "
							+ dest.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private volatile boolean reportWritten = false;
	private volatile String suiteName;
	private final long sequence;

	/**
	 * Constructor.
	 */
	public PaxmlListener() {
		super();
		sequence = SEQUENCE.getAndIncrement();
		if (log.isDebugEnabled()) {
			log.debug("paxml listener instance created with result sequence: "
					+ sequence);
		}
	}

	private static void copyFile(String name, File destDir) throws IOException {

		ClassPathResource res = new ClassPathResource(REPORTER_DIR + "/" + name);
		FileOutputStream out = null;
		InputStream in = null;
		try {
			out = new FileOutputStream(new File(destDir, name));
			in = res.getInputStream();
			IOUtils.copy(in, out);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart(ITestContext testContext) {
		if (suiteName == null) {
			suiteName = testContext.getCurrentXmlTest().getSuite().getName();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTestFailure(ITestResult tr) {
		if (!reportWritten) {
			overwriteReport();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onTestSuccess(ITestResult tr) {
		if (!reportWritten) {
			overwriteReport();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generateReport(List<XmlSuite> arg0, List<ISuite> arg1,
			String arg2) {
		if (log.isDebugEnabled()) {
			log.debug("Generating paxml report ... ");
		}
		overwriteReport();
	}

	private void overwriteReport() {

		InputStream in = null;
		OutputStream out = null;
		try {
			in = RES.getInputStream();
			ByteArrayOutputStream array = new ByteArrayOutputStream();
			IOUtils.copy(in, array);
			String content = array.toString("UTF-8");
			content = content.replace("%{SEQUENCE}%", sequence + "");
			content = content.replace("%{TIMESTAMP}%",
					System.currentTimeMillis() + "");
			content = content.replace("%{SUIT_NAME}%", suiteName);
			File file = new File("./target/surefire-reports/" + suiteName
					+ "/index.html");
			file.getParentFile().mkdirs();
			FileUtils.writeStringToFile(file, content, "UTF-8");
			if (log.isDebugEnabled()) {
				log.debug("Report for test suit '" + suiteName
						+ "' overwritten with sequence number " + sequence
						+ ", file: " + file.getAbsolutePath());
			}
		} catch (Exception e) {
			throw new RuntimeException(
					"Cannot overwrite report index for test suit: " + suiteName,
					e);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
		reportWritten = true;
	}

}
