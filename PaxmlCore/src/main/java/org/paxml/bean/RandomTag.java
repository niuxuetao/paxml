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
package org.paxml.bean;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.el.UtilFunctions;

/**
 * Random tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "random")
public class RandomTag extends BeanTag {
    private double low;
    private double high;
    private boolean fractional;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        final double num;
        if (low == 0 && low == high) {
            num = System.currentTimeMillis();
        } else {
            num = random();
        }
        return num;
    }

    private double random() {

        if (fractional) {
            return UtilFunctions.random(low, high);
        } else {
            return UtilFunctions.random(Math.round(low), Math.round(high));
        }

    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public boolean isFractional() {
        return fractional;
    }

    public void setFractional(boolean fractional) {
        this.fractional = fractional;
    }

}
