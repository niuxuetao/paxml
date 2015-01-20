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
import java.util.LinkedHashMap;
import java.util.Map;

import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.RangedIterator;

public abstract class AbstractTable implements ITable {
	private boolean readonly;
	private ITableRange range;
	private ITableTransformer readTransformer;

	@Override
	public Iterator<IRow> iterator() {
		return getRows();
	}

	@Override
	public ITableRange getRange() {
		return range;
	}

	protected void setRange(ITableRange range) {
		this.range = range;
	}

	@Override
	public IColumn getColumn(int index) {
		return getColumns().get(index);
	}

	@Override
	public boolean isInsertable() {
		return isReadonly();
	}

	@Override
	public boolean isUpdatable() {
		return isReadonly();
	}

	@Override
	public boolean isAppendable() {
		return isReadonly();
	}

	@Override
	public boolean isDeletable() {
		return isReadonly();
	}

	@Override
	public boolean isReadonly() {
		return readonly;
	}

	public ITableTransformer getReadTransformer() {
		return readTransformer;
	}

	public void setReadTransformer(ITableTransformer readTransformer) {
		this.readTransformer = readTransformer;
	}

	abstract protected String getResourceIdentifier();

	protected void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public void assertWritable() {
		if (readonly) {
			throw new PaxmlRuntimeException("Resource is readonly: " + getResourceIdentifier());
		}
	}

	abstract protected Iterator<IRow> getAllRows();

	@Override
	public Iterator<IRow> getRows() {
		ITableRange r = getRange();
		if (r == null) {
			return getAllRows();
		} else {
			return new RangedIterator<IRow>(r.getFirstRow(), r.getLastRow(), getAllRows());
		}
	}

	@Override
	public ITableDiff compare(ITable against, ITableTransformer tran) {

		Iterator<IRow> it1 = getRows();
		Iterator<IRow> it2 = against.getRows();
		int index = 0;
		while (it1.hasNext() && it2.hasNext()) {
			IRow row1 = it1.next();
			IRow row2 = it2.next();
			Map<String, Object> map1 = getTransformedCellValues(row1, tran);
			
			index++;
		}

		return null;
	}

	protected void setCellValues(IRow rowDest, int from, int to, IRow rowSrc, ITableTransformer tran) {

		Map<String, Object> map = getTransformedCellValues(rowSrc, tran);
		for (int j = from; j <= to; j++) {
			String col = getColumn(j).getName();
			Object val = map.get(col);
			rowDest.setCellValue(j, val);
		}
	}

	protected Map<String, Object> getTransformedCellValues(IRow rowSrc, ITableTransformer tran) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		for (Iterator<ICell> it = rowSrc.getCells(); it.hasNext();) {
			ICell cell = it.next();
			String newCol = tran == null ? cell.getColumn().getName() : tran.mapColumn(cell.getColumn());
			Object newVal = tran == null ? cell.getValue() : tran.transformValue(cell);
			result.put(newCol, newVal);
		}
		return result;
	}

}
