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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.launch.LaunchPoint;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class PaxmlUtils {
	public static final String PAXML_HOME_ENV_KEY = "PAXML_HOME";

	public static File getPaxmlHome() {
		String paxmlHome = System.getenv(PAXML_HOME_ENV_KEY);
		if (paxmlHome == null) {
			paxmlHome = System.getProperty(PAXML_HOME_ENV_KEY);
		}
		if (paxmlHome == null) {
			throw new PaxmlRuntimeException("System environment 'PAXML_HOME' not set!");
		}
		return new File(paxmlHome);
	}

	public static File getPaxmlFile(String file) {
		return new File(getPaxmlHome(), file);
	}

	public static long getNextSessionId() {
		if(true){
			return -1;
		}
		JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());
		return temp.query("select next value for session_id_seq", new ResultSetExtractor<Long>() {

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getLong(1);
			}

		});
	}
	public static long recordExecutionScheduled(LaunchPoint p, long planExecutionId) {
		final long id = getNextSessionId();
		JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());
		temp.update("insert into paxml_execution (id, session_id, process_id, paxml_name, paxml_path, paxml_params, status) values(?,?,?,?,?,?,?)", 
				id, p.getSessionId(), p.getProcessId(), p.getResource().getName(), p.getResource().getPath(), null, 0);
		return id;
	}
/*
	public static void recordExecutionStart(long recId) {
		JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());
		temp.update("update paxml_execution ", p.get);
	}

	public static void recordExecutionStop(long recId, boolean succeeded) {
		JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());

	}
*/
	public static Resource readResource(String resUri) {
		return new DefaultResourceLoader().getResource(resUri);
	}

	public static String readStreamToString(InputStream in, String encoding) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			IOUtils.copy(in, out);
			return out.toString(encoding == null ? "UTF-8" : encoding);
		} catch (IOException e) {
			throw new PaxmlRuntimeException(e);
		}
	}

	public static String readResourceToString(String resUri, String encoding) {
		Resource res = readResource(resUri);
		InputStream in = null;
		try {
			in = res.getInputStream();
			return readStreamToString(in, encoding);
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot read uri: " + resUri, e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

}
