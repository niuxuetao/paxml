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

import org.paxml.annotation.Conditional;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * If tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Conditional(ifAttribute = IfTag.TEST_ATTR, unlessAttribute = Conditional.NOT_SUPPORTED)
@Tag(name = IfTag.TAG_NAME, factory = IfTagFactory.class)
public class IfTag extends AbstractControlTag {

    /**
     * Tag name.
     */
    public static final String TAG_NAME = "if";
    /**
     * Attribute for test.
     */
    public static final String TEST_ATTR = "test";

    /**
     * Private keys.
     * 
     * @author Xuetao Niu
     * 
     */
    private static enum PrivateKeys {
        IF_RAN
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doExecute(Context context) {
        
        Object result = executeChildren(context);
        setIfRan(context);
        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(Context context) {
        clearIfRan(context);
        return super.execute(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onNotExecuted(Context context) {
        super.onNotExecuted(context);
        context.setInternalObject(PrivateKeys.IF_RAN, false, false);
    }

    static boolean isIfRan(Context context) {
        Boolean ran = (Boolean) context.getInternalObject(PrivateKeys.IF_RAN, false);
        return ran != null && ran;
    }
    static void setIfRan(Context context){
        context.setInternalObject(PrivateKeys.IF_RAN, true, false);
    }
    static void clearIfRan(Context context){
        context.removeInternalObject(PrivateKeys.IF_RAN, false);
    }
}
