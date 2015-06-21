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
import org.apache.commons.lang3.StringUtils;
import org.paxml.core.IParserContext;
import org.paxml.el.ExpressionFactory;
import org.paxml.tag.DefaultTagFactory;
import org.paxml.util.AxiomUtils;

/**
 * Mutex tag factory.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            the tag type to produce
 */
public class MutexTagFactory<T extends MutexTag> extends AbstractControlTagFactory<T> {
    /**
     * The name property for the mutext name.
     */
    public static final String NAME = "name";
    /**
     * The timeout property to wait for entering the mutex.
     */
    public static final String TIMEOUT = "timeout";

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(T tag, IParserContext context) {
        
        OMElement ele = context.getElement();
        
        assertNoAttributes(ele, NAME, TIMEOUT);
        
        String timeout = AxiomUtils.getAttribute(ele, TIMEOUT);
        if (StringUtils.isNotBlank(timeout)) {
            tag.setTimeout(ExpressionFactory.create(timeout));
        }
        String name = AxiomUtils.getAttribute(ele, NAME);
        if (StringUtils.isNotBlank(name)) {
            tag.setName(ExpressionFactory.create(name));
        }
        return super.populate(tag, context);
    }

}
