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
package org.paxml.selenium.webdriver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.paxml.core.IExecutionListener;
import org.paxml.launch.Paxml;

/**
 * The base impl of all WebDriver tags.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class WebDriverTag extends BeanTag {
    private static class WebDriverWrapper {
        private WebDriver webDriver;
        private boolean keepOnError = false;
        private int index;
    }

    private static final ThreadLocal<List<WebDriverWrapper>> WEB_DRIVERS = new ThreadLocal<List<WebDriverWrapper>>();
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
     * Default wait timeout.
     */
    public static final long DEFAULT_TIMEOUT = 120000;

    private static final Log log = LogFactory.getLog(WebDriverTag.class);

    private WebDriver session;

    /**
     * The execution listener.
     * 
     * @author Xuetao Niu
     * 
     */
    public static class SessionCleanListener implements IExecutionListener, Runnable {

        private volatile boolean done = false;

        /**
         * {@inheritDoc}
         */
        public void onEntry(Paxml paxml, Context context) {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public synchronized void run() {

            checkToClose(null);

        }

        private synchronized void checkToClose(Context context) {
            if (done) {
                return;
            }

            closeSessions(false);

            done = true;
        }

        /**
         * {@inheritDoc}
         */
        public void onExit(Paxml paxml, Context context) {

            checkToClose(context);

        }

    }

    /**
     * The private context keys.
     * 
     * @author Xuetao Niu
     * 
     */
    private static enum PrivateKeys {
        LISTENER
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

    public static void closeSessions(boolean force) {
        List<WebDriverWrapper> list = getWebDrivers();
        for (int i = list.size() - 1; i >= 0; i--) {
            WebDriverWrapper wp = list.get(i);
            if (force || !wp.keepOnError) {
                wp.webDriver.close();
            }
            list.remove(i);
        }
    }

    public static void unregisterSession(WebDriver wd, boolean forceClose) {
        if (wd == null) {
            throw new RuntimeException("WebDriver cannot be null!");
        }
        WebDriverWrapper wp = getSessionWrapper(wd);

        if (forceClose || !wp.keepOnError) {
            wd.close();
        }
        getWebDrivers().remove(wp.index);
    }

    private static WebDriverWrapper getSessionWrapper(WebDriver wd) {
        List<WebDriverWrapper> list = getWebDrivers();
        for (int i = 0; i < list.size(); i++) {
            WebDriverWrapper wp = list.get(i);
            if (wp.webDriver == wd) {
                wp.index = i;
                return wp;
            }
        }
        throw new RuntimeException("Leaked WebDriver: " + wd);
    }

    public static void registerSession(Context context, WebDriver ws, boolean keepOnError) {
        if (ws == null) {
            throw new RuntimeException("WebDriver cannot be null!");
        }

        SessionCleanListener listener = (SessionCleanListener) context.getInternalObject(PrivateKeys.LISTENER, true);
        if (listener == null) {
            listener = new SessionCleanListener();
            context.setInternalObject(PrivateKeys.LISTENER, listener, true);
            context.getPaxmlExecutionListeners(true).add(listener);
            // add to jvm shutdown hook in case the java process is killed.
            Runtime.getRuntime().addShutdownHook(new Thread(listener));
        }

        WebDriverWrapper wp = new WebDriverWrapper();
        wp.webDriver = ws;
        wp.keepOnError = keepOnError;
        getWebDrivers().add(wp);
    }

    private static List<WebDriverWrapper> getWebDrivers() {
        List<WebDriverWrapper> list = WEB_DRIVERS.get();
        if (list == null) {
            list = new ArrayList<WebDriverWrapper>();
            WEB_DRIVERS.set(list);
        }

        return list;
    }

    public WebDriver getSession() {
        return session;
    }

    public void setSession(WebDriver session) {
        this.session = session;
    }
}
