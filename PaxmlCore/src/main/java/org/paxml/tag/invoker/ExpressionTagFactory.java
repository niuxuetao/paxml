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

import org.apache.commons.lang3.StringUtils;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.el.ExpressionFactory;
import org.paxml.tag.LeafTagFactory;

/**
 * Expression tag factory. This is the fundamental factory that parses an expression.
 * 
 * @author Xuetao Niu
 * 
 */
public class ExpressionTagFactory extends LeafTagFactory<ExpressionTag> {
    /**
     * {@inheritDoc}
     * 
     */
    @Override
    protected boolean populate(final ExpressionTag tag, IParserContext context) {

        super.populate(tag, context);

        String text = context.getElement().getText();
        if (StringUtils.isEmpty(text)) {
            throw new PaxmlRuntimeException("<" + ExpressionTag.TAG_NAME + ">'s value cannot be blank!");
        }

        tag.setExpression(ExpressionFactory.create(text));

        return false;
    }
}
