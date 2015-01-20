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

import org.springframework.core.io.ClassPathResource;

/**
 * Resource that exists on classpath.
 * 
 * @author Xuetao Niu
 * 
 */
public class ClasspathResource extends PaxmlResource {
    /**
     * The path prefix for this resource.
     */
    public static final String PREFIX = "classpath:";

    public static final String SPRING_PREFIX1 = PREFIX;
    public static final String SPRING_PREFIX2 = "classpath*:";

    private final String rawPath;

    /**
     * Construct from spring classpath resource.
     * 
     * @param res
     *            the spring resource.
     */
    public ClasspathResource(final ClassPathResource res) {
        this(getNakedPath(res));
    }

    /**
     * Construct from full path with identifier.
     * 
     * @param path
     *            the full path
     */
    public ClasspathResource(final String path) {
        // very important to use unique the same, because the super class's
        // equals() and hashCode() depends on it.
        super(PREFIX + path);
        rawPath = path;
    }

    /**
     * Extract the path without prefix.
     * 
     * @param res
     *            the spring resource
     * @return the path without prefix.
     */
    private static String getNakedPath(ClassPathResource res) {
        String path = res.getPath();
        String lowrCasePath = path.toLowerCase();
        if (lowrCasePath.startsWith(SPRING_PREFIX1)) {
            return path.substring(SPRING_PREFIX1.length());
        } else if (lowrCasePath.startsWith(SPRING_PREFIX2)) {
            return path.substring(SPRING_PREFIX2.length());
        }
        return path;
    }

    public String getRawPath() {
        return rawPath;
    }

}
