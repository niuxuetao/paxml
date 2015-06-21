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
package org.paxml.tag.invoker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.Conditional;
import org.paxml.core.IParserContext;
import org.paxml.tag.DefaultTagFactory;
import org.paxml.tag.ITag;
import org.paxml.util.AxiomUtils;
import org.paxml.util.ReflectUtils;

/**
 * Base invoker tag factory impl.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the invoker tag type that is being produced by the factory.
 */
public class InvokerTagFactory<T extends AbstractInvokerTag> extends DefaultTagFactory<T> {

    /**
     * Process the tag attributes and children.
     * @param tag the tag
     * @param context the parse context
     * @param filter the attribute filter
     */
    public static void processElement(ITag tag, IParserContext context, IAttributeFilter filter) {

        final OMElement ele = context.getElement();

        String text = ele.getText();
        if (StringUtils.isBlank(text)) {
            text = null;
        }

        pushAttributeToSubConst(ele, filter);

    }
    /**
     * Push the attributes to sub elements. 
     * @param ele the ele
     * @param filter the attr filter
     */
    public static void pushAttributeToSubConst(final OMElement ele, final IAttributeFilter filter) {

        // convert the attribute parameter specification into children node
        // specification
        traverseAttributes(ele, new IAttributeVisitor() {
            private OMElement firstPushedSub;

            public void visit(OMElement ele, String name, String value) {
                if (filter == null || filter.accept(ele, name, value)) {

                    OMElement constEle = createDataTag(name, value, ele.getLineNumber());

                    if (firstPushedSub == null) {

                        OMNode firstChild = ele.getFirstOMChild();
                        if (firstChild != null) {
                            firstChild.insertSiblingBefore(constEle);
                        } else {
                            ele.addChild(constEle);
                        }
                        firstPushedSub = constEle;
                    } else {
                        firstPushedSub.insertSiblingAfter(constEle);
                    }
                }
            }

        });

    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(T tag, IParserContext context) {

        super.populate(tag, context);

        final OMElement ele = context.getElement();

        final Set<String> ignore = getIgnoredAttributes(tag, context);

        processElement(tag, context, new IAttributeFilter() {

            public boolean accept(OMElement ele, String attrName, String attrValue) {
                return ignore == null || !ignore.contains(attrName);
            }
        });

        for (OMElement child : AxiomUtils.getElements(ele, null)) {
            if (isInvokerTag(child, context)) {
                encloseWithValueTag(ele, child, null);
            }
        }

        return false;
    }
    /**
     * Return ignored set of attributes.
     * @param tag the tag
     * @param context the context
     * @return the ignored attr name set, or null.
     */
    protected Set<String> getIgnoredAttributes(T tag, IParserContext context) {
        Conditional cond = ReflectUtils.getAnnotation(tag.getClass(), Conditional.class);
        if (cond == null) {
            return null;
        }
        return new HashSet<String>(Arrays.asList(cond.ifAttribute(), cond.unlessAttribute()));
    }
}
