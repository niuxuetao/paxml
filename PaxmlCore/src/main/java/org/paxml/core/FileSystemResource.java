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
package org.paxml.core;

import java.io.File;

/**
 * The file system impl of paxml resource.
 * 
 * @author Xuetao Niu
 * 
 */
public class FileSystemResource extends PaxmlResource {
    /**
     * The prefix of the full path.
     */
    public static final String PREFIX = "file:";

    private final File file;

    /**
     * Construct from a file.
     * 
     * @param file
     *            the file
     */
    public FileSystemResource(final File file) {

        super(PREFIX + file.getAbsolutePath().replace('\\', '/'));
        
        this.file = file;
        
    }

    public File getFile() {
        return file;
    }

}
