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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * FindData tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = FindConstTag.TAG_NAME)
public class FindConstTag extends BeanTag {
    private static final Log log = LogFactory.getLog(FindConstTag.class);
    /**
     * The tag name.
     */
    public static final String TAG_NAME = "findData";

    private String byId;
    private boolean ignoreParentContext = false;

    @Override
    protected Object doInvoke(Context context) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Finding const with id '" + byId + "'" + (ignoreParentContext ? " without " : " with ")
                    + "parent context.");
        }
        return context.getConst(byId, !ignoreParentContext);
    }

    public String getById() {
        return byId;
    }

    public void setById(String byId) {
        this.byId = byId;
    }

    public boolean isIgnoreParentContext() {
        return ignoreParentContext;
    }

    public void setIgnoreParentContext(boolean ignoreParentContext) {
        this.ignoreParentContext = ignoreParentContext;
    }

}
