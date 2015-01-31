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

public class DefaultCellComparator implements ICellComparator {

	@Override
	public CellDiffType compare(ICell left, ICell right) {

		if (left == null) {
			if (right == null) {
				return null;
			}
			return CellDiffType.LEFT_NULL;
		}
		if (right == null) {
			return CellDiffType.RIGHT_NULL;
		}
		Object leftValue = left.getValue();
		Object rightValue = right.getValue();
		if (leftValue == null) {
			if (rightValue == null) {
				return null;
			}
			return CellDiffType.LEFT_NULL;
		}
		if (rightValue == null) {
			return CellDiffType.RIGHT_NULL;
		}
		return compare(leftValue, left.getColumn(), rightValue, right.getColumn());
	}

	/**
	 * Non-null compare with default behavior: compare of both are comparable, otherwise compare the string value.
	 * 
	 * @param valueLeft
	 *            non-null left cell value
	 * @param columnLeft
	 *            left cell column
	 * @param valueRight
	 *            non-null right cell value
	 * @param columnRight
	 *            right cell column
	 * @return
	 */
	protected CellDiffType compare(Object valueLeft, IColumn columnLeft, Object valueRight, IColumn columnRight) {
		if (valueLeft.equals(valueRight)) {
			return null;
		}
		if (valueLeft instanceof Comparable && valueRight instanceof Comparable) {
			int r = ((Comparable) valueLeft).compareTo((Comparable) valueRight);
			if (r == 0) {
				return null;
			}
			if (r < 0) {
				return CellDiffType.LEFT_SMALLER;
			}
			return CellDiffType.RIGHT_SMALLER;
		}
		return compare(valueLeft.toString(), columnLeft, valueRight.toString(), columnRight);
	}

}
