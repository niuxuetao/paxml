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

import org.apache.axiom.om.OMElement;
import org.paxml.core.IParserContext;
import org.paxml.tag.AbstractTagFactory;
/**
 * If tag factory.
 * @author Xuetao Niu
 *
 */
public class IfTagFactory extends AbstractTagFactory<IfTag> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final IfTag tag, IParserContext context) {
        super.populate(tag, context);
        final OMElement ele = context.getElement();

        assertExactAttributes(ele, IfTag.TEST_ATTR);

        return false;

    }
}
