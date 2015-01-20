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

import java.util.LinkedHashMap;
import java.util.Map;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.el.IExpression;

/**
 * Expression tag impl. This is the fundamental tag for getting a value
 * from an IExpression.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = ExpressionTag.TAG_NAME, factory = ExpressionTagFactory.class)
public class ExpressionTag extends AbstractInvokerTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "expression";
    private IExpression expression;
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object invoke(Context context) throws Exception {
        return expression.evaluate(context);
    }

    public IExpression getExpression() {
        return expression;
    }

    public void setExpression(IExpression expression) {
        this.expression = expression;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> inspectAttributes() {

        Map<String, Object> attrs = super.inspectAttributes();
        if (attrs == null) {
            attrs = new LinkedHashMap<String, Object>(1);
        }
        attrs.put("value", expression.getString());
        return attrs;
    }

}
