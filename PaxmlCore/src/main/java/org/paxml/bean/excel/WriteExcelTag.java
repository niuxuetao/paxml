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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.table.excel.ExcelFile;

/**
 * WriteExcel tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "writeExcel")
public class WriteExcelTag extends ReadExcelTag {

	private static final Log log = LogFactory.getLog(WriteExcelTag.class);

	private Object data;
	private Map columnMapper;

	private int _row = -1;
	private String cell;
	private int _column = -1;

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\niuxuetao\\Downloads\\Untitled spreadsheet.xls");
		Workbook wb1 = WorkbookFactory.create(file);
		Workbook wb2 = WorkbookFactory.create(file);

		WriteExcelTag tag = new WriteExcelTag();
		tag.setValue(file.getAbsolutePath());
		tag.setRange("A:C1");
		tag.setSheet("Sheet1");
		tag.afterPropertiesInjection(null);
		Object result = tag.doInvoke(null);
		System.out.println(result);
	}

	@Override
	protected Object doInvoke(Context context) throws Exception {
		String query = getQuery();
		if (query == null || query.isEmpty()) {
			doBasic(context);
		} else {
			doQuery(context);
		}
		return null;
	}

	private Row getRow(Sheet sheet, int index) {
		Row row = sheet.getRow(index);
		if (row == null) {
			row = sheet.createRow(index);
		}
		return row;
	}

	private Cell getCell(Row row, int index) {
		Cell cell = row.getCell(index);
		if (cell == null) {
			cell = row.createCell(index);
		}
		return cell;
	}

	private void setCellValue(Cell cell, Object obj) {
		if (obj == null) {
			cell.setCellValue((String) null);
		} else if (obj instanceof Number) {
			cell.setCellValue(((Number) obj).doubleValue());
		} else if (obj instanceof Boolean) {
			cell.setCellValue((Boolean) obj);
		} else if (obj instanceof java.util.Date) {
			cell.setCellValue((java.util.Date) obj);
		} else if (obj instanceof java.util.Calendar) {
			cell.setCellValue((java.util.Calendar) obj);
		} else if (obj instanceof java.sql.Date) {
			cell.setCellValue(new java.sql.Date(((java.sql.Date) obj).getTime()));
		} else {
			cell.setCellValue(obj.toString());
		}
	}

	private void doBasic(Context context) throws Exception {
		Sheet sheet = getExcelSheet(true);

		if (_row >= 0 && _column >= 0) {
			Row row = getRow(sheet, _row);
			Cell cell = getCell(row, _column);
			setCellValue(cell, data);
		} else if (data != null) {
			Row row = getRow(sheet, getFirstRow() - 1);
			Cell cell = getCell(row, _firstColumn - 1);
			if (data instanceof Map) {
				for (Map.Entry<?, ?> entry : ((Map<?, ?>) data).entrySet()) {

				}
			}
		}
	}

	private int getCellIndex(Object key) {
		key = columnMapper == null ? key : columnMapper.get(key);
		if (key == null) {
			throw new PaxmlRuntimeException("");
		}
		int[] xy = getXY(key.toString());
		if (xy == null) {
			throw new PaxmlRuntimeException("Invalid column: " + key);
		}
		return xy[1];

	}

	/**
	 * Do excel query.
	 * 
	 * @param context
	 * @return iterator if lazy, otherwise list.
	 * @throws Exception
	 */
	private Object doQuery(Context context) throws Exception {

		Connection con = getConnection(new ExcelFile(getValue()).getFile());
		PreparedStatement s = null;
		try {
			s = getPreparedStatement(con);
			int[] results = s.executeBatch();
			if (results.length == 1) {
				return results[0];
			} else {
				return Arrays.asList(results);
			}
		} catch (SQLException e) {
			throw new PaxmlRuntimeException(e);
		} finally {
			closeQueryResource(con, s, null);
		}
	}

	@Override
	protected void afterPropertiesInjection(Context context) {
		super.afterPropertiesInjection(context);
		if (StringUtils.isBlank(cell)) {
			return;
		}
		String c = cell.trim();

		int[] xy = getXY(c);
		_row = xy[0];
		_column = xy[1];
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	public Map getColumnMapper() {
		return columnMapper;
	}

	public void setColumnMapper(Map columnMapper) {
		this.columnMapper = columnMapper;
	}

}
