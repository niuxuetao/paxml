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
package org.paxml.el;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.ITagLibrary;
import org.paxml.util.ReflectUtils;

/**
 * Apache Jexl expression impl.
 * 
 * @author Xuetao Niu
 * 
 */
public class JexlExpression extends AbstractExpression {
    private static final JexlEngine JEXL_ENGINE_STRICT = new JexlEngine();
    private static final JexlEngine JEXL_ENGINE_NON_STRICT = new JexlEngine();
    static {
        JEXL_ENGINE_STRICT.setLenient(false);
        JEXL_ENGINE_NON_STRICT.setLenient(true);
    }
    private final Expression exp;
    private final boolean strict;

    /**
     * Construct from string.
     * 
     * @param exp
     *            the string expression
     * @param strict true to disallow unknown const, false to allow.           
     */
    public JexlExpression(final String exp, boolean strict) {        
        this.exp = (strict?JEXL_ENGINE_STRICT:JEXL_ENGINE_NON_STRICT).createExpression(exp);
        this.strict=strict;
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(Context context) {
        return exp.evaluate(convertContext(context));
    }

    /**
     * {@inheritDoc}
     */
    public String getString() {
        return exp.getExpression();
    }

    private JexlContext convertContext(final Context context) {
        return new JexlContext() {

            public Object get(String name) {

                Object obj = context.getConst(name, true);

                if (obj == null) {
                    // check if it is a util function reference
                    obj = getUtilFunctions(name, context);
                    if (obj != null) {
                        context.getRootContext().addConst(name, null, obj, true);
                    }

                }
                return obj;

            }

            public boolean has(String name) {
                if (!context.hasConstId(name, true)) {
                    if(strict){
                        throw new PaxmlRuntimeException("Unknown const name: " + name);
                    }
                    return true;
                }
                return true;
            }

            public void set(String name, Object value) {
                throw new UnsupportedOperationException("Cannot change the reference of constant: " + name);
            }

        };
    }

    private Object getUtilFunctions(String name, Context context) {
        for (ITagLibrary tagLib : context.getPaxml().getParser().getTagLibraries()) {
            Class<? extends IUtilFunctionsFactory> clazz = tagLib.getUtilFunctionsFactory(name);
            if (clazz != null) {
                return ReflectUtils.createObject(clazz).getUtilFunctions(context);
            }
        }
        return null;
    }
}


