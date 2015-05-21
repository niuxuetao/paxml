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

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.KeyStroke;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.paxml.core.Context;
import org.paxml.core.Context.Stack.IStackTraverser;
import org.paxml.core.IEntity;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.ITag;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Add extensions support for Selenium.
 * 
 * @author Xuetao Niu
 */
public class SeleniumHelper {
	private final DefaultSelenium selenium;
	private volatile SeleniumServer seleniumServer;

	/**
	 * Different browser start command supported by Selenium RC Server.
	 */
	private enum BrowserStrings {
		IEXPLORE {
			/**
			 * {@inheritDoc}
			 */

			public String toString() {
				return "*iexplore";
			}
		},
		FIREFOX {
			/**
			 * {@inheritDoc}
			 */

			public String toString() {
				return "*firefox";
			}
		},
		CHROME {
			/**
			 * {@inheritDoc}
			 */

			public String toString() {
				return "*chrome";
			}
		},
		GOOGLECHROME {
			/**
			 * {@inheritDoc}
			 */

			public String toString() {
				return "*googlechrome";
			}
		}
	}

	private static enum Keys {
		SNAPSHOTS
	}

	public static class CallStack {
		private IEntity entity;
		private ITag tag;

		public IEntity getEntity() {
			return entity;
		}

		public void setEntity(IEntity entity) {
			this.entity = entity;
		}

		public ITag getTag() {
			return tag;
		}

		public void setTag(ITag tag) {
			this.tag = tag;
		}

	}

	public static class SnapshotInfo {
		private File file;
		private List<CallStack> callStack;

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public List<CallStack> getCallStack() {
			if (callStack == null) {
				callStack = new ArrayList<CallStack>();
			}
			return callStack;
		}

		public void setCallStack(List<CallStack> callStack) {
			this.callStack = callStack;
		}

	}

	/**
	 * Warning keyword.
	 */
	public static final String WARNING = "WARNING ";
	/**
	 * Error keyword.
	 */
	public static final String ERROR = "ERROR ";
	private static final Set<String> SCREENSHOTS_CAPABLE_BROWSERS;

	static {
		Set<String> tmp = new HashSet<String>();
		tmp.add(BrowserStrings.FIREFOX.toString());
		tmp.add(BrowserStrings.CHROME.toString());
		SCREENSHOTS_CAPABLE_BROWSERS = Collections.unmodifiableSet(tmp);
	}

	public static final long WAIT_FOR_AJAX_START_TIMEOUT = 1000;

	private static final Log log = LogFactory.getLog(SeleniumHelper.class);

