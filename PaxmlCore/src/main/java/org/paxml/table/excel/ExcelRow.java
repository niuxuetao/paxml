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

import java.util.Iterator;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.paxml.table.AbstractRow;
import org.paxml.table.ICell;

public class ExcelRow extends AbstractRow<ExcelTable> {
	private Row row;
	private final int index;

	ExcelRow(int index, ExcelTable table) {
		this.index = index;
		setTable(table);
	}

	ExcelRow(Row row, ExcelTable table) {
		this.row = row;
		this.index = -1;
		setTable(table);
	}

	@Override
	public int getIndex() {
		if (row == null) {
			return index;
		}
		return row.getRowNum();
	}

	@Override
	protected Iterator<ICell> getAllCells() {

		Iterator<ICell> it;
		if (getTable().isCompact()) {
			it = new AbstractIteratorDecorator(row.cellIterator()) {

				@Override
				public Object next() {
					Cell cell = (Cell) getIterator().next();
					return new ExcelCell(cell, ExcelRow.this);
				}

			};
		} else {
			it = new Iterator<ICell>() {
				private int i;

				@Override
				public boolean hasNext() {
					return i < row.getLastCellNum();
				}

				@Override
				public ICell next() {
					return getCell(i++);
				}

				@Override
				public void remove() {
					Cell c = row.getCell(i);
					if (c != null) {
						row.removeCell(c);
					}
				}

			};
		}
		return it;
	}

	@Override
	public ExcelCell getCell(int index) {
		getTable().notifyMaxColumn(row.getLastCellNum() - 1);
		
		Cell c = row.getCell(index);
		if (c == null) {
			return new ExcelCell(index - 1, this);
		} else {
			return new ExcelCell(c, this);
		}
	}

	@Override
	public void setCellValue(int index, Object obj) {
		ExcelCell dest = getCell(index);
		dest.setValue(obj);
	}

	public Row getExcelRow() {
		if (row == null) {
			row = getTable().getSheet().createRow(index);
		}
		getTable().notifyMaxColumn(row.getLastCellNum() - 1);
		return row;
	}

}
