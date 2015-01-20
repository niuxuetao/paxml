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
public class UrlTag extends WebDriverTag {
    private static final Log log = LogFactory.getLog(UrlTag.class);
    private long timeout;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        Object url = getValue();
        if (log.isInfoEnabled()) {
            log.info("Opening url: " + url);
        }
        getSession().get(url + "");

        // wait for the body element
        ElementTag eleTag = new ElementTag();
        eleTag.setSelector("xpath:/html/body");
        eleTag.setTimeout(timeout);
        eleTag.execute(context);

        return url;
    }

}
