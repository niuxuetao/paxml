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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.paxml.core.Context;

/**
 * The base impl of selectable tags.
 * 
 * @author Xuetao Niu
 * 
 */
public class SelectableTag extends WebDriverTag {
    protected static interface IElementHandler {
        Object handle(WebElement ele);
    }

    private boolean list = false;
    private boolean allowNotFound = false;

    private String selector;
    private By by;
    private String javascript;

    @Override
    protected Object onCommand(Context context) {
        throw new UnsupportedOperationException("Internal error: not implemented!");
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
        if (selector.startsWith("id:")) {
            by = By.id(selector.substring(3));
        } else if (selector.startsWith("xpath:")) {
            by = By.xpath(selector.substring(6));
        } else if (selector.startsWith("text:")) {
            by = By.xpath("//*[text()='" + selector.substring(5) + "']");
        } else if (selector.startsWith("css:")) {
            by = By.cssSelector(selector.substring(4));
        } else if (selector.startsWith("className:")) {
            by = By.className(selector.substring(10));
        } else if (selector.startsWith("name:")) {
            by = By.name(selector.substring(5));
        } else if (selector.startsWith("tag:")) {
            by = By.tagName(selector.substring(4));
        } else if (selector.startsWith("linkText:")) {
            by = By.linkText(selector.substring(9));
        } else if (selector.startsWith("partialLinkText:")) {
            by = By.partialLinkText(selector.substring(16));
        } else if (selector.startsWith("javascript:")) {
            setJavascriptSelector(selector.substring(3));
        } else {
            // make a jQuery call that returns array, requires jQuery 1.4+
            String js = "return $('" + selector + "').toArray()";
            setJavascriptSelector(js);
        }
    }

    private void setJavascriptSelector(String js) {
        by = null;
        javascript = js;
    }

    protected Object handleElements(IElementHandler handler) {
        List<Object> values = new ArrayList<Object>(0);
        for (WebElement ele : findElements(null)) {
            values.add(handler.handle(ele));
        }
        if (!list && values.size() == 1) {
            return values.get(0);
        }

        return values;
    }

    public WebElement findElement(Boolean canBeNull) {
        WebDriver session = getSession();
        WebElement ele = null;
        if (StringUtils.isNotBlank(javascript)) {
            Object obj = ((JavascriptExecutor) session).executeScript(javascript);
            if (obj instanceof WebElement) {
                ele = (WebElement) obj;
            } else if (obj instanceof List && ((List) obj).size() > 0) {
                ele = ((List<WebElement>) obj).get(0);
            } else if (ele != null) {
                throw new RuntimeException("Unknown result from javascript:\r\n" + javascript);
            }
        } else {
            ele = session.findElement(myBy());
        }
        if (ele == null && !((canBeNull == null && allowNotFound) || (canBeNull != null && canBeNull))) {
            throw new RuntimeException("Cannot find element with selector: " + selector);
        }
        return ele;
    }

    /**
     * 
     * @param canBeEmpty
     * @return never null
     */
    public List<WebElement> findElements(Boolean canBeEmpty) {
        WebDriver session = getSession();
        List<WebElement> list;
        if (StringUtils.isNotBlank(javascript)) {

            Object obj = ((JavascriptExecutor) session).executeScript(javascript);
            if (obj instanceof WebElement) {
                list = new ArrayList<WebElement>(1);
                list.add((WebElement) obj);

            } else if (obj instanceof List) {
                list = (List<WebElement>) obj;
            } else {
                throw new RuntimeException("Unknown result from javascript:\r\n" + javascript);
            }

        } else {
            list = session.findElements(myBy());
        }
        boolean allowEmpty = (canBeEmpty == null && allowNotFound) || (canBeEmpty != null && canBeEmpty);
        if (!allowEmpty && list.size() <= 0) {
            throw new RuntimeException("No elements found with selector: " + selector);
        }
        return list;
    }

    protected By myBy() {
        if (by == null && StringUtils.isBlank(javascript)) {
            throw new RuntimeException("No selector given!");
        }
        return by;
    }

    public boolean isAllowNotFound() {
        return allowNotFound;
    }

    public void setAllowNotFound(boolean allowNotFound) {
        this.allowNotFound = allowNotFound;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }
}
