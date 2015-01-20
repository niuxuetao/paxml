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
        registerTag(ClickTag.class);
        registerTag(ClickWaitTag.class);
        registerTag(PickTag.class);
        registerTag(TypeTag.class);
        registerTag(UrlTag.class);
        registerTag(WaitTillLoadedTag.class);
        registerTag(WaitForTag.class);
        registerTag(WaitForAjaxTag.class);
        registerTag(SelectTag.class);
        registerTag(JavascriptTag.class);
        registerTag(SnapshotTag.class);
        registerTag(CloseSessionTag.class);
        registerTag(SwitchSessionTag.class);

        registerTag(AssertValueTag.class);
        registerTag(AssertTextTag.class);
        registerTag(AssertVisibleTag.class);
        registerTag(AssertNotVisibleTag.class);
        registerTag(AssertVisibilityTag.class);
        registerTag(AssertPresentTag.class);
        registerTag(AssertNotPresentTag.class);
        registerTag(AssertPresenceTag.class);

        registerTag(AttachFileTag.class);
        registerTag(SelectFrameTag.class);
        
        registerUtil(SeleniumUtils.class);
    }
}
