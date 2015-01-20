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
 * The select tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "select")
public class SelectTag extends LocatorTag {

    private String label;
    public SelectTag(){
        super();
        setWaitTill(VISIBLE);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object onCommandAfterWait(Context context) {
        if (this.label != null) {
            getSeleniumUtils(context).select(getLocator(), "label=" + getLabel().toString());
        } else {
            getSeleniumUtils(context).select(getLocator(), "value=" + getValue().toString());
        }
        return null;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
