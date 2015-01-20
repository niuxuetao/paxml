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
package org.paxml.table;

import java.util.Iterator;
import java.util.Map;

import org.paxml.util.RangedIterator;

public abstract class AbstractRow<T extends ITable> implements IRow {
	private T table;

	@Override
	public Iterator<ICell> iterator() {
		return getCells();
	}

	@Override
	public ICell getCell(String name) {
		return getCell(table.getColumn(name).getIndex());
	}

	@Override
	public void setCellValue(String name, Object value) {
		setCellValue(table.getColumn(name).getIndex(), value);
	}

	@Override
	public void setCellValues(Map<String, Object> values) {
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			setCellValue(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void setCellValues(int from, int to, Iterator<Object> values) {
		int now = from;
		while (now <= to && values.hasNext()) {
			Object v = values.next();
			setCellValue(now++, v);
		}
	}

	abstract protected Iterator<ICell> getAllCells();

	@Override
	public Iterator<ICell> getCells() {
		ITableRange range = getTable().getRange();
		if (range == null) {
			return getAllCells();
		} else {
			return new RangedIterator(range.getFirstColumn(), range.getLastColumn(), getAllCells());
		}
	}

	@Override
	public T getTable() {
		return table;
	}

	public void setTable(T table) {
		this.table = table;
	}

	@Override
	public IRowDiff compare(IRow against, ITableTransformer tran) {
		
		// TODO Auto-generated method stub
		return null;
	}

}
