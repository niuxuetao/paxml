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

import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;

/**
 * Date tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = BundleTag.TAG_NAME)
public class BundleTag extends PropertiesTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "bundle";

    private String basename;
    private String locale;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Properties loadProperties(Context context) {
        if (StringUtils.isBlank(basename)) {
            throw new PaxmlRuntimeException("The 'basename' is not given to a <" + TAG_NAME + "> tag.");
        }
        ResourceBundle bundle = ResourceBundle.getBundle(basename, parseLocale(context));
        Properties props = toProperties(bundle);
        return props;
    }

    private Locale parseLocale(Context context) {
        if (StringUtils.isNotBlank(locale)) {
            return parseLocale(locale);
        }
        Locale loc = context.getLocale();
        if (loc == null) {
            loc = Locale.getDefault();
        }
        return loc;

    }

    private Properties toProperties(ResourceBundle bundle) {
        Properties props = new Properties();
        Enumeration<String> en = bundle.getKeys();
        while (en.hasMoreElements()) {
            String key = en.nextElement();
            props.put(key, bundle.getObject(key));
        }
        return props;
    }

    public String getBasename() {
        return basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

}
