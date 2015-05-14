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
import org.paxml.el.UtilFunctions;

/**
 * Confirm tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "confirm")
public class ConfirmTag extends BeanTag {
	private static final Log log = LogFactory.getLog(ConfirmTag.class);
	private String otherwise;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		Object v = getValue();

		boolean yes = v == null ? UtilFunctions.confirm() : UtilFunctions.confirm(v.toString());
		if (!yes) {
			if ("exit".equalsIgnoreCase(otherwise)) {
				if (log.isInfoEnabled()) {
					log.info("Not confirmed, exiting paxml ...");
				}
				UtilFunctions.exit();
			} else if ("return".equalsIgnoreCase(otherwise)) {
				if (log.isInfoEnabled()) {
					log.info("Not confirmed, returning from current context ...");
				}
				context.getCurrentEntityContext().setReturning(true);
			}
		}
		return yes;
	}

	public String getOtherwise() {
		return otherwise;
	}

	public void setOtherwise(String otherwise) {
		this.otherwise = otherwise;
	}

}
