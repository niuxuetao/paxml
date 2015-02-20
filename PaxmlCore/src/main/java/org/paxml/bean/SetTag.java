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
import org.paxml.table.IRow;

/**
 * Set tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "set")
public class SetTag extends BeanTag {
	
	private Object var;
	private String column;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInvoke(Context context) throws Exception {
		if (var instanceof IRow) {
			((IRow) var).setCellValue(column, getValue());
		} else {
			context.setConst(var.toString(), null, getValue(), false);
		}
		//System.err.println(context.dump());
		return var;
	}

	public Object getVar() {
		return var;
	}

	public void setVar(Object var) {
		this.var = var;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

}
