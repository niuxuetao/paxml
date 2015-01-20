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

import java.io.PrintStream;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * Print tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "print")
public class PrintTag extends BeanTag {
    private boolean line = true;
    private boolean error = false;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        final Object value = getValue();
        PrintStream stream = error ? System.err : System.out;
        if (line) {
            stream.println(value);
        } else {
            stream.print(value);
        }
        return value;
    }

    public boolean isLine() {
        return line;
    }

    public void setLine(boolean line) {
        this.line = line;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
