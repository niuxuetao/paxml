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
import org.paxml.annotation.IdAttribute;
import org.paxml.tag.AbstractTag;


/**
 * This is the most basic impl for all control tags.
 * @author Xuetao Niu
 *
 */
@Conditional
@IdAttribute(IdAttribute.NO_ID_SUPPORT)
public abstract class AbstractControlTag extends AbstractTag {
    
}
