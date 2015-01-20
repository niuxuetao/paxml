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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.util.ResourceUtils;

/**
 * Unzip tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "unzip")
public class UnzipTag extends BeanTag {
    private String file;
    private String dir;
    @Override
    protected Object doInvoke(Context context) throws Exception {
        unzip(ResourceUtils.getFile(file), ResourceUtils.getFile(dir));
        return null;

    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public String getDir() {
        return dir;
    }
    public void setDir(String dir) {
        this.dir = dir;
    }    
    public static void unzip(File file, File dir) {
        dir.mkdirs();
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(file);

            Enumeration entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {

                    new File(dir, entry.getName()).mkdirs();
                    continue;
                }

                InputStream in = null;
                OutputStream out = null;
                try {
                    zipFile.getInputStream(entry);
                    out = new BufferedOutputStream(new FileOutputStream(entry.getName()));
                    IOUtils.copy(in, out);
                } finally {
                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                }
            }

        } catch (IOException ioe) {

            throw new PaxmlRuntimeException("Cannot unzip file: " + file.getAbsolutePath() + " under dir: "
                    + dir.getAbsolutePath(), ioe);

        } finally {

            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

}
