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

import java.util.List;

import org.apache.axiom.om.OMElement;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.DefaultTagFactory;
import org.paxml.tag.ITag;

/**
 * Else tag factory.
 * 
 * @author Xuetao Niu
 * 
 */
public class ElseTagFactory extends AbstractControlTagFactory<ElseTag> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final ElseTag tag, IParserContext context) {

        super.populate(tag, context);

        final OMElement ele = context.getElement();
        final ITag parent = context.getParentTag();

        assertNoAttributes(ele, IfTag.TEST_ATTR);

        // add to the previous <if> tag
        int ifIndex = -1;
        int myIndex = -1;
        List<ITag> siblings = parent.getChildren();

        for (int i = siblings.size() - 1; i >= 0; i--) {
            ITag sibling = siblings.get(i);
            if (tag == sibling) {
                myIndex = i;
            } else if (myIndex >= 0 && (sibling instanceof IfTag || sibling instanceof ElseTag)) {
                ifIndex = i;
                break;
            }
        }
        if (ifIndex < 0) {
            throw new PaxmlRuntimeException("No <" + IfTag.TAG_NAME + "> nor <" + ElseTag.TAG_NAME
                    + "> tag found before the <" + ele.getLocalName() + "> tag");
        }
        ITag prev = siblings.get(ifIndex);
        if (myIndex - ifIndex != 1 || (prev instanceof ElseTag && ((ElseTag) prev).getCondition() == null)) {
            throw new PaxmlRuntimeException("The <" + ele.getLocalName() + "> tag should follow either a <"
                    + IfTag.TAG_NAME + "> tag or a <" + ElseTag.TAG_NAME + "> tag that has a @" + IfTag.TEST_ATTR
                    + " attribute");
        }

        return false;
    }
}
