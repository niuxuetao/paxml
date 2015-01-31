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

import java.beans.PropertyDescriptor;

import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.ReflectUtils;
import org.paxml.util.ReflectUtils.PropertyDescriptorType;

/**
 * Tag to create a java object, non conditional!
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "bean")
public class BeanCreationTag extends BeanTag {
    
    /**
     * The attribute for the java class.
     */
    public static final String CLASS = "class";
    /**
     * The attribute for constructor args.
     */
    public static final String CONSTRUCTOR_ARG = "constructor-arg";
        
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        String type = (String) context.getConst(CLASS, true);
        if (StringUtils.isBlank(type)) {
            throw new PaxmlRuntimeException("No '" + CLASS + "' attribute given!");
        }
        // process the constructor
        Object obj = null;
        Object value = context.getConst(CONSTRUCTOR_ARG, false);
        if (value == null) {
            obj = ReflectUtils.createObject(type.trim(), null);
        } else {
            obj = ReflectUtils.createObject(type.trim(), null, ReflectUtils.getList(value).toArray());
        }
                
        // process the properties
        for (PropertyDescriptor pd : ReflectUtils.getPropertyDescriptors(obj.getClass(), PropertyDescriptorType.SETTER)) {
        	
            String pname = pd.getName();
            if (!CLASS.equals(pname)) {
                Object pvalue = context.getConst(pname, false);
                if (pvalue != null) {
                	
                    ReflectUtils.callSetter(obj, pd, pvalue);
                }
            }

        }
        return obj;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean strictOnPropertyNames(Context context) {
        return false;
    }

}
