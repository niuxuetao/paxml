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
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * paxml resource locator.
 * 
 * @author Xuetao Niu
 * 
 */
public class ResourceLocator {
    private static final Log log = LogFactory.getLog(ResourceLocator.class);

    private final Map<PaxmlResource, IEntity> cachedPaxmlEntities = new ConcurrentHashMap<PaxmlResource, IEntity>(
            0);
    private final Map<String, PaxmlResource> resources = new ConcurrentHashMap<String, PaxmlResource>();

    private Parser parser;

    /**
     * Default constructor.
     */
    public ResourceLocator() {
        this(null);
    }

    /**
     * Construct from an existing parent.
     * 
     * @param parent
     *            the parent, null if no parent.
     */
    public ResourceLocator(final ResourceLocator parent) {
        if (parent != null) {
            cachedPaxmlEntities.putAll(parent.cachedPaxmlEntities);
            resources.putAll(parent.resources);
            parser = parent.parser;
        }
    }

    /**
     * Find all resources with a spring path pattern, from the added resources.
     * 
     * @param springPattern
     *            the spring path pattern.
     * @param baseFile
     *            the file used to resolve relative path with, can be null if
     *            the pattern is not relative.
     * @return all resource found, never null.
     */
    public static Set<PaxmlResource> findResources(String springPattern, Resource baseFile) {

        springPattern = springPattern.trim();

        if (StringUtils.isBlank(springPattern)) {
            throw new PaxmlRuntimeException("Cannot have empty file pattern!");
        }

        Set<PaxmlResource> set = new LinkedHashSet<PaxmlResource>();
        if (!springPattern.startsWith("file:") && !springPattern.startsWith("classpath:")
                && !springPattern.startsWith("classpath*:")) {
            springPattern = getRelativeResource(baseFile, springPattern);
        }
        if (log.isInfoEnabled()) {
            log.info("Searching for paxml resource with pattern: " + springPattern);
        }
        try {
            for (Resource res : new PathMatchingResourcePatternResolver().getResources(springPattern)) {

                if (res instanceof ClassPathResource) {
                    set.add(new ClasspathResource((ClassPathResource) res));
                } else if (res instanceof UrlResource && res.getURI().toString().startsWith("jar:")) {

                    set.add(new ClasspathResource(new ClassPathResource(StringUtils.substringAfterLast(res.getURI()
                            .toString(), "!"))));

                } else {
                    try {
                        File file = res.getFile();
                        if (file.isFile()) {
                            set.add(new FileSystemResource(file));
                        }
                    } catch (IOException e) {
                        throw new PaxmlRuntimeException("Unsupported spring resource: " + res.getURI() + ", of type: "
                                + res.getClass().getName());
                    }
                }
            }
        } catch (IOException e) {
            throw new PaxmlRuntimeException("Cannot find resources with spring pattern: " + springPattern, e);
        }
        return set;
    }

    /**
     * Make an Spring resource string from relative path which contains
     * wildcards.
     * 
     * @param base
     *            the base Spring resource
     * @param relative
     *            the relative path
     * @return the absolute spring resource path
     */
    public static String getRelativeResource(final Resource base, final String relative) {
        String result;
        // find from both class path and file system
        final boolean root = relative.startsWith("/") || relative.startsWith("\\");

        try {
            URL url = base.getURL();

            if (root) {
                result = url.getProtocol() + ":" + relative;
            } else {
                String path = FilenameUtils.getFullPathNoEndSeparator(url.getFile());
                result = url.getProtocol() + ":" + path + "/" + relative;
            }

        } catch (IOException e) {
            throw new PaxmlRuntimeException("Cannot get the relative path for plan file: " + base, e);
        }
        return result;
    }

    public Map<PaxmlResource, IEntity> getCachedPaxmlEntities() {
        return cachedPaxmlEntities;
    }

    public Map<String, PaxmlResource> getResourceMap() {
        return resources;
    }

    public Set<String> getResourceNames() {
        return resources.keySet();
    }

    /**
     * Get entity from cache, if not found, call parser to construct the entity.
     * 
     * @param name
     *            the name of the resource.
     * @param context
     *            the parser context, set to null if not known.
     * @return the entity, null if not found or unknown how to parse.
     */
    public IEntity getEntity(String name, IParserContext context) {
        PaxmlResource res = getResource(name);
        if (res == null) {
            throw new PaxmlRuntimeException("No resource found for paxml: " + name);
        }

        return parser.parse(res, false, context);
    }

    void setParser(Parser parser) {
        this.parser = parser;
    }

    public Parser getParser() {
        return parser;
    }

    /**
     * Add a resource.
     * 
     * @param resourceToAdd
     *            the resource
     */
    public void addResource(PaxmlResource resourceToAdd) {
        resources.put(resourceToAdd.getName(), resourceToAdd);
    }

    /**
     * Remove a resource.
     * 
     * @param resourceToRemove
     *            the resource
     */
    public void removeResource(PaxmlResource resourceToRemove) {
        resources.remove(resourceToRemove.getName());
    }

    /**
     * Add a collection of resources.
     * 
     * @param resourcesToAdd
     *            the resource collection.
     */
    public void addResources(Collection<PaxmlResource> resourcesToAdd) {
        for (PaxmlResource res : resourcesToAdd) {
            addResource(res);
        }
    }

    /**
     * Find a set of resources filtered with the given name pattern. The scope
     * of the find are the added resources.
     * 
     * @param pattern
     *            the pattern on resource name, not on path.
     * @return the set, never null
     */
    public Set<PaxmlResource> filterResources(String pattern) {
        Set<PaxmlResource> set = new LinkedHashSet<PaxmlResource>(0);
        for (Map.Entry<String, PaxmlResource> entry : resources.entrySet()) {
            if (FilenameUtils.wildcardMatch(entry.getKey(), pattern)) {
                set.add(entry.getValue());
            }
        }
        return set;
    }

    /**
     * Get a added resource with a name.
     * 
     * @param name
     *            the resource name
     * @return the added resource, null if not added.
     */
    public PaxmlResource getResource(String name) {
        return resources.get(name);
    }
}
