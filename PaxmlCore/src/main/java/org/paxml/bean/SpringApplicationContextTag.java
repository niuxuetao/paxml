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
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.SpringXmlEntityFactory;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;

/**
 * SpringApplicationContext impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = SpringApplicationContextTag.TAG_NAME)
public class SpringApplicationContextTag extends BeanTag {
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "springApplicationContext";

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doInvoke(Context context) throws Exception {
        final Object value = getValue();
        final String path = value == null ? null : value.toString().trim();
        if (StringUtils.isBlank(path)) {
            throw new PaxmlRuntimeException("No spring xml file given!");
        }
        Resource res = PaxmlUtils.getResource(path, getEntity().getResource().getSpringResource());
        PaxmlResource _res = PaxmlResource.createFromResource(res);
        return SpringXmlEntityFactory.getApplicationContext(_res);
    }

}
