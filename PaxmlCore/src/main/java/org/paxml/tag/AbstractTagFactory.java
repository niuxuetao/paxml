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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Conditional;
import org.paxml.annotation.IdAttribute;
import org.paxml.control.AbstractControlTag;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.IEntityFactory;
import org.paxml.core.IParserContext;
import org.paxml.core.Namespaces;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.Condition;
import org.paxml.el.ExpressionFactory;
import org.paxml.tag.invoker.AbstractInvokerTag;
import org.paxml.tag.invoker.ExpressionTag;
import org.paxml.util.Attributes;
import org.paxml.util.AxiomUtils;
import org.paxml.util.Elements;
import org.paxml.util.ReflectUtils;

/**
 * This is the basic impl of a tag factory.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the type of tag this factory produces.
 */
public abstract class AbstractTagFactory<T extends ITag> implements ITagFactory<T> {
    /**
     * Attribute filter.
     * 
     * @author Xuetao Niu
     * 
     */
    public static interface IAttributeFilter {
        /**
         * Determines whether an attribute is visitable.
         * 
         * @param ele
         *            the owning element
         * @param attrName
         *            the attr name
         * @param attrValue
         *            the attr value
         * @return true to visit it, false not to.
         */
        boolean accept(OMElement ele, String attrName, String attrValue);
    }

    /**
     * Visitor for xml element attributes.
     * 
     * @author Xuetao Niu
     * 
     */
    public static interface IAttributeVisitor {
        /**
         * Event handler for visiting an attribute.
         * 
         * @param ele
         *            the containing element
         * @param attrName
         *            the attribute name
         * @param attrValue
         *            the attribute value
         */
        void visit(OMElement ele, String attrName, String attrValue);
    }

    private IEntityFactory entityFactory;

    /**
     * Visit all attributes of an xml element.
     * 
     * @param ele
     *            the element
     * @param visitor
     *            the visitor
     */
    public static void traverseAttributes(OMElement ele, IAttributeVisitor visitor) {
        for (OMAttribute attr : new Attributes(ele)) {
            visitor.visit(ele, attr.getLocalName(), attr.getAttributeValue());
        }
    }

    /**
     * Assert an xml element has exactly the given attributes, no more, no less.
     * 
     * @param ele
     *            the element
     * @param attrs
     *            the exact attributes
     */
    public static void assertExactAttributes(OMElement ele, String... attrs) {
        assertAttributes(ele, attrs);
        assertNoAttributes(ele, attrs);
    }

    /**
     * Assert an xml element has at least the given attributes.
     * 
     * @param ele
     *            the element
     * @param attrs
     *            the attributes
     */
    public static void assertAttributes(OMElement ele, String... attrs) {
        for (String attr : attrs) {
            if (StringUtils.isBlank(AxiomUtils.getAttribute(ele, attr))) {
                throw new PaxmlRuntimeException("Attribute '" + attr + "' is required in tag: " + ele.getLocalName());
            }
        }
    }

    /**
     * Assert an xml element has no child nodes.
     * 
     * @param ele
     *            the element
     */
    public static void assertLeafElement(OMElement ele) {
        if (ele.getFirstOMChild() != null) {
            throw new PaxmlRuntimeException("Tag <" + ele.getLocalName() + "> should not have children tags nor text value");
        }
    }

    /**
     * Assert an xml element has no child elements.
     * 
     * @param ele
     *            the element
     */
    public static void assertNoSubElements(OMElement ele) {
        if (ele.getFirstElement() != null) {
            throw new PaxmlRuntimeException("Tag <" + ele.getLocalName() + "> should not have children tags");
        }
    }

    /**
     * Assert an xml element has no attributes except for some given ones.
     * 
     * @param ele
     *            the element
     * @param except
     *            the list of attributes that can exist
     */
    public static void assertNoAttributes(OMElement ele, String... except) {
        final Set<String> set = new HashSet<String>(Arrays.asList(except));
        traverseAttributes(ele, new IAttributeVisitor() {

            public void visit(OMElement ele, String attrName, String attrValue) {
                if (!set.contains(attrName)) {
                    throw new PaxmlRuntimeException("No attribute '" + attrName + "' should exist in tag: "
                            + ele.getLocalName());
                }
            }
        });
    }

