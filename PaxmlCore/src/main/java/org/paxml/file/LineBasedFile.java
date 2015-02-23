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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.poi.util.IOUtils;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;

public class LineBasedFile implements Iterator<String>, IFile {
	private final BufferedReader reader;
	private final String name;
	private String line;

	public LineBasedFile(Resource file, String encoding) {
		name = PaxmlUtils.getResourceFile(file);
		InputStream in = null;
		try {
			in = file.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in, encoding == null ? "UTF-8" : encoding));
			// cache the 1st line
			line = reader.readLine();
		} catch (IOException e) {
			IOUtils.closeQuietly(in);
			throw new PaxmlRuntimeException("Cannot read file: " + name, e);
		}
	}

	@Override
	public boolean hasNext() {
		return line != null;
	}

	@Override
	public String next() {
		String result = line;
		try {
			// advance the cached line
			line = reader.readLine();
		} catch (IOException e) {
			line = null;
			IOUtils.closeQuietly(reader);
			throw new PaxmlRuntimeException("Cannot read file: " + name, e);
		}
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		IOUtils.closeQuietly(reader);
	}

}
