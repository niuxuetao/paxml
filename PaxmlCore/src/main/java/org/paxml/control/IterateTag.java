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
package org.paxml.control;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.cglib.core.ReflectUtils;

import org.apache.commons.jxpath.Pointer;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.IExpression;
import org.paxml.tag.IPropertyVisitor;
import org.paxml.tag.AbstractTag.ChildrenResultList;

/**
 * Iterate tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "iterate", factory = IterateTagFactory.class)
public class IterateTag extends AbstractControlTag implements IPropertyVisitor<Context, ChildrenResultList> {
    /**
     * The default const name of var.
     */
    public static final String DEFAULT_VAR = "var";
    /**
     * The default const name of the var name.
     */
    public static final String DEFAULT_VAR_NAME = "name";
    /**
     * The default const name of index.
     */
    public static final String DEFAULT_INDEX = "index";

    private IExpression list;
    private IExpression map;
    private IExpression xpath;
    private IExpression times;
    private IExpression bean;
    private IExpression values;
    private String varName = DEFAULT_VAR;
    private String indexVarName = DEFAULT_INDEX;
    private String varNameText = DEFAULT_VAR_NAME;

    private ChildrenResultList visitIterator(Context context, Iterator<?> it) {
        if (it == null) {
            return null;
        }
        ChildrenResultList list = null;
        int i = 0;
        while (it.hasNext()) {
            Object value = it.next();
            if (value instanceof Pointer) {
                value = ((Pointer) value).getValue();
            }
            if (value != null) {
                list = addAll(list, visit(context, it, i + "", i, value));
                i++;
            }
        }
        return list;
    }

    private ChildrenResultList visitIterable(Context context, Iterable<?> it) {
        return visitIterator(context, it.iterator());
    }

    private ChildrenResultList visitEnumeration(Context context, Enumeration<?> e) {
        if (e == null) {
            return null;
        }
        ChildrenResultList list = null;
        int i = 0;
        while (e.hasMoreElements()) {
            list = addAll(list, visit(context, e, i + "", i, e.nextElement()));
            i++;
        }
        return list;
    }

    private ChildrenResultList visitBean(Context context, Object bean, boolean readValue) {
        if (bean == null) {
            return null;
        }
        ChildrenResultList list = null;
        int i = 0;
        for (PropertyDescriptor d : ReflectUtils.getBeanGetters(bean.getClass())) {
            Method method = d.getReadMethod();
            Object value = null;
            if (readValue) {
                try {
                    value = method.invoke(bean);
                } catch (Exception e) {
                    throw new PaxmlRuntimeException("Cannot read property '" + d.getName() + "' from class: "
                            + bean.getClass().getName(), e);
                }
            }

            list = addAll(list, visit(context, bean, d.getName(), i++, value));
        }
        return list;
    }

    private ChildrenResultList visitMap(Context context, Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        ChildrenResultList list = null;
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            list = addAll(list, visit(context, map, entry.getKey(), i++, entry.getValue()));
        }
        return list;
    }

    private ChildrenResultList visitArray(Context context, Object array) {
        if (array == null) {
            return null;
        }
        ChildrenResultList list = null;
        final int len = Array.getLength(array);
        for (int i = 0; i < len; i++) {
            Object value = Array.get(array, i);
            list = addAll(list, visit(context, array, i + "", i, value));

        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doExecute(Context context) {
        try {

            if (list != null) {
                return iterateList(context, list.evaluate(context));
            } else if (map != null) {
                Object value = map.evaluate(context);
                if (value instanceof Map) {
                    return visitMap(context, (Map) value);
                } else if (value != null) {
                    return visit(context, value, null, 0, value);
                }
            } else if (bean != null) {
                Object overValue = bean.evaluate(context);
                return visitBean(context, overValue, true);
            } else if (xpath != null) {
                String exp = xpath.evaluateString(context);
                List<?> list = (List<?>) context.xpathSelect(exp, true);
                return iterateList(context, list);
            } else if (times != null) {
                String num = times.evaluateString(context);
                final long rounds;
                try {
                    rounds = (long) Double.parseDouble(num);
                } catch (Exception e) {
                    throw new PaxmlRuntimeException("The @" + IterateTagFactory.ATTR_TIMES + " attribute of tag <"
                            + getTagName() + "> is not a number: " + num);
                }
                ChildrenResultList list = null;
                for (int i = 0; i < rounds; i++) {
                    list = addAll(list, visit(context, rounds, null, i, i));
                }
                return list;
            } else if (values != null) {
                return iterateValues(context, values.evaluate(context));
            } else {
                throw new PaxmlRuntimeException("Nothing to iterate!!!");
            }
        } finally {
            context.setConstOverwritable(false);
        }
        return null;
    }

    private Object iterateValues(Context context, Object overValue) {

        if (overValue == null) {
            // do nothing
            return null;
        } else if (overValue instanceof Iterable) {
            return visitIterable(context, (Iterable<?>) overValue);
        } else if (overValue instanceof Iterator) {
            return visitIterator(context, (Iterator<?>) overValue);
        } else if (overValue instanceof Enumeration) {
            return visitEnumeration(context, (Enumeration<?>) overValue);
        } else if (overValue instanceof Map) {
            // iterate over the values of the map
            return visitMap(context, (Map<?, ?>) overValue);
        } else if (overValue.getClass().isArray()) {
            // iterate over the items of the array
            return visitArray(context, overValue);
        } else {
            return visit(context, overValue, null, 0, overValue);
        }
    }

    private Object iterateList(Context context, Object overValue) {

        if (overValue == null) {
            // do nothing
            return null;
        } else if (overValue instanceof Iterable) {
            return visitIterable(context, (Iterable<?>) overValue);
        } else if (overValue instanceof Iterator) {
            return visitIterator(context, (Iterator<?>) overValue);
        } else if (overValue instanceof Enumeration) {
            return visitEnumeration(context, (Enumeration<?>) overValue);
        } else if (overValue.getClass().isArray()) {
            // iterate over the items of the array
            return visitArray(context, overValue);
        } else {
            return visit(context, overValue, null, 0, overValue);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ChildrenResultList visit(Context context, Object obj, Object propertyName, int index, Object propertyValue) {
        // set the loop vars and remember the previous status
        final boolean varExists = context.hasConstId(varName, false);
        final Object oldVar = varExists ? context.removeConst(varName) : null;
        context.addConst(varName, varName, propertyValue, true);

        final boolean indexExists = context.hasConstId(indexVarName, false);
        final Object oldIndex = indexExists ? context.removeConst(indexVarName) : null;
        context.addConst(indexVarName, indexVarName, index, true);

        final boolean varNameExists = context.hasConstId(varNameText, false);
        final Object oldVarName = varNameExists ? context.removeConst(varNameText) : null;
        context.addConst(varNameText, varNameText, propertyName, true);

        ChildrenResultList list = executeChildren(context);

        // restore the previous status
        context.removeConst(varName);
        if (varExists) {
            context.addConst(varName, varName, oldVar, true);
        }

        context.removeConst(indexVarName);
        if (indexExists) {
            context.addConst(indexVarName, indexVarName, oldIndex, true);
        }

        context.removeConst(varNameText);
        if (varNameExists) {
            context.addConst(varNameText, varNameText, oldVarName, true);
        }
        return list;
    }

    private static ChildrenResultList addAll(ChildrenResultList to, Collection<Object> from) {

        if (from != null) {
            if (to == null) {
                to = new ChildrenResultList(from.size());
            }
            to.addAll(from);
        }
        return to;
    }

    public IExpression getList() {
        return list;
    }

    public void setList(IExpression list) {
        this.list = list;
    }

    public IExpression getMap() {
        return map;
    }

    public void setMap(IExpression map) {
        this.map = map;
    }

    public IExpression getXpath() {
        return xpath;
    }

    public void setXpath(IExpression xpath) {
        this.xpath = xpath;
    }

    public IExpression getTimes() {
        return times;
    }

    public void setTimes(IExpression times) {
        this.times = times;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getIndexVarName() {
        return indexVarName;
    }

    public void setIndexVarName(String indexVarName) {
        this.indexVarName = indexVarName;
    }

    public String getVarNameText() {
        return varNameText;
    }

    public void setVarNameText(String varNameText) {
        this.varNameText = varNameText;
    }

    public IExpression getBean() {
        return bean;
    }

    public void setBean(IExpression bean) {
        this.bean = bean;
    }

    public IExpression getValues() {
        return values;
    }

    public void setValues(IExpression values) {
        this.values = values;
    }

}