    /**
     * Assert an xml element has no attributes except for some given ones and
     * the ones that are for conditional execution if the given tag impl is
     * annotated with @Conditional.
     * 
     * @param ele
     *            the element
     * @param tagImpl
     *            the tag impl class to search for @Conditional annotation where
     *            the conditional execution attributes are given
     * @param except
     *            the list of attributes that the element can have more than the
     *            conditional ones.
     */
    public static void assertNoAttributes(OMElement ele, Class<? extends ITag> tagImpl, String... except) {
        Conditional a = ReflectUtils.getAnnotation(tagImpl, Conditional.class);
        List<String> excepts = new ArrayList<String>(Arrays.asList(except));
        if (a != null) {
            if (!StringUtils.isBlank(a.ifAttribute())) {
                excepts.add(a.ifAttribute());
            }
            if (!StringUtils.isBlank(a.unlessAttribute())) {
                excepts.add(a.unlessAttribute());
            }
        }
        assertNoAttributes(ele, excepts.toArray(new String[excepts.size()]));
    }

    /**
     * Parse the conditional attributes.
     * 
     * @param ele
     *            the element
     * @param ifAttr
     *            the attribute name for "positive" test. Only if the evaluation
     *            result of this attribute's value is "true", the tag will be
     *            executed. If this parameter is null, then there is no
     *            "positive" test.
     * @param unlessAttr
     *            the attribute name for "negative" test. Only if the evaluation
     *            result of this atribure's value is not "true", the tag will be
     *            executed.
     * @return the conditional execution construct
     */
    public static Condition parseConditions(OMElement ele, String ifAttr, String unlessAttr) {

        final String ifCond = StringUtils.isBlank(ifAttr) ? null : AxiomUtils.getAttribute(ele, ifAttr);
        final String unlessCond = StringUtils.isBlank(unlessAttr) ? null : AxiomUtils.getAttribute(ele, unlessAttr);

        Condition cond = null;
        if (!StringUtils.isBlank(ifCond)) {
            cond = new Condition();
            cond.setNegated(false);
            cond.setExpression(ExpressionFactory.create(ifCond));
        }
        if (!StringUtils.isBlank(unlessCond)) {
            if (cond != null) {
                throw new PaxmlRuntimeException("Cannot have both conditional attributes specified: " + ifAttr + ","
                        + unlessAttr);
            }
            cond = new Condition();
            cond.setNegated(true);
            cond.setExpression(ExpressionFactory.create(unlessCond));
        }
        return cond;
    }

    /**
     * Replace a node with another one.
     * 
     * @param oldNode
     *            old node
     * @param newNode
     *            new node
     * @return the old node that is detached
     */
    public static OMNode replaceNode(OMNode oldNode, OMNode newNode) {
        oldNode.insertSiblingAfter(newNode);
        return oldNode.detach();
    }

    /**
     * Create a data tag.
     * 
     * @param tagName
     *            the tag name
     * @param text
     *            the text of the tag
     * @param lineNumber
     *            the line number of the tag
     * @return the data tag, never null.
     */
    public static OMElement createDataTag(String tagName, String text, int lineNumber) {
        OMElement tag = AxiomUtils.getOMFactory().createOMElement(tagName, Namespaces.DATA, null);
        if (text != null) {
            tag.setText(text);
        }
        tag.setLineNumber(lineNumber);
        return tag;

    }

    /**
     * Enclose a tag with &lt;value&gt; as parent tag name.
     * 
     * @param container
     *            the tag's parent tag.
     * @param ele
     *            the tag, null to enclose nothing but only the text.
     * @param text
     *            the text to put under the the newly created &lt;value&gt; tag,
     *            null if no text is needed under the &lt;value&gt; tag.
     * @return the enclosing tag, never null. The line number will be the same
     *         as the ele if it is not null, otherwise, the line number will be
     *         the same as the container's. The &lt;value&gt; tag's position
     *         will be the same as the ele's if it is not null, if it is null,
     *         the position will be the last child of the container.
     */
    public static OMElement encloseWithValueTag(OMElement container, OMElement ele, String text) {
        OMElement valueEle = createDataTag(Context.DEFAULT_VALUE_NAME, text, ele == null ? container.getLineNumber()
                : ele.getLineNumber());

        if (ele != null) {
            valueEle.addChild(replaceNode(ele, valueEle));
        } else {
            container.addChild(valueEle);
        }
        return valueEle;
    }

