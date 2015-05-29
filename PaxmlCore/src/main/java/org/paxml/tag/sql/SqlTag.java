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
package org.paxml.tag.sql;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.T;
import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.DBUtils;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Sql tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "sql")
public class SqlTag extends BeanTag {
	private static final Log log = LogFactory.getLog(SqlTag.class);

	/**
	 * Executor for generic sql execution.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static interface ISqlExecutor {
		Object update(String sql);

		Object query(String sql, boolean close);

	}

	/**
	 * Update only impl.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static class UpdateExecutor implements ISqlExecutor {

		private final DataSource ds;

		UpdateExecutor(final DataSource ds) {
			this.ds = ds;
		}

		private Object handleException(SQLException e, String sql) {
			String msg = "Cannot execute sql: " + sql;
			throw new PaxmlRuntimeException(msg, e);
		}

		public DataSource getDataSource() {
			return ds;
		}

		public Object query(String sql, boolean close) {
			Connection con = null;
			Statement stmt = null;
			ResultSet resultSet = null;
			try {
				con = ds.getConnection();
				stmt = con.createStatement();
				resultSet = stmt.executeQuery(sql);
				return resultSet;
			} catch (SQLException e) {
				return handleException(e, sql);
			} finally {
				if (close) {
					SqlQueryTag.closeResultSet(resultSet);
					SqlQueryTag.closeStatement(stmt);
					SqlQueryTag.closeConnection(con);
				}
			}
		}

		public Object update(String sql) {
			Connection con = null;
			Statement stmt = null;
			try {
				con = ds.getConnection();
				stmt = con.createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				return handleException(e, sql);
			} finally {
				SqlQueryTag.closeStatement(stmt);
				SqlQueryTag.closeConnection(con);
			}
		}

	}

	private JdbcTemplate jdbcTemplate;
	private Object dataSource;
	private String file;
	private boolean readColumnNames = true;
	private boolean list = true;
	private boolean singleStatement;
	private Map param;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInvoke(Context context) throws Exception {
		if (jdbcTemplate == null) {
			final DataSource ds = findDataSource(context);
			if (ds == null) {
				throw new PaxmlRuntimeException("No data source found!");
			}
			jdbcTemplate = new JdbcTemplate(ds);
		} else if (dataSource != null) {
			throw new PaxmlRuntimeException("Cannot have both the 'jdbcTemplate'" + " and the 'dataSource' attributes given!");
		}
		Object result = null;
		if (StringUtils.isNotBlank(file)) {
			Resource res = PaxmlUtils.getResource(file, getResource().getSpringResource());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = res.getInputStream();
			try {
				IOUtils.copy(in, out);
			} finally {
				IOUtils.closeQuietly(in);
			}

			result = executeSql(out.toString("UTF-8"), context);

		}
		Object value = getValue();
		final String sql;
		if (value == null) {
			sql = null;
		} else if (value instanceof List) {
			StringBuilder sb = new StringBuilder();
			for (Object item : (List) value) {
				if (item != null) {
					sb.append(item).append(" ");
				}
			}
			sql = sb.toString();
		} else {
			sql = value.toString();
		}
		if (StringUtils.isNotBlank(sql)) {
			result = executeSql(sql, context);
		}
		return result;
	}

	protected Object executeSql(String sql, Context context) {

		return executeSql(sql, new ISqlExecutor() {
			@Override
			public Object update(final String sql) {
				if (param != null) {
					NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(jdbcTemplate);
					return t.execute(sql, param, new PreparedStatementCallback<Void>() {

						@Override
						public Void doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
							ps.executeUpdate();
							return null;
						}

					});
				} else {
					jdbcTemplate.execute(sql);
				}
				return null;
			}

			@Override
			public Object query(String sql, boolean close) {
				if (param != null) {
					NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(jdbcTemplate);
					return t.queryForList(sql, param);
				} else {
					return jdbcTemplate.queryForList(sql);
				}
			}

		});

	}

	private Object executeSql(String sql, ISqlExecutor exe) {

		Object result = null;
		List<String> sqlList;
		if (singleStatement) {
			sqlList = Arrays.asList(sql);
		} else {
			sqlList = DBUtils.breakSql(sql);
		}

		final int maxIndex = sqlList.size() - 1;
		for (int i = 0; i <= maxIndex; i++) {

			if (isQuery(sql)) {
				if (list) {
					if (log.isDebugEnabled()) {
						log.debug("Running sql: " + sql);
					}
					try {
						if (param != null) {
							NamedParameterJdbcTemplate t = new NamedParameterJdbcTemplate(jdbcTemplate);
							result = t.queryForList(sql, param);
						} else {
							result = jdbcTemplate.queryForList(sql);
						}
					} catch (RuntimeException e) {
						throw new PaxmlRuntimeException("Cannot execute sql: " + sql, e);
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Running sql: " + sqlList.get(i));
					}
					try {
						result = exe.query(sqlList.get(i), true);
					} catch (RuntimeException e) {
						throw new PaxmlRuntimeException("Cannot execute sql: " + sqlList.get(i), e);
					}
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Running sql: " + sqlList.get(i));
				}
				try {
					exe.update(sqlList.get(i));
				} catch (RuntimeException e) {
					throw new PaxmlRuntimeException("Cannot execute sql: " + sqlList.get(i), e);
				}
			}

		}
		return result;

	}

	private DataSource findDataSource(Context context) {
		DataSource ds;
		if (dataSource instanceof DataSource) {
			ds = (DataSource) dataSource;
		}

		if (dataSource == null) {
			ds = SqlDataSourceTag.getDataSource(context);
		} else {
			ds= (DataSource) context.getConst(dataSource.toString(), true);
		}
		return ds;
	}

	public Object getDataSource() {
		return dataSource;
	}

	public void setDataSource(Object dataSource) {
		this.dataSource = dataSource;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean isReadColumnNames() {
		return readColumnNames;
	}

	public void setReadColumnNames(boolean readColumnNames) {
		this.readColumnNames = readColumnNames;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isList() {
		return list;
	}

	public void setList(boolean list) {
		this.list = list;
	}

	public boolean isSingleStatement() {
		return singleStatement;
	}

	public void setSingleStatement(boolean singleStatement) {
		this.singleStatement = singleStatement;
	}

	public static boolean isQuery(String sql) {

		final String select = "select";

		if (StringUtils.isBlank(sql) || sql.length() <= select.length()) {
			return false;
		}

		return Character.isWhitespace(sql.charAt(select.length())) && sql.toLowerCase().startsWith(select);
	}

	public Map getParam() {
		return param;
	}

	public void setParam(Map param) {
		this.param = param;
	}

}
