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

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import org.paxml.annotation.IdAttribute;
import org.paxml.core.Context.Scope;
import org.paxml.core.IParserContext;
import org.paxml.core.PaxmlRuntimeException;
import org.paxml.tag.invoker.InvokerTagFactory;

/**
 * Tag factory for const tags.
 * 
 * Some rules apply to make xpath and jexl queries valid:
 * 
 * 1) Dots in tag name is not allowed. 2) Number cannot be the start of the tag
 * name.
 * 
 * @author Xuetao Niu
 * 
 * @param <T>
 *            const tag
 */
@IdAttribute
public class ConstTagFactory<T extends ConstTag> extends DefaultTagFactory<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean populate(final T tag, IParserContext context) {

		final OMElement ele = context.getElement();

		final String tagName = ele.getLocalName();

		if (tagName.contains(".")) {
			throw new PaxmlRuntimeException("Data tag name should not contain dots");
		}
		if (StringUtils.isNumeric(tagName.substring(0, 1))) {
			throw new PaxmlRuntimeException("Data tag name should not start with number");
		}

		super.populate(tag, context);

		tag.setValueName(tagName);
		tag.setSubconst(isUnderConst(tag));

		if (isLocalConst(tag)) {
			if (tag.getIdExpression() == null) {
				throw new PaxmlRuntimeException("Local constant should have id attribute: <" + tagName + ">");
			}
			tag.setScope(Scope.LOCAL);
		} else {
			// must be a parameter const here
			if (!tag.isSubconst()) {
				tag.setScope(Scope.PARAMETER);
			}
		}

		InvokerTagFactory.processElement(tag, context, new IAttributeFilter() {

			public boolean accept(OMElement ele, String attrName, String attrValue) {
				final boolean yes = tag.isSubconst() || !IdAttribute.DEFAULT_VALUE.equals(attrName);
				if (yes) {
					tag.addAttributeName(attrName);
				}
				return yes;
			}
		});

		return false;
	}

	

}
