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

public abstract class AbstractCell<T extends IRow> implements ICell{
	
	private T row;
	
	@Override
	public T getRow() {
		return row;
	}

	public void setRow(T row) {
		this.row = row;
	}

	@Override
	public IColumn getColumn() {
		return getRow().getTable().getColumn(getIndex());
	}
	
	@Override
	public String toString(){
		return String.valueOf(getValue());
	}
	
	@Override
	public Object getValue() {
		return getRow().getCellValue(getIndex());
	}

	@Override
	public void setValue(Object obj) {
		getRow().setCellValue(getIndex(), obj);
	}
	
}
