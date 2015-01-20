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
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Conditional;
import org.paxml.core.IParserContext;
import org.paxml.util.ReflectUtils;

/**
 * Default impl for leaf tags. A leaf tag is a tag that has no sub elements and
 * no attributes except for the conditional ones.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the type of tag.
 */
public class LeafTagFactory<T extends ITag> extends DefaultTagFactory<T> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final T tag, IParserContext context) {

        final OMElement ele = context.getElement();

        assertNoSubElements(ele);

        Conditional a = ReflectUtils.getAnnotation(tag.getClass(), Conditional.class);
        if (a != null) {
            List<String> conditionalAttrs = new ArrayList<String>(2);
            if (StringUtils.isNotBlank(a.ifAttribute())) {
                conditionalAttrs.add(a.ifAttribute());
            }
            if (StringUtils.isNotBlank(a.unlessAttribute())) {
                conditionalAttrs.add(a.unlessAttribute());
            }
            assertNoAttributes(ele, conditionalAttrs.toArray(new String[conditionalAttrs.size()]));
        } else {
            assertNoAttributes(ele);
        }

        super.populate(tag, context);

        return false;
    }
}
