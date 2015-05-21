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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The url tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "url")
public class UrlTag extends SeleniumTag {
    private static final Log log = LogFactory.getLog(UrlTag.class);

    private long timeout = -1;
    private Boolean keepSessionOnError;
    private boolean newSession = false;
    private String browser;
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
    	SeleniumHelper selenium = getSelenium(context, newSession, browser);
        final long to = timeout >= 0 ? timeout : selenium.getWaitForReloadTimeout();
        String url = String.valueOf(getValue());
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        setStartUrl(context, url + "");
        if (keepSessionOnError != null) {
            setKeepSessionOnError(context, keepSessionOnError);
        }
        
        SeleniumUtils util = getSeleniumUtils(context);
        util.open(url, to);     
        return getSelenium(context);
    }

    public Boolean getKeepSessionOnError() {
        return keepSessionOnError;
    }

    public void setKeepSessionOnError(Boolean keepSessionOnError) {
        this.keepSessionOnError = keepSessionOnError;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

	public boolean isNewSession() {
		return newSession;
	}

	public void setNewSession(boolean newSession) {
		this.newSession = newSession;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

}
