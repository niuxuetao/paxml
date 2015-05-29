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

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * wait till loaded tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "newSession")
public class NewSessionTag extends WebDriverTag {
    private String hub;
    private Boolean proxyAutoDetect;
    private String proxyAutoConfigUrl;
    private String proxyHttp;
    private String proxyFtp;
    private String proxySsl;
    private String browser;
    private boolean keepOnError = false;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        WebDriver wd = createWebDriver(context);
        registerSession(context, wd, keepOnError);
        return wd;
    }

    private WebDriver createWebDriver(Context context) {
        Proxy proxy = new Proxy();
        if (StringUtils.isNotBlank(proxyHttp)) {
            proxy.setHttpProxy(proxyHttp);
        }
        if (StringUtils.isNotBlank(proxyFtp)) {
            proxy.setFtpProxy(proxyFtp);
        }
        if (StringUtils.isNotBlank(proxySsl)) {
            proxy.setSslProxy(proxySsl);
        }
        if (StringUtils.isNotBlank(proxyAutoConfigUrl)) {
            proxy.setProxyAutoconfigUrl(proxyAutoConfigUrl);
        }
        if (proxyAutoDetect != null) {
            proxy.setAutodetect(proxyAutoDetect);
        }

        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, proxy);
        cap.setJavascriptEnabled(true);

        if (StringUtils.isNotBlank(hub)) {
            final URL url;
            try {
                url = new URL(hub);
            } catch (MalformedURLException e) {
                throw new RuntimeException("hub url is invalid: " + hub, e);
            }
            if ("firefox".equalsIgnoreCase(browser)) {
                cap = cap.firefox();
            } else if ("iexplore".equalsIgnoreCase(browser)) {
                cap = cap.internetExplorer();
            } else if ("chrome".equalsIgnoreCase(browser)) {
                cap = cap.chrome();
            }
            return new RemoteWebDriver(url, cap);
        }

        if ("firefox".equalsIgnoreCase(browser)) {
            return new FirefoxDriver(cap);
        }
        if ("iexplore".equalsIgnoreCase(browser)) {
            return new InternetExplorerDriver(cap);
        }
        if ("chrome".equalsIgnoreCase(browser)) {
            return new ChromeDriver(cap);
        }

        throw new RuntimeException("unknown browser type: " + browser);

    }

    public Boolean getProxyAutoDetect() {
        return proxyAutoDetect;
    }

    public void setProxyAutoDetect(Boolean proxyAutoDetect) {
        this.proxyAutoDetect = proxyAutoDetect;
    }

    public String getProxyAutoConfigUrl() {
        return proxyAutoConfigUrl;
    }

    public void setProxyAutoConfigUrl(String proxyAutoConfigUrl) {
        this.proxyAutoConfigUrl = proxyAutoConfigUrl;
    }

    public String getProxyHttp() {
        return proxyHttp;
    }

    public void setProxyHttp(String proxyHttp) {
        this.proxyHttp = proxyHttp;
    }

    public String getProxyFtp() {
        return proxyFtp;
    }

    public void setProxyFtp(String proxyFtp) {
        this.proxyFtp = proxyFtp;
    }

    public String getProxySsl() {
        return proxySsl;
    }

    public void setProxySsl(String proxySsl) {
        this.proxySsl = proxySsl;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public boolean isKeepOnError() {
        return keepOnError;
    }

    public void setKeepOnError(boolean keepOnError) {
        this.keepOnError = keepOnError;
    }

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
    }

}
