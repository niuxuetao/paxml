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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.dbutils.ResultSetIterator;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.table.AbstractTable;
import org.paxml.table.IColumn;
import org.paxml.table.IRow;
import org.paxml.table.ITable;
import org.paxml.table.ITableRange;
import org.paxml.table.ITableTransformer;
import org.paxml.table.TableColumn;
import org.springframework.jdbc.support.JdbcUtils;

public class JdbcTable extends AbstractTable {
	private Connection con;
	private ResultSet rs;
	private PreparedStatement ps;
	private ResultSetMetaData meta;
	private String query;
	private Collection<Object> queryParams;
	private final Map<String, IColumn> columns = new LinkedHashMap<String, IColumn>();

	public JdbcTable(Connection con, String query, Collection<Object> queryParams) {
		try {
			this.con = con;
			this.query = query;
			this.queryParams = queryParams;
			ps = con.prepareStatement(query);
			if (queryParams != null) {
				int i = 0;
				for (Object p : queryParams) {
					ps.setObject(i++, p);
				}
			}
			ps.execute();
			rs = ps.getResultSet();
			meta = rs.getMetaData();

			for (int i = 0; i < meta.getColumnCount(); i++) {
				String name = meta.getColumnName(i);
				columns.put(name, new TableColumn(i, name));
			}
		} catch (Exception e) {
			close(false);
			throw new PaxmlRuntimeException("Cannot execute jdbc query: " + query, e);
		}
		setReadonly(true);
	}

	public void close(boolean closeConnection) {
		JdbcUtils.closeResultSet(rs);
		rs = null;
		JdbcUtils.closeStatement(ps);
		ps = null;
		if (closeConnection) {
			JdbcUtils.closeConnection(con);
		}
		con = null;
	}

	@Override
	protected void finalize() {
		close(true);
	}

	@Override
	public String getName() {

		try {
			return meta.getTableName(0);
		} catch (SQLException e) {
			throw new PaxmlRuntimeException("Cannot get table name with query: " + query);
		}

	}

	@Override
	protected Iterator<IRow> getAllRows() {
		return new AbstractIteratorDecorator(new ResultSetIterator(rs)) {
			private int index;

			@Override
			public Object next() {

				Object[] row = (Object[]) super.next();
				return new JdbcRow(index++, row, JdbcTable.this);
			}

		};

	}

	@Override
	public List<IColumn> getColumns() {
		return new ArrayList<IColumn>(columns.values());
	}

	@Override
	public IColumn getColumn(String name) {
		return columns.get(name);
	}

	@Override
	public Map<String, IColumn> getColumnsMap() {
		return Collections.unmodifiableMap(columns);
	}

	@Override
	public List<String> getColumnNames() {
		return new ArrayList<String>(columns.keySet());
	}

	@Override
	public ITable getPart(ITableRange range, ITableTransformer tran) {

		JdbcTable t = new JdbcTable(con, query, queryParams);
		t.setReadTransformer(tran);
		t.setRange(range);
		return t;
	}

	@Override
	public void setPart(ITableRange range, ITable source, boolean insert, ITableTransformer tran) {
		assertWritable();
	}

	@Override
	public int getRowCount() {
		return -1;
	}

	@Override
	public IRow getRow(int index) {
		return null;
	}

	@Override
	protected String getResourceIdentifier() {
		return query;
	}

}
