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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.paxml.file.IFile;
import org.paxml.table.AbstractTable;
import org.paxml.table.IColumn;
import org.paxml.table.IRow;
import org.paxml.table.ITable;
import org.paxml.table.ITableRange;
import org.paxml.table.ITableTransformer;
import org.paxml.table.TableRange;
import org.paxml.util.CachedIterator;

public class ExcelTable extends AbstractTable implements IFile {
	private static final int CACHE_SIZE = 50;
	private final ExcelFile file;
	private final Sheet sheet;
	private final boolean readonly;
	private final boolean compact;

	private final ConcurrentHashMap<Integer, ExcelColumn> cachedColumns = new ConcurrentHashMap<Integer, ExcelColumn>();
	private volatile int maxColumnIndex;

	public static ExcelTable forRead(Object file, String sheet, String range) {
		return new ExcelTable(file, sheet, range, true, true);
	}

	public static ExcelTable forReadWrite(Object file, String sheet, String range) {
		return new ExcelTable(file, sheet, range, false, false);
	}

	public ExcelTable(Object file, String sheet, String range, boolean readonly, boolean compact) {
		this(file instanceof ExcelFile ? (ExcelFile) file : new ExcelFile(file), sheet, range == null ? null : new ExcelRange(range, false), readonly, compact);
	}

	public ExcelTable(ExcelFile file, String sheet, ITableRange range, boolean readonly, boolean compact) {
		this(file, file.getSheet(sheet, true), range, readonly, compact);
	}

	protected ExcelTable(ExcelFile file, Sheet sheet, ITableRange range, boolean readonly, boolean compact) {
		super();
		this.sheet = sheet;
		this.file = file;
		this.readonly = readonly;
		this.compact = compact;
		setRange(range);
	}

	@Override
	public void close() {
		file.close();
	}

	@Override
	public void flush() {
		file.save();
	}

	@Override
	public IColumn addColumn(String name) {
		return getColumn(name);
	}

	@Override
	public IColumn addColumn(String name, int index) {
		return getColumn(name);
	}

	@Override
	public ExcelColumn getColumn(int index) {
		notifyMaxColumn(index);
		ExcelColumn col = cachedColumns.get(index);
		if (col == null) {
			col = new ExcelColumn(index);
			cachedColumns.putIfAbsent(index, col);
		}
		return col;
	}

	@Override
	protected Iterator<IRow> getAllRows() {

		if (compact) {
			Iterator it = sheet.rowIterator();

			return new AbstractIteratorDecorator(it) {

				@Override
				public Object next() {

					return new ExcelRow((Row) getIterator().next(), ExcelTable.this);

				}

			};
		} else {
			return new Iterator<IRow>() {

				private int index = 0;

				@Override
				public boolean hasNext() {
					return index <= sheet.getLastRowNum();
				}

				@Override
				public IRow next() {

					ExcelRow r = getRow(index);
					index++;
					return r;

				}

				@Override
				public void remove() {
					Row row = sheet.getRow(index);
					if (row != null) {
						sheet.removeRow(row);
					}
				}

			};
		}
	}

	@Override
	public String getResourceIdentifier() {
		return file.getFile().getAbsolutePath();
	}

	public ExcelFile getFile() {
		return file;
	}

	public Sheet getSheet() {
		return sheet;
	}

	@Override
	public String getName() {
		return sheet.getSheetName();
	}

	@Override
	public List<IColumn> getColumns() {
		List<IColumn> cols = new ArrayList<IColumn>(maxColumnIndex);
		for (int i = 0; i <= maxColumnIndex; i++) {
			cols.add(getColumn(i));
		}
		return cols;
	}

	@Override
	public Map<String, IColumn> getColumnsMap() {
		List<IColumn> cols = getColumns();
		Map<String, IColumn> map = new HashMap<String, IColumn>(cols.size());
		for (IColumn col : cols) {
			map.put(col.getName(), col);
		}
		return map;
	}

