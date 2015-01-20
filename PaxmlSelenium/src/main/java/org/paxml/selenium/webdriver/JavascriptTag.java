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

import org.openqa.selenium.JavascriptExecutor;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The javascript tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "javascript")
public class JavascriptTag extends WebDriverTag {
    private boolean async = false;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        JavascriptExecutor exe = (JavascriptExecutor) getSession();
        if (async) {
            return exe.executeAsyncScript(getValue() + "");
        } else {
            return exe.executeScript(getValue() + "");
        }

    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

}
