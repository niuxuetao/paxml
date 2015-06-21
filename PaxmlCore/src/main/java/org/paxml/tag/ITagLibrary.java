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
package org.paxml.tag;

import java.util.Set;

import org.paxml.el.IUtilFunctionsFactory;

/**
 * The prototype of tag library.
 * 
 * @author Xuetao Niu
 * 
 */
public interface ITagLibrary {
	/**
	 * Get the namespace uri of the tab lib.
	 * 
	 * @return the namespace uri
	 */
	String getNamespaceUri();

	/**
	 * Get tag factory instance from tag name.
	 * 
	 * @param tagName
	 *            the tag name
	 * @return the tag factory instance used to construct the tag with
	 */
	ITagFactory<? extends ITag> getFactory(String tagName);

	/**
	 * Get the util function impl class by name.
	 * 
	 * @param name
	 *            the name of util in context
	 * @return the util impl class, null if not found.
	 */
	Class<? extends IUtilFunctionsFactory> getUtilFunctionsFactory(String name);

	/**
	 * Return the names for the util functions factories.
	 * 
	 * @return non null set
	 */
	Set<String> getUtilFunctionsFactoryNames();

}
