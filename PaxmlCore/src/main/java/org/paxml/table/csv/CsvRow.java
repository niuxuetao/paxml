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
package org.paxml.table.csv;

import java.util.Iterator;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.paxml.table.AbstractRow;
import org.paxml.table.ICell;

public class CsvRow extends AbstractRow<CsvTable> {

	public CsvRow(CsvTable table) {
		setTable(table);
	}

	@Override
	public int getIndex() {
		return getTable().getCurrentRowIndex();
	}

	@Override
	public ICell getCell(int index) {
		return new CsvCell(this, index);
	}

	@Override
	public void setCellValue(int index, Object value) {
		getTable().writeCurrentRow(index, value == null ? null : value.toString());
	}

	@Override
	protected Iterator<ICell> getAllCells() {
		return new AbstractIteratorDecorator(getTable().getColumnCurrentRow().iterator()) {
			private int index = 0;

			@Override
			public Object next() {
				return new CsvCell(CsvRow.this, index++);
			}

		};
	}

}
