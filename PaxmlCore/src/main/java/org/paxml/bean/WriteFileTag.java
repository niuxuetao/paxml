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

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * writeFile tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "writeFile")
public class WriteFileTag extends BeanTag {
    private String encoding = "UTF-8";
    private String file;
    private boolean append = false;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        Object value = getValue();
        if (value == null) {
            return null;
        }
        FileOutputStream out = new FileOutputStream(file, append);
        String content = value.toString();
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(encoding));
        try {
            IOUtils.copy(in, out);
            return content;
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

}
