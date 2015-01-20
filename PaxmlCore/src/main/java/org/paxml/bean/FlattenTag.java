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
package org.paxml.bean;

import java.util.ArrayList;
import java.util.List;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * Flatten tag impl. Flaten the inner lists.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "flatten")
public class FlattenTag extends BeanTag {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        Object val = getValue();
        if(val instanceof List){
            Object result = flatten((List)val, new ArrayList());
            return result;
        }else{
            return val;
        }

    }

    private List flatten(List list, List result) {        
        for (Object obj : list) {
            if (obj instanceof List) {
                flatten((List) obj, result);
            } else {
                result.add(obj);
            }
        }
        return result;
    }
}