    /**
     * Create an &lt;expression&gt; tag.
     * 
     * @param text
     *            the text of the tag, null for no text.
     * @param lineNumber
     *            the line number for the tag.
     * @return the tag, never null.
     */
    public static OMElement createExpressionTag(String text, int lineNumber) {
        OMElement tag = AxiomUtils.getOMFactory().createOMElement(ExpressionTag.TAG_NAME, Namespaces.COMMAND, null);
        if (text != null) {
            tag.setText(text);
        }
        tag.setLineNumber(lineNumber);
        return tag;

    }

    /**
     * Check if a tag is a const tag.
     * 
     * @param ele
     *            the tag
     * @param context
     *            the parser context
     * @return true if yes, false if not
     */
    public static boolean isConstTag(OMElement ele, IParserContext context) {
        Class<? extends ITag> tagClass = AbstractPaxmlEntityFactory.getTagClass(ele, context);
        return ConstTag.class.equals(tagClass);
    }

    /**
     * Check if a tag is a control tag.
     * 
     * @param ele
     *            the tag
     * @param context
     *            the parser context
     * @return true if yes, false if not
     */
    public static boolean isControlTag(OMElement ele, IParserContext context) {
        Class<? extends ITag> tagClass = AbstractPaxmlEntityFactory.getTagClass(ele, context);
        return ReflectUtils.isSubClass(tagClass, AbstractControlTag.class, false);
    }

    /**
     * Check if the tag is an invoker tag.
     * 
     * @param ele
     *            the tag
     * @param context
     *            the parser context
     * @return true if yes, false if not
     */
    public static boolean isInvokerTag(OMElement ele, IParserContext context) {
        Class<? extends ITag> tagClass = AbstractPaxmlEntityFactory.getTagClass(ele, context);
        return ReflectUtils.isSubClass(tagClass, AbstractInvokerTag.class, false);
    }

    /**
     * Check if a tag has sub const tags.
     * 
     * @param ele
     *            the tag
     * @param context
     *            the parser context
     * @param seeAlsoUnderControlTags
     *            true to also go through nested control tags
     * @return true if it has, false not.
     */
    public static boolean hasSubConstTag(OMElement ele, IParserContext context, boolean seeAlsoUnderControlTags) {
        Elements eles = new Elements(ele);
        for (OMElement child : eles) {
            if (isConstTag(child, context)) {
                return true;
            }
        }
        if (seeAlsoUnderControlTags) {
            for (OMElement child : eles) {
                if (isControlTag(child, context)) {
                    return hasSubConstTag(child, context, seeAlsoUnderControlTags);
                }
            }
        }
        return false;
    }

    /**
     * Check if a tag has sub invoker tags.
     * 
     * @param ele
     *            the tag
     * @param context
     *            the parser context
     * @param seeAlsoUnderControlTags
     *            true to also go through nested control tags
     * @return true if it has, false not.
     */
    public static boolean hasSubInvokerTag(OMElement ele, IParserContext context, boolean seeAlsoUnderControlTags) {
        Elements eles = new Elements(ele);
        for (OMElement child : eles) {
            if (isInvokerTag(child, context)) {
                return true;
            }
        }
        if (seeAlsoUnderControlTags) {
            for (OMElement child : eles) {
                if (isInvokerTag(child, context)) {
                    return hasSubInvokerTag(child, context, seeAlsoUnderControlTags);
                }
            }
        }
        return false;
    }

