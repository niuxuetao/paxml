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

import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlResource;
import org.paxml.core.ResourceLocator;

/**
 * Inspect tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "inspect")
public class InspectTag extends BeanTag {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) {
        Object val = getValue();
        String entityName = val == null ? null : val.toString().trim();
        if (StringUtils.isBlank(entityName)) {
            val = getEntity().printTree(0);
        } else {
            ResourceLocator locator = getResourceLocator();
            PaxmlResource res = locator.getResource(entityName);
            val = getResourceLocator().getEntity(entityName, null);
        }

        return val;
    }

}
