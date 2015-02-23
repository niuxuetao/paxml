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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;

/**
 * Base class for paxml resources. It provides the fixed implementation for
 * equals() and and hashCode(), which is why this class should be used instead
 * of directly using spring resource.
 * 
 * Instances of this class is thread safe, the thread safety should also be
 * guaranteed by subclasses.
 * 
 * Each resource has a name, which suppose to be unique.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class PaxmlResource {

    /**
     * The full path of the resource, with the prefix.
     */
    private final String path;

    /**
     * Constructor with full path, children class should overwrite this
     * constructor.
     * 
     * @param path
     *            the full path.
     */
    protected PaxmlResource(final String path) {
        this.path = path;
    }

    /**
     * Create resource from full path.
     * 
     * @param pathWithPrefix
     *            the full path with prefix. See all subclasses of this class
     *            for the possible prefixes.
     * @return the resource or null if the prefix is not supported.
     */
    public static PaxmlResource createFromPath(String pathWithPrefix) {
        if (pathWithPrefix.startsWith(FileSystemResource.PREFIX)) {
            return new FileSystemResource(
                    new File(pathWithPrefix.substring(FileSystemResource.PREFIX.length())));
        } else if (pathWithPrefix.startsWith(ClasspathResource.SPRING_PREFIX1)) {
            String barePath = pathWithPrefix.substring(ClasspathResource.SPRING_PREFIX1.length());

            return new ClasspathResource(barePath);
        } else if (pathWithPrefix.startsWith(ClasspathResource.SPRING_PREFIX2)) {
            String barePath = pathWithPrefix.substring(ClasspathResource.SPRING_PREFIX2.length());

            return new ClasspathResource(barePath);
        } else if (pathWithPrefix.startsWith("jar:")) {
            String barePath=StringUtils.substringAfterLast(pathWithPrefix, "!");
            return new ClasspathResource(barePath);
        }
        return null;
        
    }

    /**
     * Create from a spring resource.
     * 
     * @param res
     *            the spring resource
     * @return the paxml resource
     */
    public static PaxmlResource createFromResource(Resource res) {
        URL url;
        try {
            url = res.getURL();
        } catch (IOException e) {
            throw new PaxmlRuntimeException("Cannot create paxmlResource from spring resource: " + res, e);
        }
        return createFromPath(url.toString());
    }

    /**
     * Open the input stream for the resource. It delegates to calling
     * getSpringResource().getInputStream().
     * 
     * @return the input stream, never null.
     */
    public InputStream openInputStream() {
        try {
            return getSpringResource().getInputStream();
        } catch (IOException e) {
            throw new PaxmlRuntimeException(e);
        }
    }

    /**
     * Get the name of the resource.
     * 
     * @return the name of the resource.
     */
    public String getName() {
        return FilenameUtils.getBaseName(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName() + ", " + path;
    }

    public String getPath() {
        return path;
    }

    public Resource getSpringResource() {
        return PaxmlUtils.getResource(path, null);
    }

    public long getLastModified() {
        Resource res = getSpringResource();
        if (!res.exists()) {
            return -1;
        }
        final File file;
        try {
            file = res.getFile();
        } catch (IOException e) {
            return 0;
        }
        return file.lastModified();

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaxmlResource other = (PaxmlResource) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }
    
    
}
