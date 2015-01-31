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

public class CellDiff {
	private CellDiffType type;
	private int leftColumnIndex;
	private String leftColumnName;
	private int rightColumnIndex;
	private String rightColumnName;
	private Object leftValue;
	private Object rightValue;

	public CellDiffType getType() {
		return type;
	}

	public void setType(CellDiffType type) {
		this.type = type;
	}

	public int getLeftColumnIndex() {
		return leftColumnIndex;
	}

	public void setLeftColumnIndex(int leftColumnIndex) {
		this.leftColumnIndex = leftColumnIndex;
	}

	public String getLeftColumnName() {
		return leftColumnName;
	}

	public void setLeftColumnName(String leftColumnName) {
		this.leftColumnName = leftColumnName;
	}

	public int getRightColumnIndex() {
		return rightColumnIndex;
	}

	public void setRightColumnIndex(int rightColumnIndex) {
		this.rightColumnIndex = rightColumnIndex;
	}

	public String getRightColumnName() {
		return rightColumnName;
	}

	public void setRightColumnName(String rightColumnName) {
		this.rightColumnName = rightColumnName;
	}

	public Object getLeftValue() {
		return leftValue;
	}

	public void setLeftValue(Object leftValue) {
		this.leftValue = leftValue;
	}

	public Object getRightValue() {
		return rightValue;
	}

	public void setRightValue(Object rightValue) {
		this.rightValue = rightValue;
	}

}
