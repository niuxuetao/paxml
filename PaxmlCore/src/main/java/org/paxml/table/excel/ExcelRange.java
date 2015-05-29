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

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellReference;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.table.TableRange;

public class ExcelRange extends TableRange {

	public ExcelRange(String range, boolean relative) {
		super(false);
		this.relative = relative;
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
			firstColumn = xy1[1];
			lastColumn = xy2[1];
		}
		correctValues();

	}

	public final static int[] getXY(String xy) {
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
