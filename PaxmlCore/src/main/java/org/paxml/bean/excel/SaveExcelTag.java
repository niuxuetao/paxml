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
package org.paxml.bean.excel;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.table.excel.ExcelFile;

/**
 * Save excel tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "saveExcel")
public class SaveExcelTag extends AbstractExcelTag {
	private boolean close = true;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		ExcelFile ef = getFile();
		ef.save();
		if (close) {
			ef.close();

		}
		return null;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

}