	void notifyMaxColumn(int col) {
		maxColumnIndex = Math.max(maxColumnIndex, col);
	}

	@Override
	public IColumn getColumn(String name) {
		return getColumn(ExcelColumn.getColumnIndex(name));
	}

	@Override
	public List<String> getColumnNames() {
		List<String> names = new ArrayList<String>(maxColumnIndex);
		for (int i = 0; i <= maxColumnIndex; i++) {
			names.add(getColumn(i).getName());
		}
		return names;
	}

	@Override
	public IRow createNextRow(Object... cellValues) {
		ExcelRow row = getRow(getCurrentRowIndex() + 1);
		for (int i = 0; i < cellValues.length; i++) {
			row.setCellValue(i, cellValues[i]);
		}
		return row;
	}

	@Override
	public ITable getPart(ITableRange range, ITableTransformer tran) {
		ExcelTable t;
		if (getRange() == null || !range.isRelative()) {

			t = new ExcelTable(file, sheet, range, readonly, compact);

		} else {

			TableRange r = new TableRange(false);
			r.setRelative(false);
			r.setFirstRow(range.getFirstRow() + getRange().getFirstRow());
			r.setFirstColumn(range.getFirstColumn() + getRange().getFirstColumn());
			r.setFirstRow(range.getLastRow() + getRange().getFirstRow());
			r.setFirstRow(range.getLastColumn() + getRange().getLastColumn());
			r.correctValues();

			t = new ExcelTable(file, sheet, r, readonly, compact);
		}
		t.setReadTransformer(tran);
		return t;

	}

	@Override
	public void setPart(ITableRange range, ITable source, boolean insert, final ITableTransformer tran) {
		assertWritable();
		if (range == null) {
			range = new TableRange();
		}

		final int c = source.getRowCount();
		if (c == 0) {
			return;
		}
		final boolean shift = insert && range.getLastRow() <= sheet.getLastRowNum();

		Iterator<IRow> sit = source.getRows();
		if (c < 0) {
			// c<0, unknown source row count, shift once per batch if needed;

			final int batchSize = Math.min(range.getRowCount(), CACHE_SIZE);
			int index = range.getFirstRow();
			int read = 0;
			for (CachedIterator<IRow> it = new CachedIterator<IRow>(batchSize, sit); index < range.getLastRow() && it.hasNext();) {
				List<IRow> batch = it.next();
				if (shift) {
					sheet.shiftRows(index, sheet.getLastRowNum(), batch.size());
				}
				for (int i = 0; i < batch.size(); i++) {
					ExcelRow r = getRow(i + index);
					IRow sr = batch.get(i);
					setCellValues(r, range.getFirstColumn(), range.getLastColumn(), sr, tran);
				}
				read += batch.size();
				index += batch.size();
				it.setCacheSize(Math.min(CACHE_SIZE, range.getRowCount() - read));
			}
		} else {
			// shift only needed
			if (shift) {
				sheet.shiftRows(range.getFirstRow(), sheet.getLastRowNum(), Math.min(c, range.getRowCount()));
			}

			for (int i = range.getFirstRow(); i <= range.getLastRow() && sit.hasNext(); i++) {
				ExcelRow r = getRow(i);
				IRow sr = sit.next();
				setCellValues(r, range.getFirstColumn(), range.getLastColumn(), sr, tran);
			}
		}

	}

	@Override
	public int getRowCount() {
		if (compact) {
			return sheet.getPhysicalNumberOfRows();
		} else {
			return sheet.getLastRowNum() + 1;
		}
	}

	public boolean isCompact() {
		return compact;
	}

	public ExcelRow getRow(int index) {
		Row row = sheet.getRow(index);

		if (row == null) {
			return new ExcelRow(index, this);
		} else {
			return new ExcelRow(row, this);
		}

	}

}
