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

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.IObjectContainer;
import org.paxml.util.XmlUtils;

/**
 * toXml tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = ToXmlTag.TAG_NAME)
public class ToXmlTag extends BeanTag {
	/**
	 * The tag name.
	 */
	public static final String TAG_NAME = "toXml";
	private String rootTag;
	private String rootListItemTag;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		Object val = getValue();
		if (val == null) {
			return null;
		}
		String rt = rootTag;
		if (rt == null && val instanceof IObjectContainer) {
			rt = ((IObjectContainer) val).getName();
		}
		if (rt == null) {
			rt = "xml-fragment";
		}

		return XmlUtils.toXml(val, rt, rootListItemTag == null ? "item" : rootListItemTag);
	}

	public String getRootTag() {
		return rootTag;
	}

	public void setRootTag(String rootTag) {
		this.rootTag = rootTag;
	}

	public String getRootListItemTag() {
		return rootListItemTag;
	}

	public void setRootListItemTag(String rootListItemTag) {
		this.rootListItemTag = rootListItemTag;
	}

}
