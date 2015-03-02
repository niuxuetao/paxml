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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.file.FileHelper;
import org.paxml.file.IFile;
import org.paxml.table.IRow;
import org.paxml.table.ITable;
import org.paxml.util.PaxmlUtils;
import org.paxml.util.ReflectUtils;
import org.springframework.util.StringUtils;

/**
 * Append tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "append")
public class AppendTag extends BeanTag {
	private String to;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		IFile ifile;
		if (StringUtils.isEmpty(to)) {
			ifile = context.getOnlyFile();
			if (ifile == null) {
				throw new PaxmlRuntimeException("Please specify a destination to append to!");
			}
		} else {
			File f = PaxmlUtils.getFile(to);
			ifile = context.getFile(f);
			if (ifile == null) {
				ifile = FileHelper.load(PaxmlUtils.getResource(f));
			}
		}
		if (ifile.isReadonly()) {
			throw new PaxmlRuntimeException("Cannot append to: " + to + ", because it is readonly!");
		}
		Object row = getValue();
		if (ifile instanceof ITable) {
			ITable table = ((ITable) ifile);
			IRow r = table.createNextRow();
			if (row == null) {
				throw new PaxmlRuntimeException("Please specify a row to append!");
			} else if (row instanceof Map) {
				r.setCellValues((Map) row);
			} else {
				List list = new ArrayList();
				ReflectUtils.collect(row, list, true);
				r.setCellValues(0, -1, list.iterator());
			}
		} else {
			throw new PaxmlRuntimeException("Unsupported resource type to append to: " + ifile.getClass().getName());
		}

		ifile.flush();
		return ifile;

	}

}
