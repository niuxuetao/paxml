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
package org.paxml.tag.sql;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.core.PaxmlRuntimeException;

public class DdlScript implements Comparable<DdlScript> {
    public static enum Type {
        // the natural order determines the compare result
        CREATE_DDL("-create-ddl-"), CREATE_DATA("-create-data-"), UPDATE_DDL("-update-ddl-"), UPDATE_DATA(
                "-update-data-");
        private final String name;

        private Type(String name) {
            this.name = name;
        }

        private static Type parse(String fn) {
            for (Type t : Type.values()) {
                if (StringUtils.containsIgnoreCase(fn, t.name)) {
                    return t;
                }
            }
            throw new PaxmlRuntimeException("Cannot detect type of ddl from file name: " + fn);
        }
    }

    private final File container;
    private final String file;
    private final DdlVersion version;
    private final Type type;

    public DdlScript(File container, String file) {
        this.file = file;
        file = FilenameUtils.getBaseName(file);
        String v = StringUtils.substringAfterLast(file, "-");
        this.version = new DdlVersion(v);
        this.container = container;
        this.type = Type.parse(file);
    }

    @Override
    public int compareTo(DdlScript o) {
        int r = version.compareTo(o.version);

        if (r != 0) {
            return r;
        }

        return type.compareTo(o.type);
    }

    public Type getType() {
        return type;
    }

    public String readContent() {
        File theFile = null;
        InputStream in = null;
        try {
            if (container.isDirectory()) {
                theFile = new File(container, file);
                in = new FileInputStream(theFile);
            } else {
                // assume it being a zip
                ZipFile zip = new ZipFile(container);
                ZipEntry entry = zip.getEntry(file);
                in = zip.getInputStream(entry);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            return out.toString("UTF-8");
        } catch (IOException e) {
            throw new PaxmlRuntimeException("Cannot read file content from: " + getFileName(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public File getContainer() {
        return container;
    }

    public String getFileName() {
        if (container.isDirectory()) {
            return new File(container, file).getAbsolutePath();
        } else {
            return container.getAbsolutePath() + "!" + file;
        }
    }

    public String getFile() {
        return file;
    }

    public DdlVersion getVersion() {
        return version;
    }
    
    @Override
    public String toString() {
        return getFile();
    }

}
