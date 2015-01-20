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
package org.paxml.tag;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.annotation.Util;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.IUtilFunctionsFactory;

/**
 * Default impl of tag library.
 * 
 * @author Xuetao Niu
 * 
 */
public class DefaultTagLibrary implements ITagLibrary {
    private static final Log log = LogFactory.getLog(DefaultTagLibrary.class);
    private final Map<String, Class<? extends IUtilFunctionsFactory>> utils = 
        new ConcurrentHashMap<String, Class<? extends IUtilFunctionsFactory>>();
    private final Map<String, Class<? extends ITag>> tags = 
        new ConcurrentHashMap<String, Class<? extends ITag>>(0);

    /**
     * {@inheritDoc}
     */
    public Class<? extends ITag> getTagImpl(String tagName) {
        return tags.get(tagName);
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends IUtilFunctionsFactory> getUtilFunctionsFactory(String name) {
        return utils.get(name);
    }

    /**
     * {@inheritDoc}
     */
    public void registerTag(Class<? extends ITag> clazz) {
        String tagName = getTagName(clazz);
        if (StringUtils.isBlank(tagName)) {
            throw new PaxmlRuntimeException("Class " + clazz.getName()
                    + " has no 'name' property given in its own nor its super classes/interfaces' @"
                    + Tag.class.getSimpleName() + " annotation");
        }
        tags.put(tagName, clazz);
    }

    /**
     * {@inheritDoc}
     */
    public void registerUtil(Class<? extends IUtilFunctionsFactory> clazz) {
        String utilName = getUtilName(clazz);
        if (StringUtils.isBlank(utilName)) {
            throw new PaxmlRuntimeException("Class " + clazz.getName() + " has no value given in the @"
                    + Util.class.getSimpleName() + " annotation.");
        }
        Class<? extends IUtilFunctionsFactory> existing = utils.put(utilName, clazz);
        if (existing != null) {
            log.warn("Util functions named " + utilName + " from class " + existing.getName()
                    + " is overridded by another one from class " + clazz.getName());
        }
    }

    private static String getUtilName(Class<? extends IUtilFunctionsFactory> clazz) {
        Util a = clazz.getAnnotation(Util.class);
        if (a != null) {
            return a.value();
        }
        return null;
    }

    private static String getTagName(Class<? extends ITag> clazz) {
        Tag a = clazz.getAnnotation(Tag.class);
        if (a != null) {
            return a.name();
        }
        return null;
    }

    public Set<String> getUtilFunctionsFactoryNames() {
        return Collections.unmodifiableSet(utils.keySet());
    }
    
}
