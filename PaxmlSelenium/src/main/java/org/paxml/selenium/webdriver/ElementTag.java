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

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The element tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "element")
public class ElementTag extends SelectableTag {
    private long timeout = DEFAULT_TIMEOUT;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        if (timeout <= 0) {
            return findElements(null);
        } else {
            long max = timeout < 1000 ? 1000 : timeout;
            List<WebElement> list = new WebDriverWait(getSession(), Math.round(max / 1000.0))
                    .until(new ExpectedCondition<List<WebElement>>() {

                        public List<WebElement> apply(WebDriver wd) {
                            List<WebElement> list = findElements(true);
                            if (list.size() <= 0) {
                                return null;
                            }
                            return list;
                        }

                    });

            return list;
        }
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
