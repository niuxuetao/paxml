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
package org.paxml.bean.excel;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.paxml.annotation.Tag;
import org.paxml.bean.AbstractLazyTag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.table.excel.ExcelFile;
import org.paxml.util.ReflectUtils;

/**
 * ReadExcel tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "readExcel")
public class ReadExcelTag extends AbstractLazyTag {
	private static final String ODBC_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
	private static final Log log = LogFactory.getLog(ReadExcelTag.class);

	private ExcelFile file;

	private String query;
	private Object queryParameter;
	private String sheet;
	private int firstRow;
	private String firstColumn;
	private int lastRow = Integer.MAX_VALUE;
	private String lastColumn;
	private String range;

	protected int _firstColumn;
	protected int _lastColumn = Integer.MAX_VALUE;

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\niuxuetao\\Downloads\\Untitled spreadsheet.xls");
		Workbook wb1 = WorkbookFactory.create(file);
		Workbook wb2 = WorkbookFactory.create(file);

		ReadExcelTag tag = new ReadExcelTag();
		tag.setValue(file.getAbsolutePath());
		tag.setRange("A2:C");
		tag.setSheet("Sheet1");
		tag.afterPropertiesInjection(null);
		Object result = tag.doInvoke(null);
		System.out.println(result);
	}

	@Override
	protected Iterator getIterator(Context context) throws Exception {
		if (StringUtils.isBlank(query)) {
			return doBasic(context);
		} else {
			return doQuery(context);
		}
	}

	protected Sheet getExcelSheet(boolean createIfNone) {

		Workbook wb = file.getWorkbook();
		Sheet s = null;
		int index = -1;
		if (wb.getNumberOfSheets() > 0) {
			if (StringUtils.isBlank(sheet)) {
				s = wb.getSheetAt(0);
			} else {
				s = wb.getSheet(sheet);
				if (s == null) {

					try {
						index = Integer.parseInt(sheet.trim()) - 1;
					} catch (Exception e) {
						throw new PaxmlRuntimeException("Please specify either an existing sheet name or a sheet index number. This is neither: " + sheet, e);
					}
					if (index < 0) {
						index = file.getWorkbook().getActiveSheetIndex();
					}
					if (index >= 0) {
						s = wb.getSheetAt(index);
					}
				}
			}
		}
		if (s == null) {
			if (createIfNone) {
				if (sheet == null || index == 0) {
					s = wb.createSheet();
				} else {
					s = wb.createSheet(sheet);
				}
			} else {
				throw new PaxmlRuntimeException("No sheet found with index " + index + " in file: " + file.getFile().getAbsolutePath());
			}
		}
		return s;
	}

	private Iterator doBasic(Context context) throws Exception {

		return new Iterator() {
			private Iterator<Row> it;
			private int index;
			private Map<Integer, String> headers = new LinkedHashMap<Integer, String>();

			private void start() {

				boolean ok = false;
				try {

					Sheet s = getExcelSheet(false);

					it = s.iterator();
					// find the start row
					if (log.isDebugEnabled()) {
						log.debug("Start reading from row " + Math.max(1, firstRow) + " of sheet: " + s.getSheetName());
					}

					for (int i = 1; i < firstRow && it.hasNext(); i++) {
						it.next();
						index++;
					}

					ok = true;
				} finally {
					if (!ok) {
						end();
					}
				}
			}

			private void end() {
				it = null;
				file.close();
			}

			@Override
			public boolean hasNext() {
				if (it == null) {
					start();
				}
				if (lastRow > 0 && index > lastRow - 1) {
					end();
					return false;
				}
				try {
					boolean has = it.hasNext();
					if (!has) {
						end();
					}
					return has;
				} catch (Exception e) {
					end();
					throw new PaxmlRuntimeException(e);
				}
			}

			@Override
			public Object next() {
				try {
					Row row = it.next();
					Object r = readRow(row);
					index++;
					return r;
				} catch (Exception e) {
					end();
					throw new PaxmlRuntimeException(e);
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private Map<Object, Object> readRow(Row row) {

				final int firstCell = Math.max(row.getFirstCellNum(), _firstColumn);
				final int lastCell = _lastColumn < 0 ? row.getLastCellNum() - 1 : Math.min(row.getLastCellNum() - 1, _lastColumn);

				if (log.isDebugEnabled()) {
					log.debug("Reading cells: " + new CellReference(index, firstCell).formatAsString() + ":" + new CellReference(index, lastCell).formatAsString());
				}

				Map<Object, Object> result = new LinkedHashMap<Object, Object>();
				for (int i = firstCell; i <= lastCell; i++) {
					Cell cell = row.getCell(i);
					if (cell != null) {
						Object value = file.getCellValue(cell);
						// dual keys for the same value
						result.put(i, value);
						String key = headers.get(i);
						if (key == null) {
							key = new CellReference(-1, i).formatAsString();
							headers.put(i, key);
						}
						result.put(key, value);
					}
				}
				return result;
			}

		};

	}

	protected void closeQueryResource(Connection con, PreparedStatement s, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {

			} finally {
				rs = null;
			}
		}
		if (s != null) {
			try {
				s.close();
			} catch (Exception e) {

			} finally {
				s = null;
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {

			} finally {
				con = null;
			}
		}
	}

	/**
	 * Do excel query.
	 * 
	 * @param context
	 * @return iterator if lazy, otherwise list.
	 * @throws Exception
	 */
	private Iterator doQuery(Context context) throws Exception {

		return new Iterator() {
			private Connection con;
			private PreparedStatement s;
			private ResultSet rs;
			private String[] columns;

			@Override
			public void finalize() {
				end();
			}

			private void end() {
				closeQueryResource(con, s, rs);
			}

			private void start() {
				if (con != null) {
					return;
				}
				File f = file.getFile();
				con = getConnection(f);
				if (log.isDebugEnabled()) {
					log.debug("Opened excel file via odbc: " + f.getAbsolutePath());
					log.debug("Executing excel query: " + query);
				}
				try {
					s = getPreparedStatement(con);
					s.execute();
					rs = s.getResultSet();
					if (rs != null) {

						ResultSetMetaData meta = rs.getMetaData();
						columns = new String[meta.getColumnCount()];
						for (int i = columns.length - 1; i >= 0; i--) {
							columns[i] = meta.getColumnName(i);
						}

					}
				} catch (Exception e) {
					end();
					throw new PaxmlRuntimeException("Cannot execute excel query: " + query);
				}
			}

			@Override
			public boolean hasNext() {
				start();
				try {
					boolean hasNext = rs != null && rs.next();
					if (!hasNext) {
						end();
					}
					return hasNext;
				} catch (Exception e) {
					end();
					throw new PaxmlRuntimeException(e);
				}
			}

			@Override
			public Object next() {
				Map<String, Object> row = new LinkedHashMap<String, Object>(columns.length);
				try {

					for (int i = 1; i <= columns.length; i++) {
						String column = columns[i];
						Object value = rs.getObject(i);
						row.put(column, value);
					}
				} catch (Exception e) {
					end();
					throw new PaxmlRuntimeException(e);
				}
				return row;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};

	}

	protected PreparedStatement getPreparedStatement(Connection con) throws SQLException {

		PreparedStatement p = con.prepareStatement(query);
		if (queryParameter != null) {
			List list = new ArrayList();
			ReflectUtils.collect(queryParameter, list, true);
			for (int i = list.size() - 1; i >= 0; i--) {
				p.setObject(i, list.get(i));
			}
		}
		return p;
	}

	protected Connection getConnection(File f) {
		try {
			Class.forName(ODBC_DRIVER);
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}
		try {
			return DriverManager.getConnection("jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};" + "Dbq="
					+ f.getAbsolutePath().replace("/", "" + File.pathSeparatorChar) + ";");
		} catch (SQLException e) {
			throw new PaxmlRuntimeException("Cannot open excel file via odbc: " + f.getAbsolutePath(), e);
		}
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFirstColumn() {
		return firstColumn;
	}

	public void setFirstColumn(String firstColumn) {
		this.firstColumn = firstColumn;
	}

	public String getLastColumn() {
		return lastColumn;
	}

	public void setLastColumn(String lastColumn) {
		this.lastColumn = lastColumn;
	}

	public String getSheet() {
		return sheet;
	}

	public void setSheet(String sheet) {
		this.sheet = sheet;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}

	public int getLastRow() {
		return lastRow;
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public Object getQueryParameter() {
		return queryParameter;
	}

	public void setQueryParameter(Object queryParameter) {
		this.queryParameter = queryParameter;
	}

	public ExcelFile getFile() {
		return file;
	}

	public void setFile(ExcelFile file) {
		this.file = file;
	}

	@Override
	protected void afterPropertiesInjection(Context context) {
		super.afterPropertiesInjection(context);
		if (StringUtils.isNotBlank(range)) {
			range = range.trim().toUpperCase();
			String[] ranges = range.split(":");
			int[] xy1 = getXY(ranges[0]);
			int[] xy2 = getXY(ranges.length > 1 ? ranges[1] : "");
			if (xy1 == null || xy2 == null) {
				throw new PaxmlRuntimeException("Invalid range specified: " + range + ". Expect standard excel range specification, e.g. 'A2:E11' or 'B:D4' or 'A:F'");
			}
			firstRow = xy1[0] + 1;
			lastRow = xy2[0] + 1;
			_firstColumn = xy1[1];
			_lastColumn = xy2[1];
		}
		if (StringUtils.isNotBlank(firstColumn)) {
			_firstColumn = new CellReference(firstColumn).getCol();
		}
		if (StringUtils.isNotBlank(lastColumn)) {
			_lastColumn = new CellReference(lastColumn).getCol();
		}
		if (firstRow < 1) {
			firstRow = 1;
		}
		if (lastRow < 1) {
			lastRow = Integer.MAX_VALUE;
		}

		if (_firstColumn < 0) {
			_firstColumn = 0;
		}
		if (_lastColumn < 0) {
			_lastColumn = Integer.MAX_VALUE;
		}
		if (firstRow > lastRow) {
			int tmp = lastRow;
			lastRow = firstRow;
			firstRow = tmp;
		}
		if (_firstColumn > _lastColumn) {
			int tmp = _lastColumn;
			_lastColumn = _firstColumn;
			_firstColumn = tmp;
		}
	}

	protected int[] getXY(String xy) {
		xy = xy.trim();
		if (StringUtils.isEmpty(xy)) {
			return new int[] { -1, -1 };
		}
		if (!StringUtils.isAlphanumeric(xy)) {
			return null;
		}
		CellReference ref = new CellReference(xy);
		return new int[] { ref.getRow() < 0 ? -1 : ref.getRow(), ref.getCol() < 0 ? -1 : ref.getCol() };
	}
}
