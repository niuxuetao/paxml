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

public interface IRow extends Iterable<ICell>{
	int getIndex();
	Iterator<ICell> getCells();
	ICell getCell(String name);
	ICell getCell(int index);
	void setCellValue(int index,  Object value);
	void setCellValue(String name, Object value);
	void setCellValues(int from, int to, Iterator<Object> values);
	void setCellValues(Map<String,Object> values);
	
	ITable getTable();
	
	IRowDiff compare(IRow against, ITableTransformer tran);
}
