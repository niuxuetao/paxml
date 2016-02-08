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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.RootTag;
import org.paxml.core.ClasspathResource;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.IEntityFactory;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.DataSetEntityFactory.DataSet;
import org.paxml.util.ReflectUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Impl for spring bean xml entities.
 * 
 * @author Xuetao Niu
 * 
 */
@RootTag(SpringXmlEntityFactory.TAG_NAME)
public class SpringXmlEntityFactory implements IEntityFactory {
    /**
     * The delimiter between values.
     */
    public static final String DELEMITER = ",";
    /**
     * Root tag name.
     */
    public static final String TAG_NAME = "beans";
    /**
     * The attribute to specify a specific set of ids of the beans to load.
     */
    public static final String INCLUDE = "include";
    /**
     * The attribute to specify a specific set of ids of the beans not to load.
     */
    public static final String EXCLUDE = "exclude";

    /**
     * The attribute to specify a specific set of types of the beans to load.
     */
    public static final String INCLUDE_TYPE = "includeType";
    /**
     * The attribute to specify a specific set of types of the beans not to
     * load.
     */
    public static final String EXCLUDE_TYPE = "excludeType";

    private static final Log log = LogFactory.getLog(SpringXmlEntityFactory.class);

    // NB! Spring guarantees that a ApplicationContext itself is thread safe.
    private static final ConcurrentMap<PaxmlResource, ApplicationContext> FACTORY_CACHE = 
        new ConcurrentHashMap<PaxmlResource, ApplicationContext>();

    /**
     * Impl for spring beans entity.
     * 
     * @author Xuetao Niu
     * 
     */
    public static final class BeanXml extends DataSet {
        private final PaxmlResource targetResource;

        private BeanXml(final PaxmlResource targetResource) {
            super();
            this.targetResource = targetResource;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Map<String, String> getDataNameMap(Context context, Map<String, Object> dataMap) {
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                Object value = entry.getValue();
                map.put(entry.getKey(), value == null ? null : value.getClass().getName());
            }
            return map;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Map<String, Object> getDataMap(Context context) {

            final ApplicationContext factory = getApplicationContext(targetResource);

            Map<String, Object> map = new LinkedHashMap<String, Object>();

            Object value = context.getDefaultParameter();
            Map<String, String> idMap = new LinkedHashMap<String, String>();

            if (value instanceof Map) {
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                    Object k = entry.getKey();
                    Object v = entry.getValue();
                    if (v == null) {
                        v = k;
                    }
                    idMap.put(k.toString(), v.toString());
                }
            } else {
                for (String part : parseDelimitedString(value, null)) {
                    final int pos = part.indexOf('=');
                    if (pos < 0) {
                        throw new PaxmlRuntimeException("No '=' sign found for id mapping from line: " + part);
                    }
                    idMap.put(part.substring(0, pos), part.substring(pos + 1));
                }

            }
            Set<String> exclude = new LinkedHashSet<String>(parseDelimitedString(
                    context.getConst(EXCLUDE, false), DELEMITER));
            Set<String> include = new LinkedHashSet<String>(parseDelimitedString(
                    context.getConst(INCLUDE, false), DELEMITER));

            Set<String> excludeType = new HashSet<String>(parseDelimitedString(context.getConst(EXCLUDE_TYPE, false),
                    DELEMITER));
            Set<String> includeType = new HashSet<String>(parseDelimitedString(context.getConst(INCLUDE_TYPE, false),
                    DELEMITER));
                        
            if (include.size() <= 0 && includeType.size() <= 0) {
                include.addAll(Arrays.asList(factory.getBeanDefinitionNames()));
            }

            for (String className : includeType) {
                for (String id : factory.getBeanNamesForType(ReflectUtils.loadClassStrict(className, null))) {
                    include.add(id);
                }
            }
            //System.err.print(includeType+"="+);
            for (String className : excludeType) {
                for (String id : factory.getBeanNamesForType(ReflectUtils.loadClassStrict(className, null))) {
                    exclude.add(id);
                }
            }
                        
            include.removeAll(exclude);

            for (String name : include) {

                Object bean = factory.getBean(name);
                if (bean != null) {
                    String id = idMap.get(name);
                    if (id == null) {
                        id = name;
                    }
                    map.put(id, bean);
                }
            }
            
            return map;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected Object doExecute(Context context) {
            
            super.doExecute(context);
            
            return getApplicationContext(targetResource);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String printTree(int indent) {
            InputStream in = getResource().openInputStream();
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy(in, out);
                return out.toString("UTF-8");
            } catch (Exception e) {
                throw new PaxmlRuntimeException("Cannot inspect paxml resource: " + getResource(), e);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public IEntity create(OMElement root, IParserContext context) {
        BeanXml entity = new BeanXml(context.getResource());
        entity.setEntity(entity);
        entity.setResource(context.getResource());
        entity.setResourceLocator(context.getLocator());
        entity.setTagName(TAG_NAME);

        return entity;
    }

    /**
     * Get the cached application context with paxml resource.
     * 
     * @param targetResource
     *            the paxml resource pointing to a spring bean xml file.
     * @return the application context.
     */
    public static ApplicationContext getApplicationContext(PaxmlResource targetResource) {
        if (log.isInfoEnabled()) {
            log.debug("Requesting spring resource: " + targetResource.getPath());
        }
        ApplicationContext factory = FACTORY_CACHE.get(targetResource);
        if (factory == null) {
            if (log.isInfoEnabled()) {
                log.info("Loading spring xml: " + targetResource.getPath());
            }
            if (targetResource instanceof ClasspathResource) {
                factory = new ClassPathXmlApplicationContext(targetResource.getPath());
            } else {
                factory = new FileSystemXmlApplicationContext(targetResource.getPath());
            }
            ApplicationContext existing = FACTORY_CACHE.putIfAbsent(targetResource, factory);
            if (existing != null) {
                factory = existing;
            }
        }
        return factory;
    }
}
