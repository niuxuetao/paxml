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
import org.paxml.core.PaxmlRuntimeException;

/**
 * The closeSession tag impl. It closes the current selenium session forcefully.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "switchSession")
public class SwitchSessionTag extends SeleniumTag {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object onCommand(Context context) {
		Object selenium = getValue();
		if (selenium != null && !(selenium instanceof SeleniumHelper)) {
			throw new PaxmlRuntimeException(
					"The value parameter of the tag should be either null or a selenium session which is the result of a previous <url> tag");
		}
		return switchSelenium(context, (SeleniumHelper) selenium);
	}
}
