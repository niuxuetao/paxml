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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.beanutils.BeanUtils;
import org.paxml.core.Context;
import org.paxml.core.Namespaces;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.IdExpression;
import org.paxml.tag.invoker.AbstractInvokerTag;
import org.paxml.util.ReflectUtils;

/**
 * Base tag impl for a bean tag whose properties are settable directly from xml.
 * For example, if an invoker tag has attribute "abc" or sub element "abc", the
 * property "abc" of such a object will be set to the given value.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class BeanTag extends AbstractInvokerTag {
    /**
     * Property value visitor.
     * 
     * @author Xuetao Niu
     * 
     */
    private interface IPropertyValueReader {
        Object getValue(String name);
    }

    private final List<PropertyDescriptor> settableProperties = findSettableProperties(getClass());

    private Object value;

    static List<PropertyDescriptor> findSettableProperties(Class<? extends BeanTag> clazz) {
        List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>(0);
        for (PropertyDescriptor pd : net.sf.cglib.core.ReflectUtils.getBeanSetters(clazz)) {
            Method setter = pd.getWriteMethod();
            Class<?> ownerClass = setter.getDeclaringClass();
            if (BeanTag.class.equals(ownerClass) || ReflectUtils.isSubClass(ownerClass, BeanTag.class, true)) {
                list.add(pd);
            }
        }
        return list;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Actually do the invocation.
     * 
     * @param context
     *            the execution context
     * @return the invocation result
     * @throws Exception
     *             any exception
     */
    protected abstract Object doInvoke(Context context) throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Object invoke(final Context context) throws Exception {
        // cache the initial properties
        final Map<String, Object> initialProperties = new HashMap<String, Object>(settableProperties.size());
        for (PropertyDescriptor pd : settableProperties) {
            initialProperties.put(pd.getName(), getPropertyValue(pd.getName()));
        }
        if (strictOnPropertyNames(context)) {
            assertNoExcessiveParameters(initialProperties.keySet(), context);
        }
        preValueInjection();

        // put the properties from context
        populateProperties(false, new IPropertyValueReader() {

            public Object getValue(String name) {
                Object obj = context.getConst(name, true);
                return obj;
            }

        });

        afterPropertiesInjection(context);

        Object result = doInvoke(context);

        // restore the properties
        populateProperties(true, new IPropertyValueReader() {

            public Object getValue(String name) {
                return initialProperties.get(name);
            }
        });

        return result;
    }

    /**
     * Flag if the given params must match the property names.
     * 
     * @param context
     *            the context
     * @return true means must match, false means doesn't have to match.
     */
    protected boolean strictOnPropertyNames(Context context) {
        return true;
    }

    private void assertNoExcessiveParameters(Set<String> settablePropNames, Context context) {

        Set<String> ids = new HashSet<String>(context.getConstIds());
        ids.removeAll(settablePropNames);
        IdExpression idExp = getIdExpression();
        if (idExp != null) {
            // only remove the id if given with default namespace
            QName qn = idExp.getAttribute();
            if (!Namespaces.ROOT.equals(qn.getNamespaceURI())) {
                ids.remove(qn.getLocalPart());
            }
        }
        if (ids.size() > 0) {
            throw new PaxmlRuntimeException("Unsupported parameter(s) passed: " + ids + ", the acceptable parameters are: "
                    + settablePropNames);
        }
    }

    private void populateProperties(boolean force, IPropertyValueReader reader) {
        for (PropertyDescriptor pd : settableProperties) {

            Method setter = pd.getWriteMethod();
            Class<?> argType = setter.getParameterTypes()[0];

            final Object pvalue = reader.getValue(pd.getName());
            boolean doIt = true;
            Object targetValue = null;
            if (pvalue == null) {
                if (argType.isPrimitive()) {
                    doIt = false;
                } else {
                    doIt = force;
                }
            } else {
                targetValue = pvalue;
            }
            if (doIt) {
                ReflectUtils.callSetter(this, pd, targetValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> inspectAttributes() {
        Map<String, Object> map = super.inspectAttributes();
        if (map == null) {
            map = new LinkedHashMap<String, Object>();
        }
        for (PropertyDescriptor pd : settableProperties) {
            map.put(pd.getName(), getPropertyValue(pd.getName()));
        }
        return map;
    }

    private Object getPropertyValue(String pname) {
        try {
            return BeanUtils.getProperty(this, pname);
        } catch (NoSuchMethodException e) {
            throw new PaxmlRuntimeException("Propery '" + pname + "' has no getter in class: " + getClass().getName(), e);
        } catch (Exception e) {
            throw new PaxmlRuntimeException("Cannot get value for property '" + pname + "' from class: "
                    + getClass().getName(), e);
        }
    }

    /**
     * Handler called before all value injections occur.
     */
    protected void preValueInjection() {
        // do nothing here
    }

    /**
     * Handler called after all value injections occur.
     * @param Context the context
     */
    protected void afterPropertiesInjection(Context context) {
        // do nothing here
    }
}
