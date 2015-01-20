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

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.el.UtilFunctions;

/**
 * The default tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = DefaultConstTag.TAG_NAME, factory = DefaultConstTagFactory.class)
public class DefaultConstTag extends ConstTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "default";

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doExecute(Context context) {
        String id = getIdExpression().getId(context);

        if (context.hasConstId(id, true)) {
            
            // now we do not allow empty
            Object existing = context.getConst(id, true);
            if (!UtilFunctions.isEmpty(existing)) {
                return null;
            }

        }
        
        return super.doExecute(context);
    }

}
