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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.paxml.file.IFile;
import org.paxml.table.AbstractTable;
import org.paxml.table.IColumn;
import org.paxml.table.IRow;
import org.paxml.table.ITable;
import org.paxml.table.ITableRange;
import org.paxml.table.ITableTransformer;
import org.springframework.core.io.Resource;

public class CsvTable extends AbstractTable implements IFile {
	private final Resource res;
	
	public CsvTable(Resource res, boolean hasHeader) {
		this.res = res;		
	}

	@Override
	public String getName() {
		return res.getFilename();
	}

	@Override
	public List<IColumn> getColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IColumn getColumn(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IColumn addColumn(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IColumn addColumn(String name, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, IColumn> getColumnsMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITable getPart(ITableRange range, ITableTransformer tran) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPart(ITableRange range, ITable source, boolean insert, ITableTransformer tran) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IRow getRow(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getResourceIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Iterator<IRow> getAllRows() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void close() throws IOException {
	    // TODO Auto-generated method stub
	    
    }

}
