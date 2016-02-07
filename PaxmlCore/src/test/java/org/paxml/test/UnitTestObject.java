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
package org.paxml.test;

/**
 * TestNG test object for paxml unit tests.
 * 
 * @author Xuetao Niu
 * 
 */
public class UnitTestObject {
    private int intValue;
    private String stringValue;

    /**
     * @return the intValue
     */
    public int getIntValue() {
        return intValue;
    }

    /**
     * @param intValue
     *            the intValue to set
     */
    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    /**
     * @return the stringValue
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * @param stringValue
     *            the stringValue to set
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Make additions.
     * 
     * @param i
     *            operand 1
     * @param j
     *            operand 2
     * @return addition result.
     */
    public int doAdd(int i, int j) {
        return i + j;
    }

	@Override
	public String toString() {
		return "UnitTestObject [intValue=" + intValue + ", stringValue=" + stringValue + "]";
	}
    
}
