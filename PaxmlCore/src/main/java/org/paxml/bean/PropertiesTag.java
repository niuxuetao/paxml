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
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.ObjectList;
import org.paxml.core.ObjectTree;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.DefaultTagFactory;
import org.paxml.tag.ConstTag;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;

/**
 * Date tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = PropertiesTag.TAG_NAME)
public class PropertiesTag extends BeanTag {
    /**
     * The object tree for grouped properties. 
     * @author Xuetao Niu
     *
     */
    public static class PropertiesObjectTree extends ObjectTree {
        /**
         * {@inheritDoc}
         * @param map the map
         * 
         */
        public PropertiesObjectTree(final Map<?, ?> map) {
            super(null,map);
        }
        private PropertiesObjectTree() {
            super(null);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected ObjectTree emptyCopy() {
            return new PropertiesObjectTree();
        }
        
        
    }
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "properties";
    private static final Log log = LogFactory.getLog(PropertiesTag.class);
    private String file;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        final String id = getId(context);
        if (StringUtils.isNotBlank(id) && getParent() instanceof ConstTag) {
            throw new PaxmlRuntimeException("The 'id' attribute cannot be given to a <" + TAG_NAME
                    + "> tag if it is under a data tag.");
        }

        Properties props = loadProperties(context);

        if (props.size() <= 0) {
            if (log.isWarnEnabled()) {
                log.warn("Properties has no content loaded: " + context.getStack().getFirst());
            }
        }
        if (StringUtils.isBlank(id)) {
            final boolean group = DefaultTagFactory.isUnderConst(this);
            if (group) {
                return new PropertiesObjectTree(props);
            } else {
                Context c = context.getCurrentEntityContext();
                // flatten the properties
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    String key = entry.getKey().toString();
                    c.setConst(key, null, entry.getValue(), true);
                    c.addPropertyConstId(key);
                }
                return null;
            }
        } else {
            context.getCurrentEntityContext().addPropertyConstId(id);
            // always group it here
            return new PropertiesObjectTree(props);            

        }

    }

    /**
     * Load properties.
     * 
     * @param context
     *            the context
     * @return the loaded properties
     */
    protected Properties loadProperties(Context context) {
        Properties props = new Properties();

        // load from file
        if (StringUtils.isNotBlank(file)) {
            Resource res = PaxmlUtils.getResource(file, getEntity().getResource().getSpringResource());
            PaxmlUtils.loadProperties(props, res, null);
        }

        // load from children
        Object value = getValue();
        if (value != null) {
            if (value instanceof ObjectList) {
                for (Object item : (ObjectList) value) {
                    if (item != null) {
                        loadTextProperties(props, item.toString());
                    }
                }
            } else if (value instanceof ObjectTree) {
                for (Map.Entry<String, Object> entry : ((ObjectTree) value).entrySet()) {
                    Object v = entry.getValue();
                    if (v != null) {
                        props.put(entry.getKey(), v.toString());
                    }
                }
            } else {
                loadTextProperties(props, value.toString());
            }
        }

        return props;
    }

    private void loadTextProperties(Properties props, String text) {
        Properties loaded = new Properties();
        
        try {
            PaxmlUtils.loadProperties(loaded, true, new ByteArrayInputStream(text.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new PaxmlRuntimeException(e);
        }
        loaded = PaxmlUtils.trimProperties(loaded);
        
        props.putAll(loaded);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
