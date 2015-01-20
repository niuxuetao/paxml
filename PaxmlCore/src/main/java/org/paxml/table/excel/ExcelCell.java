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

import org.apache.poi.ss.usermodel.Cell;
import org.paxml.table.AbstractCell;
import org.paxml.table.ITableTransformer;

public class ExcelCell extends AbstractCell<ExcelRow> {

	private Cell cell;
	private final int index;

	ExcelCell(int index, ExcelRow row) {
		this.index = index;
		setRow(row);
	}

	ExcelCell(Cell cell, ExcelRow row) {

		this.cell = cell;
		this.index = cell.getColumnIndex();
		setRow(row);
	}

	@Override
	public Object getValue() {
		if (cell == null) {
			return null;
		}
		ExcelTable table = getRow().getTable();
		Object raw = table.getFile().getCellValue(cell);
		ITableTransformer tran = table.getReadTransformer();
		return tran == null ? raw : tran.transformValue(this);
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void setValue(Object obj) {
		getRow().getTable().assertWritable();
		if (cell == null) {
			if (obj == null) {
				return;
			}

			if (obj instanceof Number) {
				cell = getRow().getExcelRow().createCell(index, Cell.CELL_TYPE_NUMERIC);
				cell.setCellValue(((Number) obj).doubleValue());
			} else if (obj instanceof Boolean) {
				cell = getRow().getExcelRow().createCell(index, Cell.CELL_TYPE_BOOLEAN);
				cell.setCellValue(((Boolean) obj));
			} else {
				cell = getRow().getExcelRow().createCell(index, Cell.CELL_TYPE_STRING);
				cell.setCellValue(((String) obj));
			}

		} else {
			if (obj == null) {
				cell.setCellType(Cell.CELL_TYPE_BLANK);
			} else if (obj instanceof Number) {
				cell.setCellValue(((Number) obj).doubleValue());
			} else if (obj instanceof Boolean) {
				cell.setCellValue(((Boolean) obj));
			} else if (obj instanceof java.util.Date) {
				cell.setCellValue((java.util.Date) obj);
			} else if (obj instanceof java.util.Calendar) {
				cell.setCellValue((java.util.Calendar) obj);
			} else if (obj instanceof java.sql.Date) {
				cell.setCellValue(new java.util.Date(((java.sql.Date) obj).getTime()));
			} else {
				cell.setCellValue(((String) obj));
			}
		}
	}

}
