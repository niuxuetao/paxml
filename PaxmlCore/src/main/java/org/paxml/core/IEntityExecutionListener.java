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
package org.paxml.core;

/**
 * Listener before and after an execution. Each execution can be treated as a
 * process which has unique context.
 * 
 * @author Xuetao Niu
 * 
 */
public interface IEntityExecutionListener {
    /**
     * Event handler before the process starts.
     * 
     * @param entity
     *            the entry point entity
     * @param context
     *            the execution context
     */
    void onEntry(IEntity entity, Context context);

    /**
     * Event handler after the process finished.
     * 
     * @param entity
     *            the entry point entity
     * @param context
     *            the execution context
     */
    void onExit(IEntity entity, Context context);

}
