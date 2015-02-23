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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.file.IFile;
import org.paxml.table.AbstractTable;
import org.paxml.table.IColumn;
import org.paxml.table.IRow;
import org.paxml.table.ITable;
import org.paxml.table.ITableRange;
import org.paxml.table.ITableTransformer;
import org.paxml.table.TableColumn;
import org.paxml.util.NamedSequences;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvTable extends AbstractTable implements IFile {

	private static final Log log = LogFactory.getLog(CsvTable.class);

	private static final NamedSequences seq = new NamedSequences();
	private final Resource res;
	private final CsvOptions opt;
	private final Map<String, IColumn> headers;
	private final CsvListReader reader;
	private final CsvListWriter writer;
	private final File writerFile;
	private boolean needToWrite;
	private List<String> row;

	public CsvTable(Resource res, CsvOptions opt, ITableRange range) {
		if (opt == null) {
			opt = new CsvOptions();
		}
		CsvPreference pref = opt.buildPreference();
		this.res = res;
		this.opt = opt;
		if (res.exists()) {
			try {
				reader = new CsvListReader(new InputStreamReader(res.getInputStream(), opt.getEncoding()), pref);
			} catch (IOException e) {
				throw new PaxmlRuntimeException("Cannot read csv file: " + PaxmlUtils.getResourceFile(res), e);
			}
		} else {
			reader = null;
		}
		if (opt.isWithHeader()) {
			headers = new LinkedHashMap<String, IColumn>();
			if (reader != null) {
				String[] h;
				try {
					h = reader.getHeader(true);
				} catch (IOException e) {
					throw new PaxmlRuntimeException("Cannot read csv file header: " + PaxmlUtils.getResourceFile(res), e);
				}
				if (h != null) {
					for (String s : h) {
						addColumn(s);
					}
				}
			}
		} else {
			headers = null;
		}
		CsvListWriter w = null;
		if (!opt.isReadOnly()) {
			writerFile = getWriterFile(res);
			if (writerFile != null) {

				try {
					w = new CsvListWriter(new FileWriter(reader == null ? res.getFile() : writerFile), pref);
				} catch (IOException e) {
					// do nothing, because this means writer cannot write to the
					// file.
				}

			}
		} else {
			writerFile = null;
		}
		writer = w;

		if (reader == null && writer == null) {
			throw new PaxmlRuntimeException("Can neither read from nor write to csv file: " + PaxmlUtils.getResourceFile(res));
		}

		setRange(range);
	}

	public int getCurrentRowIndex() {
		return reader.getRowNumber();
	}

	private static File getWriterFile(Resource res) {
		try {
			File f = res.getFile();
			return PaxmlUtils.getSiblingFile(f, "." + seq.getNextValue(f.getAbsolutePath()) + ".write", true);
		} catch (IOException e) {
			return null;
		}
	}

	public CsvOptions getOptions() {
		return opt;
	}

	@Override
	public String getName() {
		return res.getFilename();
	}

	@Override
	public List<IColumn> getColumns() {
		return new ArrayList<IColumn>(headers.values());
	}

	@Override
	public IColumn getColumn(String name) {
		return headers.get(name);
	}

	@Override
	public IColumn addColumn(String name) {
		return addColumn(name, headers.size());
	}

	@Override
	public IColumn addColumn(String name, int index) {
		TableColumn col = new TableColumn(index, name);
		headers.put(name, col);
		return col;
	}

	@Override
	public Map<String, IColumn> getColumnsMap() {
		return Collections.unmodifiableMap(headers);
	}

	@Override
	public List<String> getColumnNames() {
		return new ArrayList<String>(headers.keySet());
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
		return -1;
	}

	@Override
	protected String getResourceIdentifier() {
		return PaxmlUtils.getResourceIdentifier(res);
	}

	@Override
	protected Iterator<IRow> getAllRows() {
		readRow();
		return new Iterator<IRow>() {

			@Override
			public boolean hasNext() {
				return row == null;
			}

			@Override
			public IRow next() {
				CsvRow r = new CsvRow(CsvTable.this);
				readRow();
				return r;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};

	}

	private void readRow() {
		if (writer != null && row != null) {
			// flush the current row to the writer, before advancing
			try {
				writer.write(row);
			} catch (IOException e) {
				throw new PaxmlRuntimeException("Cannot write to shadow csv file: " + writerFile.getAbsolutePath(), e);
			}
		}
		try {
			row = reader.read();
		} catch (IOException e) {
			throw new PaxmlRuntimeException("Cannot read next line from csv file: " + getResourceIdentifier(), e);
		}
	}

	List<String> getColumnCurrentRow() {
		return row;
	}

	void writeCurrentRow(int index, String value) {
		if (row == null) {
			row = new ArrayList<String>();
		}
		if (index < row.size()) {
			row.set(index, value);
		} else {
			for (int i = index - row.size(); i > 0; i--) {
				row.add(null);
			}
			row.add(value);
		}
		needToWrite = true;
	}

	/**
	 * Close the reader, then close the shadow writer and replace the original
	 * file if necessary.
	 */
	@Override
	public void close() throws IOException {
		try {
			if (reader != null) {
				reader.close();
			}
		} finally {
			if (writer != null) {
				// dump the last row to the writer
				if (row != null) {
					writer.write(row);
				}
				writer.flush();
				writer.close();
				File file = res.getFile();
				if (needToWrite) {
					if (log.isDebugEnabled()) {
						log.debug("Writing csv file: " + file.getAbsolutePath());
					}
					File del = null;
					if (file.exists()) {
						File f = res.getFile();
						del = PaxmlUtils.getSiblingFile(f, "." + seq.getNextValue(f.getAbsolutePath()) + ".delete", true);
						if (!file.renameTo(del)) {
							if (log.isErrorEnabled()) {
								log.error("Cannot rename csv file '" + f.getAbsolutePath() + "' to '" + del.getAbsolutePath() + "'");
							}
							throw new PaxmlRuntimeException("Cannot overwrite csv file: " + getResourceIdentifier());
						}

					}
					if (!writerFile.renameTo(file)) {
						if (log.isErrorEnabled()) {
							log.error("Cannot rename csv file '" + writerFile.getAbsolutePath() + "' to '" + file.getAbsolutePath() + "'");
						}
						throw new PaxmlRuntimeException("Cannnot overwrite csv file: " + getResourceIdentifier());
					}
					if (del != null) {
						del.delete();
						if (log.isWarnEnabled()) {
							log.warn("Cannot delete shadow csv file: " + del.getAbsolutePath());
						}
					}
				} else {
					if (log.isDebugEnabled()) {
						log.debug("No need to write to csv file: " + file.getAbsolutePath());
					}
					if (!writerFile.delete()) {
						if (log.isWarnEnabled()) {
							log.warn("Cannot delete shadow csv file: " + writerFile.getAbsolutePath());
						}
					}
				}
			}
		}
	}

}
