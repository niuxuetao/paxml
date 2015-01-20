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

import java.util.Iterator;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.paxml.core.IParserContext;
import org.paxml.el.ExpressionFactory;
import org.paxml.el.UtilFunctions;

/**
 * Literal tag factory impl.
 * 
 * @author Xuetao Niu
 * 
 */

public class LiteralTagFactory extends AbstractTagFactory<LiteralTag> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final LiteralTag tag, IParserContext context) {
        processIdExpression(tag, context);
        // parse all inner content to make it expression
        String xml = getInnerXml(context.getElement());
        tag.setExp(ExpressionFactory.create(xml));
        // do not do further processing
        return true;
    }
    private String getInnerXml(OMElement ele) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<OMNode> it = ele.getChildren(); it.hasNext();) {
            OMNode node = it.next();
            if (node.getType() == OMNode.TEXT_NODE) {
                sb.append(((OMText) node).getText());
            } else if (node.getType() == OMNode.COMMENT_NODE) {
                sb.append("<!--").append(((OMComment) node).getValue()).append("-->");
            } else {
                sb.append(node.toString());
            }
        }
        return sb.toString();
    }
}
