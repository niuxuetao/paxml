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

import org.paxml.tag.DefaultTagLibrary;

/**
 * Selenium tag library.
 * 
 * @author Xuetao Niu
 * 
 */
public final class TagLibrary extends DefaultTagLibrary {
    /**
     * The singleton instance.
     */
    public static final TagLibrary INSTANCE = new TagLibrary();

    private TagLibrary() {
        super();
        // register tags
        registerTag(AlertTag.class);
        registerTag(AssertDisplayedTag.class);
        registerTag(AssertElementTag.class);
        registerTag(AssertNotDisplayedTag.class);
        registerTag(AssertNotPresentTag.class);
        registerTag(AssertNotValueTag.class);
        registerTag(AssertPresentTag.class);
        registerTag(AssertValueTag.class);

        registerTag(ClickTag.class);
        registerTag(CloseSessionTag.class);
        registerTag(ElementTag.class);
        registerTag(FrameTag.class);
        registerTag(IsDisplayedTag.class);
        registerTag(IsEnabledTag.class);
        registerTag(IsSelectedTag.class);
        registerTag(JavascriptTag.class);
        registerTag(NewSessionTag.class);

        registerTag(PickAttributeTag.class);
        registerTag(PickCssTag.class);
        registerTag(PickLocationTag.class);
        registerTag(PickSizeTag.class);
        registerTag(PickTagNameTag.class);
        registerTag(PickTextTag.class);
        registerTag(PickValueTag.class);

        registerTag(SelectTag.class);
        registerTag(TypeTag.class);
        registerTag(UrlTag.class);

        // register utils
        registerUtil(WebDriverUtils.class);
    }
}
