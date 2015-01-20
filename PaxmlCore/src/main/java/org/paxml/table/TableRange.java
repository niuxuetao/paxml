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

public class TableRange implements ITableRange {

	protected int firstRow;
	protected int lastRow;
	protected int firstColumn;
	protected int lastColumn;
	protected boolean relative;

	public TableRange() {
		this(true);
	}

	public TableRange(boolean correctValues) {
		super();
		if (correctValues) {
			correctValues();
		}
	}

	public TableRange(int firstRow, int lastRow, int firstColumn, int lastColumn, boolean relative) {

		this(false);

		this.relative = relative;
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstColumn = firstColumn;
		this.lastColumn = lastColumn;
		correctValues();
	}

	public final void correctValues() {
		if (firstRow < 0) {
			firstRow = 0;
		}
		if (lastRow < 0) {
			lastRow = Integer.MAX_VALUE;
		}

		if (firstColumn < 0) {
			firstColumn = 0;
		}
		if (lastColumn < 0) {
			lastColumn = Integer.MAX_VALUE;
		}

		if (firstRow > lastRow) {
			int tmp = lastRow;
			lastRow = firstRow;
			firstRow = tmp;
		}
		if (firstColumn > lastColumn) {
			int tmp = lastColumn;
			lastColumn = firstColumn;
			firstColumn = tmp;
		}

	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
		correctValues();
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
		correctValues();
	}

	public void setFirstColumn(int firstColumn) {
		this.firstColumn = firstColumn;
		correctValues();
	}

	public void setLastColumn(int lastColumn) {
		this.lastColumn = lastColumn;
		correctValues();
	}

	@Override
	public int getFirstRow() {
		return firstRow;
	}

	@Override
	public int getLastRow() {
		return lastRow;
	}

	@Override
	public int getFirstColumn() {
		return firstColumn;
	}

	@Override
	public int getLastColumn() {
		return lastColumn;
	}

	@Override
	public boolean isRelative() {
		return relative;
	}

	public void setRelative(boolean relative) {
		this.relative = relative;
	}

	@Override
	public int getRowCount() {
		return lastRow - firstRow;
	}

	@Override
	public int getColumnCount() {
		return lastColumn - firstColumn;
	}

}
