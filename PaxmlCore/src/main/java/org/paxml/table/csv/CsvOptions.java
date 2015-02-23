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

import org.supercsv.prefs.CsvPreference;

public class CsvOptions {
	private String lineSeparator = CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols();
	private char columnSeparator =  (char)CsvPreference.STANDARD_PREFERENCE.getDelimiterChar();
	private char quote =  (char)CsvPreference.STANDARD_PREFERENCE.getQuoteChar();
	
	private boolean withHeader;
	private boolean readOnly;
	private String encoding;
	
	public String getLineSeparator() {
		return lineSeparator;
	}
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
	public char getColumnSeparator() {
		return columnSeparator;
	}
	public void setColumnSeparator(char columnSeparator) {
		this.columnSeparator = columnSeparator;
	}
	public char getQuote() {
		return quote;
	}
	public void setQuote(char quote) {
		this.quote = quote;
	}
	public boolean isWithHeader() {
		return withHeader;
	}
	public void setWithHeader(boolean withHeader) {
		this.withHeader = withHeader;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public CsvPreference buildPreference(){
		return new CsvPreference.Builder(getQuote(), getColumnSeparator(),
		        getLineSeparator()).build();
	}
	
}
