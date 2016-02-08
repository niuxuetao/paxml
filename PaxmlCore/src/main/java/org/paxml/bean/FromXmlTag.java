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

import java.util.Collection;
import java.util.Map;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.ObjectList;
import org.paxml.core.ObjectTree;
import org.paxml.util.XmlUtils;

/**
 * fromXml tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = FromXmlTag.TAG_NAME)
public class FromXmlTag extends BeanTag {
	/**
	 * The tag name.
	 */
	public static final String TAG_NAME = "fromXml";

	private boolean ignoreRootTag;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		Object val = getValue();
		if (val == null) {
			return null;
		}

		Map map = (Map) XmlUtils.fromXml(val.toString(), true);
		String rootName = map.keySet().iterator().next().toString();

		Object obj = XmlUtils.extractSingleMapRoot(map);
		
		if(!ignoreRootTag){
			ObjectTree tree=new ObjectTree(null);
			tree.addValue(rootName, obj);
			return tree;
		}		

		if (obj instanceof Map) {
			ObjectTree tree = new ObjectTree(rootName);
			tree.addValues((Map) obj);
			return tree;
		} else if (obj instanceof Collection) {
			ObjectList list = new ObjectList(rootName, true);
			list.addAll((Collection) obj);
			return list;
		} else {
			return obj;
		}
	}

	public boolean isIgnoreRootTag() {
		return ignoreRootTag;
	}

	public void setIgnoreRootTag(boolean ignoreRootTag) {
		this.ignoreRootTag = ignoreRootTag;
	}

}
