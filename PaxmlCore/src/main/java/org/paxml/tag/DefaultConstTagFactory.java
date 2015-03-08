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

import org.paxml.core.Context.Scope;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlRuntimeException;

/**
 * Tag factory for const tags.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            const tag
 */
public class DefaultConstTagFactory<T extends DefaultConstTag> extends ConstTagFactory<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean populate(final T tag, IParserContext context) {

        super.populate(tag, context);

        if (tag.getScope() != Scope.LOCAL) {
            throw new PaxmlRuntimeException("A <" + DefaultConstTag.TAG_NAME
                    + "> tag must be a local data tag, but its scope: " + tag.getScope());
        }

        return false;
    }

}
