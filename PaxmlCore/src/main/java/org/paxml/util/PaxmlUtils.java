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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.PaxmlParseException;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.launch.LaunchPoint;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class PaxmlUtils {

	private static final Log log = LogFactory.getLog(PaxmlUtils.class);

	public static final String PAXML_HOME_ENV_KEY = "PAXML_HOME";

	public static File getPaxmlHome(boolean assert_PAXML_HOME) {
		String paxmlHome = System.getenv(PAXML_HOME_ENV_KEY);
		if (paxmlHome == null) {
			paxmlHome = System.getProperty(PAXML_HOME_ENV_KEY);
		}
		if (paxmlHome == null) {
			if (assert_PAXML_HOME) {
				throw new PaxmlRuntimeException("System environment variable 'PAXML_HOME' not set!");
			} else {
				return null;
			}
		}
		return new File(paxmlHome);
	}

	/**
	 * Get resource from path.
	 * 
	 * @param path
	 *            if path has prefix, the path will be directly used; if not,
	 *            the path will be treated as a relative path based on the
	 *            "base" resource parameter.
	 * @param base
	 *            the base resource when path has no prefix. If null given and
	 *            base resource has no prefix, then the path is assumed to be a
	 *            file system resource if it exists, and if it doesn't exist, it
	 *            will be assumed as a classpath resource.
	 * @return the Spring resource.
	 */
	public static Resource getResource(String path, Resource base) {
		final String filePrefix = "file:";
		final String classpathPrefix = "classpath:";

		if (!path.startsWith(filePrefix) && !path.startsWith(classpathPrefix)) {
			if (base == null) {
				File file = getFile(path);
				if (file.isFile()) {
					path = filePrefix + file.getAbsolutePath();
				} else {
					// assume to be a file
					path = classpathPrefix + path;
				}
			} else {
				try {
					path = base.createRelative(path).getURI().toString();
				} catch (IOException e) {
					throw new PaxmlParseException("Cannot create relative path '" + path + "' from base resource: "
							+ base + ", because: " + e.getMessage());
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Taking resource from computed path: " + path);
		}
		return new DefaultResourceLoader().getResource(path);

	}

	public static Resource getResource(String path) {
		return getResource(path, null);
	}

	public static Resource getResource(File file) {
		return getResource(file.getAbsolutePath());
	}

	/**
	 * Create a file object.
	 * 
	 * @param pathWithoutPrefix
	 *            the path without spring resource prefix.
	 * @return if the given path is relative, try the following locations in
	 *         order: - current working dir - user.home dir - dir pointed by
	 *         PAXML_HOME system property - if file not found in all above
	 *         locations, then return {@code}new File(pathWithoutPrefix);{@code}
	 */
	public static File getFile(String pathWithoutPrefix) {
		File file = new File(pathWithoutPrefix);
		if (!file.isAbsolute()) {
			file = getFileUnderCurrentDir(pathWithoutPrefix);
			if (!file.exists()) {
				file = getFileUnderUserHome(pathWithoutPrefix);
			}
			if (!file.exists()) {
				File f = getFileUnderPaxmlHome(pathWithoutPrefix, false);
				if (f != null && f.exists()) {
					file = f;
				}
			}
		}

		return file;
	}

	public static File getFileUnderPaxmlHome(String file, boolean assert_PAXML_HOME) {
		File home = getPaxmlHome(assert_PAXML_HOME);
		if (home == null) {
			return null;
		}
		return new File(home, file);
	}

	public static File getFileUnderCurrentDir(String file) {

		return new File(new File("."), file);

	}

	public static File getFileUnderUserHome(String file) {

		File userHomeDir = new File(System.getProperty("user.home"));
		return new File(userHomeDir, file);

	}

	/**
	 * Trim the property names and values and return in a new Properties object.
	 * 
	 * @param props
	 *            properties
	 * @return the new Properties object contained the trimmed names and values.
	 */
	public static Properties trimProperties(Properties props) {
		Properties result = new Properties();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			result.put(key.trim(), value.trim());
		}
		return result;
	}

	/**
	 * Load properties from a resource file and more text if given.
	 * 
	 * @param props
	 *            the properties file to load into
	 * @param res
	 *            a resource to load from, null to ignore
	 * @param moreText
	 *            a text to load from, null to ignore
	 * @return the input properties
	 */
	public static Properties loadProperties(Properties props, Resource res, String moreText) {

		InputStream[] ins = new InputStream[2];
		if (res != null) {
			try {
				ins[0] = res.getInputStream();
			} catch (IOException e) {
				throw new PaxmlRuntimeException("Cannot load properties from resource " + res, e);
			}
		}
		if (moreText != null) {
			try {
				ins[1] = new ByteArrayInputStream(moreText.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new PaxmlRuntimeException(e);
			}
		}
		return loadProperties(props, true, ins);

	}

	/**
	 * Load properties from multiple streams.
	 * 
	 * @param props
	 *            the properties to load into
	 * @param closeStream
	 *            true to close all streams no matter what, false not to close
	 *            any single stream
	 * @param inputStreams
	 *            all input streams, the loading order is the same as the order
	 *            the input streams are given
	 * @return the properties passed in.
	 */
	public static Properties loadProperties(Properties props, boolean closeStream, InputStream... inputStreams) {
		int i = 0;
		try {
			for (InputStream in : inputStreams) {
				if (in != null) {
					props.load(in);
					i++;
				}
			}
		} catch (IOException e) {
			throw new PaxmlRuntimeException("Cannot load properties from input stream: " + i, e);
		} finally {
			if (closeStream) {
				for (InputStream in : inputStreams) {
					IOUtils.closeQuietly(in);
				}
			}
		}
		return props;
	}

	public static long getNextExecutionId() {
		if (true) {
			return -1;
		}
		JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());
		return temp.query("select next value for execution_id_seq", new ResultSetExtractor<Long>() {

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getLong(1);
			}

		});
	}

	public static long recordExecutionScheduled(LaunchPoint p, long planExecutionId) {
		final long id = getNextExecutionId();
		JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());
		temp.update(
				"insert into paxml_execution (id, session_id, process_id, paxml_name, paxml_path, paxml_params, status) values(?,?,?,?,?,?,?)",
				id, p.getExecutionId(), p.getProcessId(), p.getResource().getName(), p.getResource().getPath(), null,
				0);
		return id;
	}

	/*
	 * public static void recordExecutionStart(long recId) { JdbcTemplate temp =
	 * new JdbcTemplate(DBUtils.getPooledDataSource()); temp.update(
	 * "update paxml_execution ", p.get); }
	 * 
	 * public static void recordExecutionStop(long recId, boolean succeeded) {
	 * JdbcTemplate temp = new JdbcTemplate(DBUtils.getPooledDataSource());
	 * 
	 * }
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

	public static String getResourceFile(Resource res) {
		try {
			return res.getFile().getAbsolutePath();
		} catch (IOException e) {
			return res.getFilename();
		}
	}

	public static File getSiblingFile(File base, String name, boolean appendName) {
		return new File(base.getParentFile(), appendName ? base.getName() : name);
	}

	public static File getCurrentDir() {
		return Paths.get("").toFile();
	}

	public static String createRegexFromGlob(String glob) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < glob.length(); i++) {
			final char c = glob.charAt(i);
			switch (c) {
			case '*':
				sb.append(".*");
				break;
			case '?':
				sb.append(".{1}");
				break;
			default:
				sb.append(Pattern.quote(c + ""));
			}
		}
		return sb.toString();
	}

	public static String getSystemProperty(String key) {
		String value = System.getProperty(key);
		if (StringUtils.isEmpty(value)) {
			value = System.getenv(key);
		}
		return value;
	}

	public static String[] makeHttpClientAutorizationHeader(String username, String password) {

		String un_pd = username + ":" + (password == null ? "" : password);
		byte[] bytes;
		try {
			bytes = un_pd.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new PaxmlRuntimeException(e);
		}
		String auth = "Basic " + new Base64Encoder().encode(bytes);
		return new String[] { "Authorization", auth };

	}
}
