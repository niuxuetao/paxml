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
package org.paxml.test;

import java.util.Properties;

import org.paxml.annotation.Util;
import org.paxml.core.Context;
import org.paxml.el.IUtilFunctionsFactory;


/**
 * The test util functions.
 * 
 * @author Xuetao Niu
 * 
 */
@Util("test")
public class MyUtilFunctions implements IUtilFunctionsFactory {
    /**
     * The private context keys.
     * 
     * @author Xuetao Niu
     * 
     */
    private static enum PrivateKeys {
        SERVERS, LISTENER
    }
   
    /**
     * {@inheritDoc}
     */
    public Object getUtilFunctions(Context context) {
        return this;
    }
    /**
     * {@inheritDoc}
     */
    public Class<?> getXpathUtilFunctions(Context context) {
        return null;
    }

    /**
     * Get the current thread context.
     * 
     * @return the context
     */
    public Context getContext() {
        return Context.getCurrentContext();
    }

    public static Properties getSystemProperties() {
        return System.getProperties();
    }
    
}
