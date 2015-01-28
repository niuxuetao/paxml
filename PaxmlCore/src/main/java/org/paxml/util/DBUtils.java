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
package org.paxml.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.paxml.core.PaxmlRuntimeException;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBUtils {

	private static final ConcurrentMap<String, BasicDataSource> pooledDataSources = new ConcurrentHashMap<String, BasicDataSource>();

	public static final String H2_DRIVER_CLASS = "org.h2.Driver";
	public static final String H2_USER = "sa";
	public static final String H2_PASSWORD = "";
	
	public static String getDefaultH2Url(){
		String file = PaxmlUtils.getPaxmlFile("/data/h2").getAbsolutePath();
		return "jdbc:h2:" + file;
	}

	public static void initDatabase(DataSource ds) {
		runSqlResource(ds, "/ddl/create-1.0.0.sql");
	}

	public static int[] runSqlResource(DataSource ds, String uri) {
		JdbcTemplate temp = new JdbcTemplate(ds);
		List<String> list = DBUtils.breakSql(PaxmlUtils.readResourceToString(uri, null));
		return temp.batchUpdate(list.toArray(new String[list.size()]));

	}

	public static DataSource getPooledDataSource() {
		return getPooledDataSource(getDefaultH2Url());
	}

	public static DataSource getPooledDataSource(String url) {
		return getPooledDataSource(H2_DRIVER_CLASS, H2_USER, H2_PASSWORD, url);
	}

	public static DataSource getPooledDataSource(String driverClass, String username, String password, String url) {
		String key = driverClass + "::" + url;
		BasicDataSource ds = pooledDataSources.get(key);
		if (ds == null) {
			ds = new BasicDataSource();
			ds.setDriverClassName(driverClass);
			ds.setUsername(username);
			ds.setPassword(password);
			ds.setUrl(url);
			BasicDataSource existingDs = pooledDataSources.putIfAbsent(key, ds);
			if (existingDs != null) {
				try {
					ds.close();
				} catch (SQLException e) {
					// do nothing
				}
				ds = existingDs;
			}

		}

		return ds;
	}

	public static List<String> breakSql(String sql) {
		List<String> ret = new ArrayList<String>();

		List<String> lines;
		try {
			lines = IOUtils.readLines(new ByteArrayInputStream(sql.getBytes("UTF-8")));
		} catch (IOException e) {
			throw new PaxmlRuntimeException(e);
		}
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			line = line.trim();
			if (line.length() <= 0 || line.startsWith("--")) {
				continue;
			}

			if (line.charAt(line.length() - 1) == ';') {
				line = line.substring(0, line.length() - 1).trim();
				if (line.length() > 0) {
					sb.append(line);
					ret.add(sb.toString());
					sb.setLength(0);
				}
			} else {
				sb.append(line).append(' ');
			}
		}
		if (sb.length() > 0) {
			String remainder = sb.toString();
			if (remainder.charAt(remainder.length() - 1) == ';') {
				remainder = remainder.substring(0, remainder.length() - 1);
				if (remainder.length() > 0) {
					ret.add(remainder);
				}
			} else {
				ret.add(remainder);
			}
		}
		return ret;
	}
}