	/**
	 * Used by the framework for translating characters into key codes as used
	 * by {@link java.awt.event.KeyEvent}.
	 */
	private static final Map<String, String> KEY_CODES_MAP;
	/**
	 * Used by the framework for translating characters into key codes as used
	 * by {@link java.awt.event.KeyEvent}.
	 */
	private static final Map<String, Integer> CHAR_TO_KEY_CODE_MAP;
	static {
		Map<String, String> tmp = new HashMap<String, String>();
		tmp.put("!", "1");
		tmp.put("@", "2");
		tmp.put("#", "3");
		tmp.put("$", "4");
		tmp.put("%", "5");
		tmp.put("^", "6");
		tmp.put("&", "7");
		tmp.put("*", "8");
		tmp.put("(", "9");
		tmp.put(")", "10");
		tmp.put("+", "=");
		tmp.put("_", "-");
		tmp.put(":", ";");
		tmp.put("\"", "'");
		tmp.put("|", "\\");
		tmp.put("?", "/");
		KEY_CODES_MAP = Collections.unmodifiableMap(tmp);

		Map<String, Integer> tmp2 = new HashMap<String, Integer>();
		try {
			tmp2.put("-", KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0).getKeyCode());
			tmp2.put("=", KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0).getKeyCode());
			tmp2.put(";", KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, 0).getKeyCode());
			tmp2.put(",", KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, 0).getKeyCode());
			tmp2.put(".", KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0).getKeyCode());
			tmp2.put(" ", KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0).getKeyCode());
			tmp2.put("'", KeyStroke.getKeyStroke(KeyEvent.VK_QUOTE, 0).getKeyCode());
			tmp2.put("\\", KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SLASH, 0).getKeyCode());
			tmp2.put("/", KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0).getKeyCode());
		} catch (Throwable ex) {
			log.fatal(ex);
		}
		CHAR_TO_KEY_CODE_MAP = Collections.unmodifiableMap(tmp2);
	}

	private boolean ajaxAware = true;
	private long waitForTimeout = 5000;
	private long waitForReloadTimeout = 60000;
	private long waitForCheckInterval = 1000;
	private long ajaxTimeout = 60000;
	private String browserStartCommand = "";
	private boolean errorClosing;

	private boolean initialized;
	private boolean terminated;
	private String snapshotsPath;

	/**
	 * Ceate Selenium instance.
	 * 
	 * @param serverHost
	 *            selenium host
	 * @param serverPort
	 *            selenium port
	 * @param browserStartCommand
	 *            which browser to use
	 * @param browserURL
	 *            the url
	 */
	public SeleniumHelper(String serverHost, int serverPort, String browserStartCommand, String browserURL) {

		if (StringUtils.isBlank(serverHost)) {
			// start the selenium server programmatically

			synchronized (SeleniumHelper.class) {
				if (seleniumServer == null) {
					seleniumServer = startServer(serverPort);
				}
				serverPort = seleniumServer.getPort();
			}

		}
		if (StringUtils.isBlank(serverHost)) {
			serverHost = "localhost";
		}
		selenium = new DefaultSelenium(serverHost, serverPort, browserStartCommand, browserURL);

		this.browserStartCommand = browserStartCommand;
	}

	private static int getAvailablePort() {
		ServerSocket s = null;

		try {
			s = new ServerSocket(0);
			return s.getLocalPort();
		} catch (IOException e) {
			throw new PaxmlRuntimeException(e);
		} finally {
			try {
				if (s != null) {
					s.close();
				}
			} catch (Exception e) {
				log.warn("Cannot close server socket of port: " + s.getLocalPort(), e);
			}
		}

	}

	public static SeleniumServer startServer(int port) {

		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		if (port <= 0) {
			port = getAvailablePort(); // RemoteControlConfiguration.DEFAULT_PORT;
		}
		rcc.setPort(port);
		SeleniumServer server;
		try {
			server = new SeleniumServer(false, rcc);
		} catch (Exception e1) {
			throw new PaxmlRuntimeException("Cannot create embedded selenium server", e1);
		}

		try {
			server.start();
			log.info("Embedded Selenium server started at port " + port);
		} catch (Exception e) {

			throw new PaxmlRuntimeException("Cannot start embedded selenium server at port: " + port, e);
		}
		return server;

	}

	public String getBrowserStartCommand() {
		return browserStartCommand;
	}

	public void setSnapshotsPath(String snapshotsPath) {
		this.snapshotsPath = snapshotsPath;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isSnapshotSupported() {
		return SCREENSHOTS_CAPABLE_BROWSERS.contains(browserStartCommand);
	}

	/**
	 * Make screenshot of the AUT to png file.
	 * 
	 * @param event
	 *            the name of the event.
	 * @param remote
	 *            true to take remote snapshots, false local.
	 * @return the snapshot file, null if not taken
	 */
	public File takeSnapshot(Context context) {

		if (isSnapshotSupported()) {
			final String snapshotFolder = getSnapshotsPath();
			if (snapshotFolder == null) {
				if (log.isErrorEnabled()) {
					log.error("Snapshot folder not given in property: " + SeleniumTag.SELENIUM_SNAPSHOT_FOLDER);
				}
				return null;
			}
			final String entity = context.getProcessId() + "-" + context.getCurrentEntity().getResource().getName();
			final String fn = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS").format(new Date());
			File dir = new File(snapshotFolder);
			dir.mkdirs();
			File file = new File(dir, entity + "." + fn + ".png");
			for (int i = 1; file.exists(); i++) {
				file = new File(dir, entity + "." + fn + "." + i + ".png");
			}
			final String base64 = selenium.captureEntirePageScreenshotToString("");
			try {
				FileUtils.writeByteArrayToFile(file, Base64.decodeBase64(base64.getBytes()));
				addSnapshot(context, file);
			} catch (IOException e) {
				throw new PaxmlRuntimeException("Cannot save snapshot to file: " + file.getAbsolutePath(), e);
			}
			if (log.isInfoEnabled()) {
				log.info("Remote snapshot taken and saved to local file: " + file.getAbsolutePath());
			}

			return file;
		} else {

			if (log.isWarnEnabled()) {
				log.warn("Snapshot cannot be taken upon error" + " because the current browser does not support taking snapshots: " + getBrowserStartCommand());
			}

		}

		return null;
	}

	/**
	 * Should not be called by paxml.
	 * 
	 * @param url
	 *            the url
	 */
	void open(String url) {
		terminated = false;
		if (!initialized) {
			start();
		}
		selenium.open(url);
	}

	public boolean isTerminated() {
		return terminated;
	}

	public void terminate() {
		if (terminated) {
			return;
		}
		try {
			selenium.close();
		} catch (Exception e) {
			log.warn("Error closing selenium", e);
		} finally {
			try {
				selenium.stop();
			} catch (Exception e) {
				log.warn("Error stopping selenium", e);
			} finally {
				try {
					if (seleniumServer != null && seleniumServer.getServer().isStarted()) {
						seleniumServer.stop();
					}
				} catch (Exception e) {
					log.warn("Error stopping selenium server", e);
				}
			}
		}

		terminated = true;
		if (log.isInfoEnabled()) {
			log.info("Selenium session terminated: " + this);
		}
	}

	/**
	 * Should not be called by paxml.
	 */
	void close() {

		errorClosing = false;
		if (log.isDebugEnabled()) {
			log.debug("Attempt to close selenium");
		}
		if (initialized) {
			if (log.isDebugEnabled()) {
				log.debug("Closing selenium");
			}
			try {
				selenium.close();
			} catch (Exception e) {
				errorClosing = true;
				log.warn("Error closing selenium", e);
			}
		}
	}

	/**
	 * Should not be called by paxml.
	 */
	void stop() {
		if (errorClosing) {
			if (log.isWarnEnabled()) {
				log.warn("Skipping stopping selenium due to error in closing");
			}
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("Attempt to stop selenium");
		}
		if (initialized) {
			initialized = false;
			if (log.isDebugEnabled()) {
				log.debug("Stopping selenium");
			}
			try {
				selenium.stop();
			} catch (Exception e) {
				log.warn("Error stopping selenium", e);
			}
		}
	}

	public long getAjaxTimeout() {
		return ajaxTimeout;
	}

	public void setAjaxTimeout(long ajaxTimeout) {
		this.ajaxTimeout = ajaxTimeout;
	}

	public boolean isAjaxAware() {
		if (!ajaxAware) {
			if (log.isInfoEnabled()) {
				log.info("The current page is not ajax aware.");
			}
		}
		return ajaxAware;
	}

	public void setAjaxAware(boolean ajaxAware) {
		this.ajaxAware = ajaxAware;
	}

	/**
	 * Should not be called by paxml
	 */
	void start() {
		selenium.start();
		if (this.browserStartCommand.equals("*iexplore")) {
			log.debug("We are running explorer, lets use javascript-xpath");
			selenium.useXpathLibrary("javascript-xpath");
		}
		initialized = true;
	}

	static String[] getKeyCodes(char ch) {
		boolean shiftRequired = false;
		String key = String.valueOf(ch);
		String value = KEY_CODES_MAP.get(key);

		if (value != null) {
			shiftRequired = true;
			key = value;
		} else if (Character.isUpperCase(key.charAt(0))) {
			shiftRequired = true;
		} else {
			key = key.toUpperCase();
		}

		KeyStroke ks = KeyStroke.getKeyStroke("pressed " + key.toUpperCase());
		int keyCode = 0;
		if (ks != null && ks.getKeyCode() != 0) {
			keyCode = ks.getKeyCode();
		} else {
			keyCode = CHAR_TO_KEY_CODE_MAP.get(key);
		}

		return shiftRequired ? new String[] { String.valueOf(KeyEvent.VK_SHIFT), String.valueOf(keyCode) } : new String[] { String.valueOf(keyCode) };
	}

	public long getWaitForTimeout() {
		return waitForTimeout;
	}

	public void setWaitForTimeout(long waitForTimeout) {
		this.waitForTimeout = waitForTimeout >= 0 ? waitForTimeout : 0;
	}

	public long getWaitForCheckInterval() {
		return waitForCheckInterval;
	}

	public void setWaitForCheckInterval(long waitForCheckInterval) {
		this.waitForCheckInterval = waitForCheckInterval >= 0 ? waitForCheckInterval : 0;
	}

	public long getWaitForReloadTimeout() {
		return waitForReloadTimeout;
	}

	public void setWaitForReloadTimeout(long waitForReloadTimeout) {
		this.waitForReloadTimeout = waitForReloadTimeout;
	}

	/**
	 * Add a screenshot to a context.
	 * 
	 * @param file
	 * @return
	 */
	private static int addSnapshot(Context context, File file) {

		List<SnapshotInfo> list = (List<SnapshotInfo>) context.getInternalObject(Keys.SNAPSHOTS, true);
		if (list == null) {
			list = new ArrayList<SnapshotInfo>(1);
			context.setInternalObject(Keys.SNAPSHOTS, list, true);
		}
		final SnapshotInfo si = new SnapshotInfo();
		si.setFile(file);
		context.getStack().traverse(new IStackTraverser() {

			public boolean onItem(IEntity entity, ITag tag) {
				CallStack cs = new CallStack();
				cs.setEntity(entity);
				cs.setTag(tag);
				si.getCallStack().add(cs);
				return true;
			}
		});
		list.add(si);
		return list.size();
	}

	/**
	 * Get the snapshot from a context. Should not be invoked by paxml code.
	 * 
	 * @param context
	 *            the context
	 * @return the list of snapshots
	 */
	public static List<SnapshotInfo> getSnapshots(Context context) {

		if (context == null) {
			return new ArrayList<SnapshotInfo>(0);
		}
		List<SnapshotInfo> list = (List<SnapshotInfo>) context.getInternalObject(Keys.SNAPSHOTS, true);
		if (list == null) {
			list = new ArrayList<SnapshotInfo>(0);
		}
		return Collections.unmodifiableList(list);
	}

	public String getSnapshotsPath() {
		return snapshotsPath;
	}

	/**
	 * Get the actual default selenium. Should not be called by paxml
	 * 
	 * @return the default selenium
	 */
	DefaultSelenium getSelenium() {
		return selenium;
	}
}
