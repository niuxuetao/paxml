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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;

/**
 * Split tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "split")
public class SplitTag extends BeanTag {

	private boolean byCapital = false;
	private boolean bySpace = true;
	private boolean byCharacter = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doInvoke(Context context) {
		Object value = getValue();
		if (value == null) {
			value = "";
		}
		List<String> list = new ArrayList<String>();
		if (bySpace) {
			StringTokenizer st = new StringTokenizer(value.toString());
			while (st.hasMoreTokens()) {
				list.add(st.nextToken());
			}
		} else {
			list.add(value.toString());
		}
		if (byCapital) {
			List<String> tmp = new ArrayList<String>();
			for (String str : list) {
				splitByCapital(str, tmp);
			}
			list = tmp;
		}
		if (byCharacter) {
			List<String> chars = new ArrayList<String>();
			for (String str : list) {
				for (char c : str.toCharArray()) {
					chars.add(Character.toString(c));
				}
			}
			list = chars;
		}
		return list;
	}

	public static void splitByCapital(String str, List<String> result) {
		int start = 0;
		for (int i = 0; i < str.length(); i++) {

			Character c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				// if the next char is lowercased, end the previous word
				if ((i - 1 >= 0 && !Character.isUpperCase(str.charAt(i - 1))) || (i + 1 < str.length() && searchLowercase(str, i + 1) > 0)) {
					String word = str.substring(start, i);
					if (StringUtils.isNotBlank(word)) {
						result.add(word);
					}
					start = i;
				} else {

				}
			}
		}
		String last = str.substring(start, str.length());
		if (StringUtils.isNotBlank(last)) {
			result.add(last);
		}
	}

	private final static int searchLowercase(String str, int since) {
		for (int i = since; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isLowerCase(c)) {
				return i;
			} else if (Character.isUpperCase(c)) {
				break;
			}
		}
		return -1;
	}

	public boolean isByCapital() {
		return byCapital;
	}

	public void setByCapital(boolean byCapital) {
		this.byCapital = byCapital;
	}

	public boolean isBySpace() {
		return bySpace;
	}

	public void setBySpace(boolean bySpace) {
		this.bySpace = bySpace;
	}

	public boolean isByCharacter() {
		return byCharacter;
	}

	public void setByCharacter(boolean byCharacter) {
		this.byCharacter = byCharacter;
	}

}
