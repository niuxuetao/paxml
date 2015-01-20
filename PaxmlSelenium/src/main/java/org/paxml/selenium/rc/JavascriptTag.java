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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The javascript tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "javascript")
public class JavascriptTag extends SeleniumTag {
    private static final Log log = LogFactory.getLog(JavascriptTag.class);

    @Override
    protected Object onCommand(Context context) {
        Object value = getValue();
        if (value == null) {
            return null;
        }
        final String js = value.toString();
        if (log.isDebugEnabled()) {
            log.debug("Evaluating javascript: " + js);
        }

        return getSelenium(context).getSelenium().getEval(js);
    }

}
