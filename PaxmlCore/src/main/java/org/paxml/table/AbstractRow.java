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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.paxml.util.RangedIterator;

public abstract class AbstractRow<T extends ITable> extends AbstractMap<String, Object> implements IRow {
	private T table;

	@Override
	public Object getCellValue(String name) {
		ICell cell = getCell(name);
		return cell == null ? null : cell.getValue();
	}

	@Override
	public Object getCellValue(int index) {
		ICell cell = getCell(index);
		return cell == null ? null : cell.getValue();
	}

	@Override
	public Object put(String key, Object value) {
		IColumn col = table.getColumn(key);
		if (col == null) {
			col = table.addColumn(key);
		}
		int index = col.getIndex();
		Object existing = getCellValue(index);
		setCellValue(index, value);
		return existing;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> set = new LinkedHashSet<Map.Entry<String, Object>>();

		for (final IColumn col : table.getColumns()) {
			set.add(new Map.Entry<String, Object>() {

				@Override
				public String getKey() {
					return col.getName();
				}

				@Override
				public Object getValue() {
					return getCellValue(col.getIndex());
				}

				@Override
				public Object setValue(Object value) {
					Object existing = getValue();
					setCellValue(col.getIndex(), value);
					return existing;
				}

			});
		}
		return set;
	}

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
		while ((to < 0 || now <= to) && values.hasNext()) {
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
	public List<CellDiff> compare(List<IColumn> myColumns, IRow against, List<IColumn> theirColumns, ICellComparator comp) {

		if (myColumns == null) {
			myColumns = getTable().getColumns();
		}
		if (theirColumns == null) {
			theirColumns = against.getTable().getColumns();
		}
		if (comp == null) {
			comp = new DefaultCellComparator();
		}

		List<CellDiff> diffs = new ArrayList<CellDiff>(0);

		final int overlappedCols = Math.min(myColumns.size(), theirColumns.size());

		for (int i = 0; i < overlappedCols; i++) {

			ICell left = getCell(i);
			ICell right = against.getCell(i);
			CellDiffType d = comp.compare(left, right);
			if (d != null) {
				CellDiff cd = new CellDiff();
				IColumn cLeft = myColumns.get(i);
				IColumn cRight = theirColumns.get(i);
				cd.setLeftColumnIndex(cLeft.getIndex());
				cd.setLeftColumnName(cLeft.getName());
				cd.setLeftValue(left == null ? null : left.getValue());
				cd.setRightColumnIndex(cRight.getIndex());
				cd.setRightColumnName(cRight.getName());
				cd.setRightValue(right == null ? null : right.getValue());
				cd.setType(d);
				diffs.add(cd);
			}

		}

		return diffs.isEmpty() ? null : diffs;
	}
}
