/**
 * This file is part of PaxmlSelenium.
 *
 * PaxmlSelenium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlSelenium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlSelenium.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.selenium.rc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.paxml.core.IExecutionListener;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.launch.Paxml;

/**
 * The base impl of selenium tags.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class SeleniumTag extends BeanTag {
	public static final String DEFAULT_BROWSER = "*firefox";

	/**
	 * The keys for global internal objects.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static enum Keys {
		ERROR_SNAPSHOT, SNAPSHOTS
	}

	/**
	 * Flag of mock ups.
	 */
	public static final boolean MOCK = false;
	/**
	 * The selenium rc host property name.
	 */
	public static final String SELENIUM_RC_HOST = "selenium.rc.host";
	/**
	 * The selenium rc port property name.
	 */
	public static final String SELENIUM_RC_PORT = "selenium.rc.port";
	/**
	 * The selenium browser identifier property name.
	 */
	public static final String SELENIUM_BROWSER_IDENTIFIER = "selenium.browser";
	/**
	 * The folder containing selenium snapshots
	 */
	public static final String SELENIUM_SNAPSHOT_FOLDER = "selenium.snapshot.folder";

	private static final Log log = LogFactory.getLog(SeleniumTag.class);

	/**
	 * The execution listener.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	public static class PaxmlListener implements IExecutionListener, Runnable {
		private final List<XSelenium> seleniums;
		private volatile boolean done = false;

		PaxmlListener(final List<XSelenium> seleniums) {
			this.seleniums = seleniums;
		}

		/**
		 * {@inheritDoc}
		 */
		public void onEntry(Paxml paxml, Context context) {
			// do nothing
		}

		/**
		 * {@inheritDoc} Last line of defense.
		 */
		@Override
		public synchronized void run() {

			checkToClose(null);

		}

		private synchronized void checkToClose(Context context) {
			if (done) {
				return;
			}
			for (XSelenium selenium : seleniums) {
				try {
					if (context == null) {
						log.warn("Cleaning up selenium session without paxml context");
						selenium.terminate();
					} else if (context.getExceptionContext() == null) {
						log.info("Cleaning up selenium session after successful execution");
						selenium.terminate();
					} else {
						try {
							// take snapshot because the test has errors.
							if (selenium.getSnapshotsPath() != null) {
								File file = selenium.takeSnapshot(context);
								setErrorSnapshot(context.getExceptionContext(), file);
							}
						} finally {
							boolean keep = true;
							try {
								keep = isKeepSessionOnError(context);
							} finally {
								if (keep) {
									if (log.isWarnEnabled()) {
										log.warn("The selenium session is not closed in the end," + " because the last <url> tag has @keepSessionOnError='true'!");
									}
								} else {
									log.info("Cleaning up selenium session after failed execution");
									selenium.terminate();
								}
							}
						}
					}
				} catch (Exception e) {
					if (log.isWarnEnabled()) {
						log.warn("Error closing selenium session", e);
					}
				}
			}
			done = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void onExit(Paxml paxml, Context context) {
			try {
				checkToClose(context);
			} finally {
				// make sure to clean it otherwise the same thread is polluted.
				for (PrivateKeys pk : PrivateKeys.values()) {
					context.removeInternalObject(pk, true);
				}
			}
		}

	}

	/**
	 * The private context keys.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static enum PrivateKeys {
		SELENIUM, URL, LISTENER, KEEP_SESSION_ON_ERROR, SELENIUMS
	}

	/**
	 * Handler if mocked.
	 * 
	 * @param context
	 *            the context
	 * @return null
	 */
	protected Object onMocked(Context context) {
		System.out.println(this);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final Object doInvoke(Context context) {
		if (MOCK) {
			return onMocked(context);
		} else {

			Object result = onCommand(context);
			return result;

		}
	}

	/**
	 * The actual command impl.
	 * 
	 * @param context
	 *            the context
	 * @return the command return.
	 */
	protected abstract Object onCommand(Context context);

	/**
	 * Get the start url.
	 * 
	 * @param context
	 *            the context
	 * @return the url, null if unknown.
	 */
	public static String getStartUrl(Context context) {
		String url = (String) context.getInternalObject(PrivateKeys.URL, true);
		if (StringUtils.isBlank(url)) {
			// create a random uuid to satisfy browser
			url = "http://" + UUID.randomUUID().toString();
		}
		return url;
	}

	/**
	 * Set the start url.
	 * 
	 * @param context
	 *            the context
	 * @param url
	 *            the url
	 */
	public static void setStartUrl(Context context, String url) {
		context.setInternalObject(PrivateKeys.URL, url, true);
	}

	/**
	 * Get browser identifier.
	 * 
	 * @param context
	 *            the context
	 * @return the identifier of browser, null if unknown.
	 */
	public static String getBrowserIdentifier(Context context) {

		return (String) context.getConst(SELENIUM_BROWSER_IDENTIFIER, true);
	}

	/**
	 * Get selenium rc host.
	 * 
	 * @param context
	 *            the context
	 * @return the rc host, null if unknown
	 */
	public static String getRcServerHost(Context context) {
		return (String) context.getConst(SELENIUM_RC_HOST, true);
	}

	/**
	 * Get selenium rc port.
	 * 
	 * @param context
	 *            the context
	 * @return the rc port, null if unknown
	 */
	public static Integer getRcServerPort(Context context) {
		try {
			return Integer.parseInt(context.getConst(SELENIUM_RC_PORT, true) + "");
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Check how selenium should behave at the end of the execution.
	 * 
	 * @param context
	 *            the context
	 * @return true means to keep the selenium session if there is an error;
	 *         false means to close the session anyway.
	 */
	public static boolean isKeepSessionOnError(Context context) {
		Boolean yes = (Boolean) context.getInternalObject(PrivateKeys.KEEP_SESSION_ON_ERROR, true);
		return yes != null && yes;
	}

	/**
	 * Set how selenium should behave at the end of the execution.
	 * 
	 * @param context
	 *            the context
	 * @param flag
	 *            true to keep the selenium session if there is an error. false
	 *            to close the session anyway.
	 */
	public static void setKeepSessionOnError(Context context, boolean flag) {
		context.setInternalObject(PrivateKeys.KEEP_SESSION_ON_ERROR, flag, true);
	}

	private static void noPropertyGivenException(String propertyName) {
		throw new PaxmlRuntimeException("No " + propertyName + " property given!");
	}

	public static SeleniumUtils getSeleniumUtils(Context context) {
		SeleniumUtils utils = new SeleniumUtils();
		return utils.getUtilFunctions(context);
	}

	/**
	 * Switch the current context selenium to another one.
	 * 
	 * @param context
	 *            the context
	 * @param selenium
	 *            the target selenium. null means to get the 1st created
	 *            selenium in the context
	 * @return the target selenium
	 */
	public static XSelenium switchSelenium(Context context, XSelenium selenium) {
		if (selenium == null) {
			if (log.isDebugEnabled()) {
				log.debug("No target session given, finding the 1st active session.");
			}
			selenium = findFirstActiveSelenium(context);
			if (selenium == null) {
				throw new PaxmlRuntimeException("There is no active session found to switch to!");
			}
		} else if (selenium.isTerminated()) {
			throw new PaxmlRuntimeException("The target session is already closed, you cannot switch to it any more!");
		}
		XSelenium old = getSelenium(context);
		context.setInternalObject(PrivateKeys.SELENIUM, selenium, true);
		if (log.isInfoEnabled()) {
			log.info("Selenium session switched from " + old + " to: " + selenium);
		}
		return selenium;
	}

	/**
	 * Find the 1st non-terminated selenium session.
	 * 
	 * @param context
	 *            the context
	 * @return the found session or null if not found.
	 */
	public static XSelenium findFirstActiveSelenium(Context context) {
		List<XSelenium> seleniums = (List<XSelenium>) context.getInternalObject(PrivateKeys.SELENIUMS, true);
		if (seleniums == null || seleniums.isEmpty()) {
			return null;
		}
		for (XSelenium s : seleniums) {
			if (!s.isTerminated()) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Get a selenium object. If doesn't exit, create a new one.
	 * 
	 * @param context
	 *            the context
	 * @return the selenium object
	 * @throws RuntimeException
	 *             if selenium object is not yet initialized.
	 */
	public static XSelenium getSelenium(Context context) {
		return getSelenium(context, false, null);
	}

	/**
	 * Get a selenium object.
	 * 
	 * @param context
	 *            the context
	 * @param forceNew
	 *            force to create a new selenium session
	 * @param browser
	 *            the type of browser
	 * 
	 * @return the selenium object
	 * @throws RuntimeException
	 *             if selenium object is not yet initialized.
	 */
	public static XSelenium getSelenium(Context context, boolean forceNew, String browser) {

		List<XSelenium> seleniums = (List<XSelenium>) context.getInternalObject(PrivateKeys.SELENIUMS, true);
		if (seleniums == null) {
			seleniums = new ArrayList<XSelenium>(1);
			context.setInternalObject(PrivateKeys.SELENIUMS, seleniums, true);
		}

		XSelenium selenium = forceNew ? null : (XSelenium) context.getInternalObject(PrivateKeys.SELENIUM, true);

		if (selenium == null) {
			String url = getStartUrl(context);

			String host = getRcServerHost(context);

			Integer port = getRcServerPort(context);
			if (port == null) {
				port = 0;
			}
			if (StringUtils.isBlank(browser)) {
				browser = getBrowserIdentifier(context);
			}
			if (StringUtils.isBlank(browser)) {
				browser = DEFAULT_BROWSER;
			}
			selenium = new XSelenium(host, port, browser, url);
			selenium.start();

			context.setInternalObject(PrivateKeys.SELENIUM, selenium, true);

			seleniums.add(selenium);

			final String path = getSnapshotPath(context);
			selenium.setSnapshotsPath(path);
			if (path != null) {
				if (selenium.isSnapshotSupported()) {
					if (log.isInfoEnabled()) {
						log.info("Selenium snapshots will be taken upon any test errors, and save to this folder: " + path);
					}
				} else {
					if (log.isInfoEnabled()) {
						log.info("The current browser does not support taking snapshots: " + selenium.getBrowserStartCommand());
					}
				}
			} else {
				log.info("Selenium snapshots will NOT be taken upon test errors, because the snapshot storage folder is not given n this property: " + SELENIUM_SNAPSHOT_FOLDER);
			}
		}

		PaxmlListener listener = (PaxmlListener) context.getInternalObject(PrivateKeys.LISTENER, true);
		if (listener == null) {
			listener = new PaxmlListener(seleniums);
			context.setInternalObject(PrivateKeys.LISTENER, listener, true);
			context.getPaxmlExecutionListeners(true).add(listener);
			// add to jvm shutdown hook in case the java process is killed.
			Runtime.getRuntime().addShutdownHook(new Thread(listener));
			context.getPaxml().addPaxmlExecutionListener(listener);
		}

		return selenium;
	}

	private static String getSnapshotPath(Context context) {
		Object obj = context.getConst(SELENIUM_SNAPSHOT_FOLDER, true);
		if (obj == null) {
			return null;
		}
		String path = obj.toString().trim();
		return path.length() == 0 ? null : path;
	}

	/**
	 * Set the snapshot file into error context.
	 * 
	 * @param errorContext
	 *            the error context
	 * @param file
	 *            the file
	 */
	private static void setErrorSnapshot(Context errorContext, File file) {
		errorContext.setInternalObject(Keys.ERROR_SNAPSHOT, file, true);
	}

	/**
	 * Get the snapshot file from error context.
	 * 
	 * @param errorContext
	 *            the error context
	 * @return the file
	 */
	public static File getErrorSnapshot(Context errorContext) {
		return (File) errorContext.getInternalObject(Keys.ERROR_SNAPSHOT, true);
	}

}
