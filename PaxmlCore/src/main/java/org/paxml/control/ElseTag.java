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
 * Else tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Conditional(ifAttribute = IfTag.TEST_ATTR, unlessAttribute = Conditional.NOT_SUPPORTED)
@Tag(name = ElseTag.TAG_NAME, factory = ElseTagFactory.class)
public class ElseTag extends AbstractControlTag {
    public static final String TAG_NAME = "else";

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doExecute(Context context) {
        if (IfTag.isIfRan(context)) {            
            return null;
        }
        Object result = executeChildren(context);
        IfTag.setIfRan(context);
        return result;
    }

}
