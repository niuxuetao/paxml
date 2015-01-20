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
package org.paxml.log4j;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.paxml.core.Context;
import org.paxml.core.IEntity;
import org.paxml.core.Context.Stack;
import org.paxml.core.Context.Stack.IStackTraverser;
import org.paxml.tag.ITag;

public class EntityInfoConverter extends PatternConverter {
    private final boolean fullStack;

    public EntityInfoConverter(boolean fullStack) {
        super();
        this.fullStack = fullStack;
    }

    @Override
    protected String convert(LoggingEvent event) {
        Context context = Context.getCurrentContext();
        if (context == null) {
            return "*";
        } else {

            Stack stack=context.getStack();

            final StringBuilder sb = new StringBuilder();

            if(!stack.isEmpty()){
                sb.append(stack.getFirst().getTagName());
            }
            stack.traverse(new IStackTraverser() {
                
                @Override
                public boolean onItem(IEntity entity, ITag tag) {
                    String name = entity.getResource().getName();
                    if (StringUtils.isBlank(name)) {
                        name = "?";
                    }
                    sb.insert(0, " > ").insert(0, tag.getLineNumber()).insert(0, "@").insert(0, name);
                    return fullStack;
                }
            });
            
            // when it has no stack, it shows question mark.
            return sb.length() > 0 ? sb.toString() : "?";
        }
    }

}