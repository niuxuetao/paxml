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
package org.paxml.file;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.table.excel.ExcelFileFactory;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;

/**
 * Helper for ITable stuff.
 * 
 * @author Xuetao Niu
 * 
 */
public class FileHelper {
	private static final Log log = LogFactory.getLog(FileHelper.class);
	private static final Map<String, IFileFactory> factories = new ConcurrentHashMap<String, IFileFactory>();

	static {

		registerFactory(new ExcelFileFactory(), "xls", "xlsx");
		registerFactory(new LineBasedFileFactory(), "txt", "properties", "*");
	}

	public static void registerFactory(IFileFactory fact, String... fileExtensions) {
		for (String ext : fileExtensions) {
			factories.put(ext.toLowerCase(), fact);
		}
	}

	public static IFile load(Resource file) {
		String ext = FilenameUtils.getExtension(file.getFilename()).toLowerCase();
		IFileFactory fact = factories.get(ext);
		if (fact == null) {
			fact = factories.get("*");
			if (fact == null) {
				throw new PaxmlRuntimeException("Unknown file type based on its extension: " + PaxmlUtils.getResourceFile(file));
			}
			if (log.isDebugEnabled()) {
				log.debug("Using the default file factory '" + fact.getClass().getName() + "' to load file: " + PaxmlUtils.getResourceFile(file));
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Using registered file factory '" + fact.getClass().getName() + "' to load file: " + PaxmlUtils.getResourceFile(file));
			}
		}
		IFile result = fact.load(file);
		Context.getCurrentContext().registerCloseable(result);
		return result;
	}

}
