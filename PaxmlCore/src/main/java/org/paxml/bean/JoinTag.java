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
import org.paxml.util.ReflectUtils;

/**
 * Join tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "join")
public class JoinTag extends BeanTag {

	private String by = "";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInvoke(Context context) {
		Object value = getValue();
		if (value == null) {
			value = "";
		}
		final StringBuilder sb = new StringBuilder();
		
		ReflectUtils.traverseObject(value, new ReflectUtils.TraverseObjectCallback() {
			private boolean first=true;
			@Override
			public boolean onElement(Object ele) {
				
				if(first){
					first=false;
				}else{
					sb.append(by);
				}				
				sb.append(ele);
				return true;
			}
		});
		return sb.toString();
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

}
