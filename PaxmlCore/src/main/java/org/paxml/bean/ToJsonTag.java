/**
 * This file is part of PaxmlCore.
 *
 * PaxmlCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlCore.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.bean;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.util.XmlUtils;

/**
 * toJson tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = ToJsonTag.TAG_NAME)
public class ToJsonTag extends BeanTag {
	
	/**
	 * The tag name.
	 */
	public static final String TAG_NAME = "toJson";

	@Override
	protected Object doInvoke(Context context) throws Exception {
		Object val = getValue();
		if (val == null) {
			return null;
		}
		
		return XmlUtils.toJson(val);
	}

}
