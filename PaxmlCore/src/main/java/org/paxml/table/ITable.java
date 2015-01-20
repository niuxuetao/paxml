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
import java.util.List;
import java.util.Map;

public interface ITable extends Iterable<IRow> {

	String getName();

	boolean isReadonly();
	
	boolean isDeletable();

	boolean isInsertable();

	boolean isUpdatable();

	boolean isAppendable();

	Iterator<IRow> getRows();

	List<IColumn> getColumns();

	IColumn getColumn(String name);

	IColumn getColumn(int index);

	Map<String, IColumn> getColumnsMap();

	List<String> getColumnNames();

	ITable getPart(ITableRange range, ITableTransformer tran);

	void setPart(ITableRange range, ITable source, boolean insert, ITableTransformer tran);

	int getRowCount();

	IRow getRow(int index);
	
	ITableRange getRange();
	
	ITableDiff compare(ITable against, ITableTransformer tran);
}
