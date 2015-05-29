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
package org.paxml.table.excel;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.Coerceable;

public class ExcelFile implements Coerceable {
	private static final Log log = LogFactory.getLog(ExcelFile.class);
	private volatile Workbook workbook;
	private volatile FormulaEvaluator evaluator;
	private volatile File file;
	
	public ExcelFile(Object f) {

		super();

		if (f instanceof ExcelFile) {
			ExcelFile ef = (ExcelFile) f;
			workbook = ef.workbook;
			evaluator = ef.evaluator;
			file = ef.file;

		} else {
			this.file = f instanceof File ? (File) f : new File(f.toString());

			if (log.isDebugEnabled()) {
				log.debug("Opening excel file: " + file.getAbsolutePath());
			}
			try {
				if (file.exists()) {

					workbook = WorkbookFactory.create(file);

				} else {
					workbook = file.getName().toLowerCase().endsWith(".xlsx") ? new XSSFWorkbook() : new HSSFWorkbook();
				}
				evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			} catch (Exception e) {
				close();
				throw new PaxmlRuntimeException("Cannot open excel file: " + file.getAbsolutePath(), e);
			}
		}
	}
	public Sheet getSheet(String sheet, boolean createIfNone) {

		Sheet s = null;
		int index = -1;
		if (workbook.getNumberOfSheets() > 0) {
			if (StringUtils.isBlank(sheet)) {
				s = workbook.getSheetAt(0);
			} else {
				s = workbook.getSheet(sheet);
				if (s == null) {

					try {
						index = Integer.parseInt(sheet.trim()) - 1;
					} catch (Exception e) {
						throw new PaxmlRuntimeException("Please specify either an existing sheet name or a sheet index number. This is neither: " + sheet, e);
					}
					if (index < 0) {
						index = workbook.getActiveSheetIndex();
					}
					if (index >= 0) {
						s = workbook.getSheetAt(index);
					}
				}
			}
		}
		if (s == null) {
			if (createIfNone) {
				if (sheet == null || index == 0) {
					s = workbook.createSheet();
				} else {
					s = workbook.createSheet(sheet);
				}
			} else {
				throw new PaxmlRuntimeException("No sheet found with index " + index + " in file: " + file.getAbsolutePath());
			}
		}
		return s;
	}
	public boolean exists(){
		return file.exists();
	}
	public void save() {
		try {
			workbook.write(new FileOutputStream(file));
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot save excel to file: " + file.getAbsolutePath(), e);
		}
	}

	public void clearEvaluatorCache() {
		evaluator.clearAllCachedResultValues();
	}

	@Override
	protected void finalize() {

		close();

	}

	public final void close() {
		if (evaluator != null) {
			try {
				clearEvaluatorCache();
			} catch (Exception e) {

			} finally {
				evaluator = null;
			}
		}

		if (workbook != null) {
			try {
				workbook.close();
			} catch (Exception e) {

			} finally {
				workbook = null;
			}
		}
	}

	public Object getCellValue(Cell cell) {
		CellValue cellValue = evaluator.evaluate(cell);
		switch (cellValue.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return cellValue.getBooleanValue();
		case Cell.CELL_TYPE_NUMERIC:
			return cellValue.getNumberValue();
		case Cell.CELL_TYPE_STRING:
			return cellValue.getStringValue();
		case Cell.CELL_TYPE_BLANK:
			return "";
		case Cell.CELL_TYPE_ERROR:
			return cellValue.getError(cell.getErrorCellValue()).getStringValue();
			// CELL_TYPE_FORMULA will never happen
		case Cell.CELL_TYPE_FORMULA:
			throw new PaxmlRuntimeException("Internal error: invalid case");
		default:
			return null;
		}
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public File getFile() {
		return file;
	}
	@Override
	public String toString() {
		return file.getAbsolutePath();
	}
	
}
