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

import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * The attachFile tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "attachFile")
public class AttachFileTag extends LocatorTag {
    
    private boolean content;

    public AttachFileTag() {
        super();
        setWaitTill(VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommandAfterWait(Context context) {
        final String locator = getLocator();

        Object value = getValue();

        final SeleniumUtils selenium = getSeleniumUtils(context);
        if (content) {
            return selenium.attachFileContent(locator, value.toString());
        } else {
            selenium.attachFile(locator, value.toString());
            return null;
        }

    }

    public boolean isContent() {
        return content;
    }

    public void setContent(boolean content) {
        this.content = content;
    }

}
