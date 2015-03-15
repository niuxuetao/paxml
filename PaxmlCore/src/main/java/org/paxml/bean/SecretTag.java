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

/**
 * Secret tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "secret")
public class SecretTag extends BeanTag {
	private String name;
	
	@Override
	protected Object doInvoke(Context context) throws Exception {
		Object clearValue = getValue();
		if (clearValue == null) {
			return context.getSecret(name);
		} else {
			context.setSecret(name, clearValue.toString());
			return clearValue;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
