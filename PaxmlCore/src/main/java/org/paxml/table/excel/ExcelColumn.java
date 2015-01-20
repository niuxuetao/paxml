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
package org.paxml.table.excel;

import org.apache.poi.ss.util.CellReference;
import org.paxml.table.TableColumn;

public class ExcelColumn extends TableColumn {

	public ExcelColumn(int index) {
		super(index, getColumnName(index));
	}

	public static String getColumnName(int index) {
		CellReference ref = new CellReference(-1, index);
		return ref.getCellRefParts()[1];
	}

	public static int getColumnIndex(String name) {
		CellReference ref = new CellReference(name);
		return ref.getCol();
	}
}
