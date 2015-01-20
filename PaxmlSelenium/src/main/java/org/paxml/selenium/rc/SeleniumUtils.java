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

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Util;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.IUtilFunctionsFactory;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Selenium utils.
 * 
 * @author Xuetao Niu
 * 
 */
@Util("selenium")
public class SeleniumUtils implements IUtilFunctionsFactory {
	private static final Log log = LogFactory.getLog(SeleniumUtils.class);
	private static final FileServer fileServer = new FileServer();

	private static final String WAIT_FOR_AJAX_START_JS = "typeof(window.jQuery)!='function' || window.jQuery.active!=0";
	private static final String WAIT_FOR_AJAX_STOP_JS = "typeof(window.jQuery)!='function' || window.jQuery.active==0";

	private DefaultSelenium selenium;
	private XSelenium as;

	public DefaultSelenium getSelenium(){
		return selenium;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public SeleniumUtils getUtilFunctions(Context context) {
		as = SeleniumTag.getSelenium(context);
		selenium = as.getSelenium();
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Class<?> getXpathUtilFunctions(Context context) {
		return null;
	}

	/**
	 * Set the selenium working speed.
	 * 
	 * @param speed
	 *            pause in ms between each operation
	 */
	public void setSpeed(long speed) {
		log.trace("Setting selenium speed to " + speed + " ms");
		selenium.setSpeed(speed + "");
	}

	/**
	 * Create a cookie.
	 * 
	 * @param nameValuePair
	 *            the cookie pair
	 * @param optionsString
	 *            the option string
	 */
	public void createCookie(String nameValuePair, String optionsString) {
		selenium.createCookie(nameValuePair, optionsString);
	}

	/**
	 * Get cookie by name.
	 * 
	 * @param name
	 *            the name
	 * @return the cookie value
	 */
	public String getCookieByName(String name) {
		return selenium.getCookieByName(name);
	}

	/**
	 * Wait till a condition is met.
	 * 
	 * @param script
	 *            the javascript as condition. Returning true means met,
	 *            otherwise not.
	 * @param timeout
	 *            the timeout in ms
	 * 
	 */
	public void waitForCondition(String script, long timeout) {
		waitForCondition(script, timeout, true);
	}

	/**
	 * Wait till a condition is met.
	 * 
	 * @param script
	 *            the javascript as condition. Returning true means met,
	 *            otherwise not.
	 * @param timeout
	 *            the timeout in ms
	 * @param hard
	 *            true to fail if condition not met upon timeout, false to
	 *            continue anyway
	 * @return true confition met, false not
	 */
	public boolean waitForCondition(String script, long timeout, boolean hard) {
		if (log.isDebugEnabled()) {
			log.debug("Wait max " + timeout + " ms for condition: " + script);
		}
		try {
			selenium.waitForCondition(script, timeout + "");
		} catch (Exception e) {
			if (hard) {
				throw new PaxmlRuntimeException("Wait for javascript condition failed, because: " + e.getMessage(), e);
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Wait for an element to be present.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param timeout
	 *            the timeout in ms
	 * @param checkInterval
	 *            the check interval in ms
	 * @param hard
	 *            true to fail if condition not met upon timeout, false to
	 *            continue anyway
	 * 
	 */
	public void waitTillPresent(String locator, long timeout, long checkInterval) {
		waitTillPresent(locator, timeout, checkInterval, true);
	}

	/**
	 * Wait for an element to be present.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param timeout
	 *            the timeout in ms
	 * @param checkInterval
	 *            the check interval in ms
	 * @param hard
	 *            true to fail if condition not met upon timeout, false to
	 *            continue anyway
	 * @return true the element is present, false not
	 */
	public boolean waitTillPresent(String locator, long timeout, long checkInterval, boolean hard) {
		if (timeout < 0) {
			timeout = as.getWaitForTimeout();
		}
		if (checkInterval < 0) {
			checkInterval = as.getWaitForCheckInterval();
		}

		final long pause = checkInterval < timeout ? checkInterval : timeout;
		final long max = System.currentTimeMillis() + timeout;
		if (log.isDebugEnabled()) {
			log.debug("Waiting max " + timeout + " ms (with check interval " + pause + " ms) for this element to appear: " + locator);
		}
		while (!isPresent(locator)) {

			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				throw new PaxmlRuntimeException(e);
			}
			if (isPresent(locator)) {
				return true;
			}
			if (System.currentTimeMillis() >= max) {
				if (hard) {
					throw new PaxmlRuntimeException("Timeout after " + timeout + " ms waiting for this element to be present: " + locator);
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Wait for element to be visible.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param timeout
	 *            the timeout in ms
	 * @param checkInterval
	 *            the check interval in ms
	 * 
	 * 
	 */
	public void waitTillVisible(String locator, long timeout, long checkInterval) {
		waitTillVisible(locator, timeout, checkInterval, true);
	}

	/**
	 * Wait for element to be visible.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param timeout
	 *            the timeout in ms
	 * @param checkInterval
	 *            the check interval in ms
	 * @param hard
	 *            true to fail if condition not met upon timeout, false to
	 *            continue anyway
	 * @return true visible, false not
	 */
	public boolean waitTillVisible(String locator, long timeout, long checkInterval, boolean hard) {
		if (timeout < 0) {
			timeout = as.getWaitForTimeout();
		}
		if (checkInterval < 0) {
			checkInterval = as.getWaitForCheckInterval();
		}

		final long pause = checkInterval < timeout ? checkInterval : timeout;
		final long max = System.currentTimeMillis() + timeout;
		if (log.isDebugEnabled()) {
			log.debug("Waiting max " + timeout + " ms (with check interval " + pause + " ms) for this element to be visible: " + locator);
		}
		while (!isVisible(locator)) {

			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				throw new PaxmlRuntimeException(e);
			}
			if (isVisible(locator)) {
				return true;
			}
			if (System.currentTimeMillis() >= max) {
				if (hard) {
					throw new PaxmlRuntimeException("Timeout after " + timeout + " ms waiting for this element to be visible: " + locator);
				} else {
					return false;
				}
			}
		}
		return true;

	}

	/**
	 * Type into element.
	 * 
	 * @param locator
	 *            the locator
	 * @param value
	 *            the value to type
	 */
	public void type(String locator, String value) {
		if (value == null) {
			return;
		}
		String tlocator = getLocator(locator);

		selenium.type(tlocator, value);
		// This is the trick. Ajax will fire when you will leave the field and
		// not during typing
		// That was the problem!
		fireEvent(tlocator, "blur");
		// waitForAjaxRequestsStop(getAjaxTimeout());

	}

	/**
	 * Fire an event on an element.
	 * 
	 * @param locator
	 *            the element locator
	 * @param event
	 *            the event name
	 */
	public void fireEvent(String locator, String event) {
		selenium.fireEvent(getLocator(locator), event);
	}

	/**
	 * Simulate key up event in an element.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param keySequence
	 *            the key sequence
	 */
	public void keyUp(String locator, String keySequence) {
		String tlocator = getLocator(locator);

		selenium.keyUp(tlocator, keySequence);
	}

	/**
	 * Simulate key down event in an element.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param keySequence
	 *            the key sequence
	 */
	public void keyDown(String locator, String keySequence) {
		String tlocator = getLocator(locator);

		selenium.keyDown(tlocator, keySequence);
	}

	/**
	 * Simulate key press event in an element.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @param keySequence
	 *            the key sequence
	 */
	public void keyPress(String locator, String keySequence) {
		String tlocator = getLocator(locator);

		selenium.keyPress(tlocator, keySequence);
	}

	/**
	 * Focus in an element.
	 * 
	 * @param locator
	 *            the locator to the element
	 * 
	 */
	public void focus(String locator) {
		String tlocator = getLocator(locator);
		selenium.focus(tlocator);
	}

	/**
	 * Wait for ajax requests to start.
	 * 
	 * @param timeout
	 *            the timeout in ms
	 * @param hard
	 *            true to fail if condition not met upon timeout, false to
	 *            continue anyway
	 * @return true started, false not
	 */
	public boolean waitForAjaxRequestsStart(long timeout, boolean hard) {

		if (as.isAjaxAware()) {

			if (timeout < 0) {
				timeout = as.getAjaxTimeout();
			}

			Object js = Context.getCurrentContext().getConst("ajaxStartedConditionJs", true);

			if (js == null) {
				js = WAIT_FOR_AJAX_START_JS;
			}

			final long beginTime = System.currentTimeMillis();

			if (!this.waitForCondition(js.toString(), timeout, hard)) {
				if (log.isDebugEnabled()) {
					log.debug("Timeout after " + timeout + " ms waiting for js: " + js);
				}
				return false;
			}

			if (log.isDebugEnabled()) {
				log.debug("Ajax call took " + (System.currentTimeMillis() - beginTime) + " ms to start");
			}

		}
		return true;

	}

	/**
	 * Wait for ajax requests to stop.
	 * 
	 * @param timeout
	 *            the timeout in ms
	 * @param hard
	 *            true to fail if condition not met upon timeout, false to
	 *            continue anyway
	 * @return true stopped, false not
	 */
	public boolean waitForAjaxRequestsStop(long timeout, boolean hard) {
		if (as.isAjaxAware()) {
			if (timeout < 0) {
				timeout = as.getAjaxTimeout();
			}
			Object js = Context.getCurrentContext().getConst("ajaxStoppedConditionJs", true);
			if (js == null) {
				js = WAIT_FOR_AJAX_STOP_JS;
			}
			final long beginTime = System.currentTimeMillis();

			if (!this.waitForCondition(js.toString(), timeout, hard)) {
				if (log.isDebugEnabled()) {
					log.debug("Ajax did not stop after " + timeout + " ms");
				}
				return false;
			}

			if (log.isDebugEnabled()) {
				log.debug("Ajax call took " + (System.currentTimeMillis() - beginTime) + " ms to stop");

			}
			if (log.isDebugEnabled()) {
				log.debug("Waiting extra 1 second after ajax stopped for the remaining javascript to finish");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		return true;
	}

	/**
	 * Get the value of an element.
	 * 
	 * @param locator
	 *            the locator
	 * @return the value
	 */
	public String getValue(String locator) {
		String tlocator = getLocator(locator);
		log.trace("Getting value from: " + tlocator);
		String ret = selenium.getValue(tlocator);
		log.trace("Got value: " + ret);
		return ret;
	}

	/**
	 * Get the text of an element.
	 * 
	 * @param locator
	 *            the locator
	 * @return the text
	 */
	public String getText(String locator) {
		String tlocator = getLocator(locator);
		log.trace("Getting text from: " + tlocator);
		String ret = selenium.getText(tlocator);
		log.trace("Got text: " + ret);
		return ret;
	}
	
	/**
	 * Check for element being present and visible.
	 * 
	 * @param locator
	 *            locator
	 * @param arguments
	 *            inserted into locator using
	 *            {@link String#format(String, Object...)}.
	 * @return true if element is present and visible
	 */
	public boolean isVisible(String locator) {
		String tlocator = getLocator(locator);

		final boolean b = isPresent(tlocator) && selenium.isVisible(tlocator);
		if (log.isDebugEnabled()) {
			log.debug("Visibility checked, this element is " + (b ? "" : "NOT ") + "visible: " + tlocator);
		}
		return b;

	}

	/**
	 * Check if an element is in the dom tree.
	 * 
	 * @param locator
	 *            the locator to the element
	 * @return true exists, false not
	 */
	public boolean isPresent(String locator) {
		String tlocator = getLocator(locator);

		final boolean b = selenium.isElementPresent(tlocator);
		if (log.isDebugEnabled()) {
			log.debug("Presence checked, this element is " + (b ? "" : "NOT ") + "present: " + locator);
		}
		return b;
	}

	/**
	 * Navigate to a page.
	 * 
	 * @param url
	 *            the url to that page
	 * @param timeout
	 *            timeout in milliseconds to wait for the complete load
	 */
	public void open(String url, long timeout) {
		if (timeout < 0) {
			timeout = as.getWaitForReloadTimeout();
		}
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}
		if (log.isInfoEnabled()) {
			log.info("Opening url with timeout " + timeout + " ms : " + url);
		}
		selenium.open(url);
		// any selenium call will change the 'pageFreshlyLoaded' flag to false.
		waitForPageToLoad(timeout);
	}

	/**
	 * Check if an element is editable.
	 * 
	 * @param locator
	 *            the locator
	 * @return true editable, false not
	 */
	public boolean isEditable(String locator) {
		return selenium.isEditable(getLocator(locator));
	}

	/**
	 * pure convenience method introduced by Bert.
	 * 
	 */
	public static String getLocator(String locator) {
		if (locator == null) {
			return null;
		}
		if (locator.startsWith("jq=")) {
			// compose a js expression using jquery
			return "dom=var jq=window.jQuery(\"" + locator.substring(3) + "\"); return jq.size()==1?jq.get(0):(jq.size() ==0 ? null: jq.toArray());";
		} else if (locator.startsWith("text=")) {
			// compose a js expression using jquery
			return "css=*:contains(\"^" + locator.substring(5) + "$\")";
		} else {
			return locator;
		}
	}

	/**
	 * Generates a partial xpath expression that matches an element whose
	 * 'attribute' attribute contains the given value. So to match &lt;div
	 * class='foo bar'&gt; you would say "//div[" + containingClass("class",
	 * "foo") + "]".
	 * 
	 * @param attribute
	 *            The attribute.
	 * @param value
	 *            The value.
	 * @return XPath fragment
	 */
	public String containingAttribute(String attribute, String value) {
		return "contains(concat(' ',normalize-space(@" + attribute + "),' '),' " + value + " ')";
	}

	/**
	 * Return the content of the tool tip. If any of the xpath characters being
	 * used, the locator will be passed as is. Otherwise, short notation will be
	 * replaced with long one (xpath)
	 * 
	 * @param locator
	 *            locator
	 * @return text or null if not present
	 */
	public String getToolTip(String locator) {

		String fullLocator = locator;
		if (locator.indexOf("/") != -1 || locator.indexOf("[") != -1 || locator.indexOf("@") != -1 || locator.indexOf("[") != -1 || locator.indexOf("]") != -1
				|| locator.indexOf("'") != -1) {
			fullLocator = "//div[@id='" + locator + "']/div/div[@class='error_tooltip_right']";
		}
		if (isPresent(fullLocator)) {
			String ret = getText(fullLocator);
			return ret == null ? "" : ret.trim();
		} else {
			return null;
		}
	}

	/**
	 * Figures out the right sequence of native keycodes that are required to
	 * type in character {@code ch} and presses the required keys.
	 * 
	 * @param ch
	 *            the character that needs to be typed in
	 */
	public void keyPressNative(char ch) {
		String[] codes = XSelenium.getKeyCodes(ch);
		if (codes.length == 1) {
			selenium.keyDownNative(codes[0]);
			selenium.keyUpNative(codes[0]);
		} else {
			selenium.keyDownNative(codes[0]);
			selenium.keyDownNative(codes[1]);
			selenium.keyUpNative(codes[1]);
			selenium.keyUpNative(codes[0]);
		}
	}

	void settlePageJs(long ajaxTimeout) {
		if (log.isDebugEnabled()) {
			log.debug("Waiting 1 second after page reload for the non-ajax javascript to finish");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// do nothing
		}

		if (waitForAjaxRequestsStart(XSelenium.WAIT_FOR_AJAX_START_TIMEOUT, false)) {
			waitForAjaxRequestsStop(ajaxTimeout, false);
		}

	}

	/**
	 * Wait for the page to load.
	 * 
	 * @param timeout
	 *            the timeout in ms
	 */
	public void waitForPageToLoad(long timeout) {
		if (timeout < 0) {
			timeout = as.getWaitForReloadTimeout();
		}
		if (log.isInfoEnabled()) {
			log.info("Waiting for page to reload, timeout: " + timeout + " ms");
		}
		final long start = System.currentTimeMillis();
		selenium.waitForPageToLoad(timeout + "");
		if (log.isDebugEnabled()) {
			log.debug("Page took " + (System.currentTimeMillis() - start) + " ms to load");
		}
		settlePageJs(timeout);
	}

	/**
	 * Select an option.
	 * 
	 * @param selectLocator
	 *            the locator to the select box
	 * @param optionLocator
	 *            the locator to the option
	 */
	public void select(String selectLocator, String optionLocator) {
		String slocator = getLocator(selectLocator);
		String olocator = getLocator(optionLocator);
		log.debug("SELECT: " + slocator + " = " + olocator);
		selenium.select(slocator, olocator);
		fireEvent(slocator, "blur");
		fireEvent(slocator, "change");

	}

	/**
	 * Get the select options.
	 * 
	 * @param selectLocator
	 *            the locator
	 * @return array of options
	 */
	public String[] getSelectOptions(String selectLocator) {
		String slocator = getLocator(selectLocator);
		log.debug("getSelectOptions: " + slocator + " = " + slocator);
		return selenium.getSelectOptions(slocator);
	}

	/**
	 * Click on an element.
	 * 
	 * @param locator
	 *            the element locator
	 */
	public void click(String locator) {
		String tlocator = getLocator(locator);
		if (log.isDebugEnabled()) {
			log.debug("Clicking: " + tlocator);
		}
		selenium.click(tlocator);

	}

	/**
	 * Get the confirmation.
	 * 
	 * @return the confirmation
	 */
	public String getConfirmation() {
		return selenium.getConfirmation();
	}

	/**
	 * Attach a string as file content on web page. This will automatically host
	 * the given content a logical file.
	 * 
	 * @param locator
	 *            the locator to the file input
	 * @param content
	 *            the content of the file to attach
	 * @return the url of the hosted content
	 */
	public String attachFileContent(String locator, String content) {
		if (content == null) {
			throw new PaxmlRuntimeException("No content specified for attaching");
		}
		String url = fileServer.hostIt(content, true);
		String loc = getLocator(locator);
		if (log.isInfoEnabled()) {
			log.info("Attaching file content to element, locator=" + loc + ", url=" + url);
		}
		selenium.attachFile(loc, url);
		return url;
	}

	/**
	 * Attach a file to file input on web page.
	 * 
	 * @param locator
	 *            the locator to the file input
	 * @param file
	 *            If it starts with http://, it will attach a file downloaded
	 *            from a remote server. If it does not start with http://, it
	 *            means a file found on classpath.
	 * 
	 * @return the url of given file, or the published url of the file.
	 */
	public String attachFile(String locator, String file) {
		if (StringUtils.isBlank(file)) {
			throw new PaxmlRuntimeException("No file specified for attaching");
		}

		String url = fileServer.hostIt(file, false);
		String loc = getLocator(locator);
		if (log.isInfoEnabled()) {
			log.info("Attaching file to element, locator=" + loc + ", url=" + url);
		}
		selenium.attachFile(loc, url);

		return file;
	}

	/**
	 * Switch to a different frame.
	 * 
	 * @param locator
	 *            the locator to the target frame
	 * @param wait
	 *            true to wait for the frame to be fully loaded, false not to
	 *            wait
	 * @param timeout
	 *            the timeout in ms when wait is true
	 * 
	 */
	public void selectFrame(String locator, boolean wait, long timeout) {
		locator = getLocator(locator);
		if (wait) {
			selenium.waitForFrameToLoad(locator, timeout + "");
		}
		if (log.isDebugEnabled()) {
			log.debug("Selecting frame: " + locator);
		}
		selenium.selectFrame(locator);
		if (wait) {
			settlePageJs(timeout);
		}

	}

	/**
	 * Evaluate javascript.
	 * 
	 * @return the evaluation result
	 */
	public Object eval(String js) {
		if (log.isDebugEnabled()) {
			log.debug("Evaluating javascript: " + js);
		}
		return selenium.getEval(js);
	}

	/**
	 * Open a new browser window.
	 * 
	 * @param url
	 *            the url of the new window
	 * @param windowId
	 *            the window id to assign to the new window, empty string means
	 *            to generate a unique window id.
	 * @return the windowId given, or the generated unique windowId.
	 */
	public String newWindow(String url, String windowId) {
		if (StringUtils.isEmpty(windowId)) {
			windowId = UUID.randomUUID().toString();
		}
		if (log.isInfoEnabled()) {
			log.info("Opening new window with window id: " + windowId);
		}
		selenium.openWindow(url, windowId);
		return windowId;
	}

	/**
	 * Switch to a different window.
	 * 
	 * @param windowId
	 *            the id of the window to switch to
	 */
	public void selectWindow(String windowId) {
		if (windowId == null || "null".equals(windowId)) {
			log.info("Selecting the main window");
			selenium.selectWindow(windowId);
		} else {
			if (log.isInfoEnabled()) {
				log.info("Selecting window: " + windowId);
			}
			selenium.selectWindow("name=" + windowId);
		}
	}

	/**
	 * Refresh the current page.
	 * 
	 * @param timeout
	 *            the timeout in ms, defaults to 60000 ms.
	 */
	public void refresh(long timeout) {
		if (timeout < 0) {
			timeout = as.getWaitForReloadTimeout();
		}
		if (log.isInfoEnabled()) {
			log.info("Refreshing the current page with timeout: " + timeout + " ms");
		}
		selenium.refresh();
		waitForPageToLoad(timeout);
	}

	/**
	 * Get the url of the current window.
	 * 
	 * @return the url string
	 */
	public String getCurrentUrl() {
		String url = String.valueOf(eval("window.location.href+''"));
		if (log.isDebugEnabled()) {
			log.debug("Current browser url: " + url);
		}
		return url;
	}
}
