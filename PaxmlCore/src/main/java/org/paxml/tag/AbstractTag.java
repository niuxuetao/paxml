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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axiom.om.OMElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.IdAttribute;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.Context.Stack;
import org.paxml.core.IEntity;
import org.paxml.core.ITagExecutionListener;
import org.paxml.core.PaxmlResource;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.core.ResourceLocator;
import org.paxml.el.Condition;
import org.paxml.el.ExpressionFactory;

/**
 * This is the most basic impl for all tags.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(factory = DefaultTagFactory.class)
@IdAttribute
public abstract class AbstractTag implements ITag {
    
    private static final Log log = LogFactory.getLog(AbstractTag.class);
    
    public static class ChildrenResultList extends ArrayList<Object> {
        public ChildrenResultList(int initSize) {
            super(initSize);
        }
    }

    private ITag parent;
    private final List<ITag> children = new ArrayList<ITag>(0);
    private String tagName;
    private int lineNumber;
    private ResourceLocator resourceLocator;
    private ITagFactory<? extends ITag> factory;
    private Condition condition;
    private PaxmlResource resource;
    private IEntity entity;
    private IdExpression idExpression;
    private OMElement xml;

    public OMElement getXmlElement() {
        return xml;
    }

    void setXmlElement(OMElement xml) {
        this.xml = xml;
    }

    /**
     * Parse the locale from the given string.
     * 
     * @param locale
     *            the locale string
     * @return the parsed locale
     */
    public static Locale parseLocale(String locale) {
        return org.springframework.util.StringUtils.parseLocaleString(locale);
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public void setResourceLocator(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Execute the tag with execution context.
     * 
     * @param context
     *            execution context
     * @return the execution result
     */
    public Object execute(Context context) {
        Stack stack = context.getStack();
        if (stack.isExiting() || (!stack.isEmpty() && context.getCurrentEntityContext().isReturning())) {
            return null;
        }

        context.pushStack(this);
        List<ITagExecutionListener> listeners = context.getTagExecutionListeners(false);
        try {
            if (listeners != null) {
                for (ITagExecutionListener listener : listeners) {
                    listener.onEntry(this, context);
                }
            }
            Object result = null;
            if (condition != null) {
                boolean ran = false;
                if (condition.isNegated()) {
                    if (!ExpressionFactory.isTrue(condition.getExpression().evaluate(context))) {
                        ran = true;
                        result = doExecute(context);

                    }
                } else {
                    if (ExpressionFactory.isTrue(condition.getExpression().evaluate(context))) {
                        ran = true;
                        result = executeAndPutResult(context);

                    }
                }
                if (!ran) {
                    onNotExecuted(context);
                }
            } else {

                result = executeAndPutResult(context);

            }
            context.popStack();
            return result;
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Exception in AbstractTag.execute.", e);
            }
            if (null == context.getExceptionContext()) {
                context.setExceptionContext(context);
            }
            throw new PaxmlRuntimeException(e);
        } finally {
            listeners = context.getTagExecutionListeners(false);
            if (listeners != null) {
                for (ITagExecutionListener listener : listeners) {
                    listener.onExit(this, context);
                }
            }

        }

    }

    private Object executeAndPutResult(Context context) throws Exception {

        Object result = doExecute(context);
        if (idExpression != null) {
            putResultAsConst(context, result);
        }
        return result;
    }

    /**
     * Put the result on context if id expression given. This will not be called
     * if the id expression is not given.
     * 
     * @param context
     *            the context
     * @param result
     *            the result of calling the doExecute(Context) method;
     */
    protected void putResultAsConst(Context context, Object result) {

        Context targetContext = context.findContextForEntity(getEntity());
        targetContext.setConst(idExpression.getId(targetContext), null, result, !context.isConstOverwritable());
    }

    /**
     * Do the actual execution work.
     * 
     * @param context
     *            the execution context
     * @return the execution result
     * @throws Exception
     *             any exception
     */
    protected abstract Object doExecute(Context context) throws Exception;

    /**
     * @return never null children list.
     */
    public List<ITag> getChildren() {
        return children;
    }

    /**
     * {@inheritDoc}
     */
    public void addChild(ITag tag) {
        children.add(tag);
    }

    /**
     * Call the execute() method of all children tags in order.
     * 
     * @param context
     *            the execution context.
     * @return the execution result.
     */
    protected ChildrenResultList executeChildren(Context context) {
        int index = 0;
        Stack stack = context.getStack();
        ChildrenResultList result = new ChildrenResultList(1);
        for (ITag tag : getChildren()) {
            if (stack.isExiting()) {
                return null;
            }
            if (!stack.isEmpty() && context.getCurrentEntityContext().isReturning()) {
                break;
            }
            result.add(executeChild(tag, index++, context));

        }
        return result;
    }

    /**
     * Execute a child tag. Override this method if subclass needs to intercept
     * child tag execution.
     * 
     * @param child
     *            the child tag
     * @param index
     *            the index of the child tag.
     * @param context
     *            the context
     * @return the result of executing a child tag.
     */
    protected Object executeChild(ITag child, int index, Context context) {
        return child.execute(context);
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * Event handler if the tag is not executed due to conditional attributes.
     * 
     * @param context
     *            the execution context
     */
    protected void onNotExecuted(Context context) {
        // do nothing in the base class
    }

    public ITag getParent() {
        return parent;
    }

    public void setParent(ITag parent) {
        this.parent = parent;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public ITagFactory<? extends ITag> getFactory() {
        return factory;
    }

    public void setFactory(ITagFactory<? extends ITag> factory) {
        this.factory = factory;
    }

    public PaxmlResource getResource() {
        return resource;
    }

    public void setResource(PaxmlResource resource) {
        this.resource = resource;
    }

    /**
     * Print the entire tree structure of the tag.
     * 
     * @param indent
     *            initial indentation which is number if tabs
     * @return the printed string
     */
    public String printTree(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("\t");
        }
        sb.append(toString()).append("\r\n");
        for (ITag child : getChildren()) {
            if (child instanceof AbstractTag) {
                sb.append(((AbstractTag) child).printTree(indent + 1));
            } else {
                sb.append(child);
            }
        }
        return sb.toString();
    }

    /**
     * Get the attributes' names and values to be made visible in print methods.
     * 
     * @return a map of attrubute's names and values, the map can be null,
     */
    protected Map<String, Object> inspectAttributes() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        PaxmlResource res = getDisplayResource();
        sb.append("<").append(getTagName()).append(":").append(getClass().getSimpleName()).append("> line ")
                .append(getLineNumber()).append(" of '").append(res.getName()).append("' [");
        Map<String, Object> attrs = inspectAttributes();
        if (attrs != null) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                if (entry.getValue() != null) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        sb.append("], resource: ").append(res.getPath());
        return sb.toString();
    }

    public IEntity getEntity() {
        return entity;
    }

    public void setEntity(IEntity entity) {
        this.entity = entity;
    }

    protected PaxmlResource getDisplayResource() {
        return entity.getResource();
    }

    public IdExpression getIdExpression() {
        return idExpression;
    }

    public void setIdExpression(IdExpression idExpression) {
        this.idExpression = idExpression;
    }

    /**
     * Get the id if id expression given.
     * 
     * @param context
     *            the context
     * @return the id if id expression given, null otherwise.
     */
    protected String getId(Context context) {
        if (idExpression == null) {
            return null;
        }
        return idExpression.getId(context);
    }

    /**
     * Break an object into string values.
     * 
     * @param obj
     *            the object which can be either a list, if given null, empty
     *            set will return.
     * @param delimiters
     *            the delimiters used to create StringTokenizer if the given
     *            object is a not a List, if given null, this delimiter set will
     *            be used: ", \r\n\t\f"
     * @return a ordered set of trimmed strings which contains no null nor blank
     *         string, never returns null List.
     */
    public static Set<String> parseDelimitedString(Object obj, String delimiters) {
        Set<String> set = new LinkedHashSet<String>(0);
        if (obj instanceof List) {
            for (Object item : (List) obj) {
                if (item != null) {
                    String str = item.toString().trim();
                    if (str.length() > 0) {
                        set.add(str);
                    }
                }
            }
        } else if (obj != null) {
            if (delimiters == null) {
                delimiters = ", \r\n\t\f";
            }
            StringTokenizer st = new StringTokenizer(obj.toString(), delimiters);
            while (st.hasMoreTokens()) {
                String part = st.nextToken().trim();
                if (part.length() > 0) {
                    set.add(part);
                }
            }
        }
        return set;
    }

}
