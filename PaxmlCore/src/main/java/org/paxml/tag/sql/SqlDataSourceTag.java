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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.bean.BeanTag;
import org.paxml.core.Context;

/**
 * Soap data source tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "sqlDataSource")
public class SqlDataSourceTag extends BeanTag {

	private static final ConcurrentMap<String, DataSource> CACHE = new ConcurrentHashMap<String, DataSource>();

	/**
	 * Private context keys.
	 * 
	 * @author Xuetao Niu
	 * 
	 */
	private static enum PrivateKeys {
		DATA_SOURCE
	}

	private String url;
	private String username;
	private String password;
	private String driver;

	/**
	 * Get the data source from context, searching parents.
	 * 
	 * @param context
	 *            the context
	 * @return the data source, null if not found.
	 */
	public static DataSource getDataSource(Context context) {
		return (DataSource) context.getLocalInternalObject(PrivateKeys.DATA_SOURCE, true);
	}

	private String getCacheKey() {
		String sep = " | ";
		return url + sep + username + sep + password + sep + driver;
	}

	private DataSource getCacheOrCreate() {
		String key = getCacheKey();
		DataSource ds = CACHE.get(key);
		if (ds == null) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(getDriver());
			dataSource.setUrl(url);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			dataSource.setDefaultAutoCommit(true);
			DataSource existing = CACHE.putIfAbsent(key, dataSource);
			if (existing != null) {
				ds = existing;
			} else {
				ds = dataSource;
			}
		}
		return ds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInvoke(Context context) throws Exception {
		DataSource dataSource = getCacheOrCreate();
		Context targetContext = context.findContextForEntity(getEntity());
		String id = getId(context);
		if (StringUtils.isNotBlank(id)) {
			targetContext.addConst(id, null, dataSource, true);
		} else {
			targetContext.setInternalObject(PrivateKeys.DATA_SOURCE, dataSource, false);
		}
		return dataSource;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		if (StringUtils.isNotBlank(driver)) {
			return driver;
		}
		// the following opensource drivers are on classpath by default
		if (StringUtils.startsWithIgnoreCase(url, "jdbc:h2:")) {
			return "org.h2.Driver";
		} else if (StringUtils.startsWithIgnoreCase(url, "jdbc:hsqldb:")) {
			return "org.hsqldb.jdbc.JDBCDriver";
		} else if (StringUtils.startsWithIgnoreCase(url, "jdbc:mysql:")) {
			return "com.mysql.jdbc.Driver";
		} else if (StringUtils.startsWithIgnoreCase(url, "jdbc:postgresql:")) {
			return "org.postgresql.Driver";
		} else if (StringUtils.startsWithIgnoreCase(url, "jdbc:odbc:")) {
			return "sun.jdbc.odbc.JdbcOdbcDriver";
		} else
		// the following are not opensource db, so not on classpath by default
		if (StringUtils.startsWithIgnoreCase(url, "jdbc:microsoft:sqlserver:")) {
			return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		} else if (StringUtils.startsWithIgnoreCase(url, "jdbc:oracle:thin:")) {
			return "oracle.jdbc.driver.OracleDriver";
		} else if (StringUtils.startsWithIgnoreCase(url, "jdbc:as400:")) {
			return "com.ibm.as400.access.AS400JDBCDriver";
		}
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}
