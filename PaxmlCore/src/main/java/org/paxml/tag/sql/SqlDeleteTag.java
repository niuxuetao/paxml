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
package org.paxml.tag.sql;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.annotation.Tag;

/**
 * SqlDelete tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "sqlDelete")
public class SqlDeleteTag extends SqlTag {
	private static final Log log = LogFactory.getLog(SqlDeleteTag.class);

	private String table;

	@Override
	public Object getValue() {
		return getDeleteStatement(table, getParam());
	}
	
	static String getDeleteStatement(String table, Map map){
		StringBuilder sb = new StringBuilder("delete from ");
		sb.append(table).append(" where ");
		boolean first = true;
		for (Object k : map.keySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(" and ");
			}
			sb.append(k).append("=").append(":").append(k);
		}
		sb.append(";\r\n");
		return sb.toString();
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

}
