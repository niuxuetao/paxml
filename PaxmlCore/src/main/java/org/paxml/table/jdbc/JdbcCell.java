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
package org.paxml.table.jdbc;

import org.paxml.table.AbstractCell;

public class JdbcCell extends AbstractCell<JdbcRow> {
	private final int index;
	
	public JdbcCell(int index, JdbcRow row) {
		this.index = index;
		setRow(row);
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public Object getValue() {
		return getRow().values[index];
	}

	@Override
	public void setValue(Object obj) {

		getRow().getTable().assertWritable();

	}
}
