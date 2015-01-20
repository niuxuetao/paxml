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

import org.openqa.selenium.Alert;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The alert tag.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "alert")
public class AlertTag extends WebDriverTag {
    private String click;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommand(Context context) {
        Alert alert = getSession().switchTo().alert();
        String text = alert.getText();

        Object value = getValue();
        if (value != null) {
            alert.sendKeys(value.toString());
        }

        if ("ok".equalsIgnoreCase(click)) {
            alert.accept();
        } else if ("close".equalsIgnoreCase(click)) {
            alert.dismiss();
        }

        return text;
    }

    public String getClick() {
        return click;
    }

    public void setClick(String click) {
        this.click = click;
    }

}