    /**
     * Check if a tag has sub control tags.
     * 
     * @param ele
     *            the tag
     * @param context
     *            the parser context
     * @return true if yes, false not
     */
    public static boolean hasSubControlTag(OMElement ele, IParserContext context) {

        for (OMElement child : new Elements(ele)) {

            if (isControlTag(child, context)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a tag is located under the entity possibly under nested control
     * tags, but not under any other types of tags.
     * 
     * @param tag
     *            the tag
     * @return true if yes, false if no
     */
    public static boolean isLocalConst(ITag tag) {
        ITag parent = tag.getParent();
        if (parent instanceof IEntity) {
            return true;
        }
        ITag ancestor = parent;
        while (ancestor instanceof AbstractControlTag) {
            ancestor = ancestor.getParent();
        }
        return ancestor instanceof IEntity;
    }
    /**
     * Check if a tag is under a const tag.
     * @param tag the tag
     * @return true yes, false no
     */
    public static boolean isUnderConst(ITag tag) {
        ITag parent = tag.getParent();
        if (parent instanceof ConstTag) {
            return true;
        }
        ITag ancestor = parent;
        while (ancestor instanceof AbstractControlTag) {
            ancestor = ancestor.getParent();
        }
        return ancestor instanceof ConstTag;
    }

    /**
     * Process a tag, assigning values to a tag's properties according to
     * attributes and possibly children tags.
     * 
     * @param tag
     *            the constructed tag object
     * @param context
     *            the parse context
     * @return true to let framework ignore children tags so that the tag
     *         factory will parse the children tags itself, false not to ignore.
     */
    protected boolean populate(T tag, IParserContext context) {

        processIdExpression(tag, context);
        processConditional(tag, context);
        processExpressions(tag, context);

        return false;

    }

    protected void processIdExpression(T tag, IParserContext context) {
        IdAttribute a = ReflectUtils.getAnnotation(tag.getClass(), IdAttribute.class);
        if (a != null) {
            final String id = a.value();
            if (IdAttribute.NO_ID_SUPPORT.equals(id)) {
                return;
            }

            OMElement ele = context.getElement();
            QName qn = new QName(Namespaces.ROOT, id);
            String attr = ele.getAttributeValue(qn);
            if (attr == null) {
                qn = new QName("", id);
                attr = ele.getAttributeValue(qn);
                if (attr != null && StringUtils.isBlank(attr)) {
                    throw new PaxmlRuntimeException("Id attribute cannot be given as blank: " + id);
                }
            } else if (StringUtils.isBlank(attr)) {
                throw new PaxmlRuntimeException("Id attribute cannot be given as blank: " + qn);
            }
            if (attr != null) {
                tag.setIdExpression(new IdExpression(ExpressionFactory.create(attr), qn));
            }
        }
    }

    protected void processConditional(T tag, IParserContext context) {
        Conditional a = ReflectUtils.getAnnotation(tag.getClass(), Conditional.class);

        if (a != null) {
            tag.setResourceLocator(context.getLocator());
            tag.setFactory(this);
            tag.setCondition(parseConditions(context.getElement(), a.ifAttribute(), a.unlessAttribute()));
        }
    }

    static void processExpressions(ITag tag, IParserContext context) {
        if (!(tag instanceof ExpressionTag)) {
            // make sure all text nodes are converted to <expression> tags
            OMElement ele = context.getElement();

            for (OMNode child : AxiomUtils.getNodes(ele)) {
                if (child.getType() == OMNode.TEXT_NODE) {
                    OMText textNode = (OMText) child;
                    String text = textNode.getText();

                    if (StringUtils.isNotBlank(text)) {
                        OMElement expTag = createExpressionTag(text, ele.getLineNumber());
                        child.insertSiblingAfter(expTag);
                        child.detach();
                    }
                }
            }
        }
    }

    /**
     * Construct a tag object from a tag impl class.
     * 
     * @param impl
     *            the tag impl class
     * @param context
     *            the parse context
     * @return the tag object, never null.
     */
    protected T constructObject(Class<? extends T> impl, IParserContext context) {
        try {
            return impl.newInstance();
        } catch (Exception e) {
            throw new PaxmlRuntimeException("Cannot parse tag <" + context.getElement().getLocalName() + ">", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public TagCreationResult<T> create(Class<? extends T> impl, IParserContext context) {

        T tag = constructObject(impl, context);
        tag.setTagName(context.getElement().getLocalName());
        tag.setLineNumber(context.getElement().getLineNumber());
        tag.setResource(context.getResource());
        tag.setEntity(context.getEntity());
        tag.setParent(context.getParentTag());
        context.getParentTag().addChild(tag);
        
        if (tag instanceof AbstractTag) {
            ((AbstractTag) tag).setFactory(this);
            ((AbstractTag) tag).setXmlElement(context.getElement());
        }
        final boolean childrenParsed = populate(tag, context);
        return new TagCreationResult<T>(tag, childrenParsed);

    }

    public IEntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(IEntityFactory factory) {
        entityFactory = factory;
    }

    protected AbstractPaxmlEntityFactory getAbstractEntityFactory() {

        return (AbstractPaxmlEntityFactory) entityFactory;

    }
}
