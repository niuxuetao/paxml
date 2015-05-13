/**
 * This file is part of PaxmlWeb.
 *
 * PaxmlWeb is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PaxmlWeb is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with PaxmlWeb.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.paxml.web;

import org.apache.axiom.om.OMElement;
import org.paxml.annotation.RootTag;
import org.paxml.core.IParserContext;
import org.paxml.tag.AbstractPaxmlEntity;
import org.paxml.tag.AbstractPaxmlEntityFactory;
import org.paxml.tag.ScenarioEntityFactory.Scenario;

@RootTag(PageFactory.TAG_NAME)
public class PageFactory extends AbstractPaxmlEntityFactory {
	public static final String TAG_NAME = "page";

	public static class Page extends Scenario {

	}

	@Override
	protected AbstractPaxmlEntity doCreate(OMElement root, IParserContext context) {
		return new Page();
	}

}
