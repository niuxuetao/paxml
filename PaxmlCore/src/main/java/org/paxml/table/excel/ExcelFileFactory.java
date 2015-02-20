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
package org.paxml.table.excel;

import java.io.IOException;

import org.paxml.core.PaxmlRuntimeException;
import org.paxml.file.IFile;
import org.paxml.file.IFileFactory;
import org.paxml.util.PaxmlUtils;
import org.springframework.core.io.Resource;

/**
 * Excel table factory.
 * 
 * @author Xuetao Niu
 * 
 */
public class ExcelFileFactory implements IFileFactory {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFile load(Resource file) {
		try {
			return new ExcelTable(file.getFile(), null, null, false, false);
		} catch (IOException e) {
			throw new PaxmlRuntimeException("Cannot find the file of resource: " + PaxmlUtils.getResourceFile(file), e);
		}
	}

}
