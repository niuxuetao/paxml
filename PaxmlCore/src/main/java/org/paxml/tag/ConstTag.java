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

import java.util.LinkedHashMap;
import java.util.Map;

import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.core.Context.Scope;
import org.paxml.core.IObjectContainer;
import org.paxml.core.ObjectList;
import org.paxml.core.ObjectTree;
import org.paxml.core.PaxmlRuntimeException;

/**
 * The const tag impl.
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "const", alias = { "data" }, factory = ConstTagFactory.class)
public class ConstTag extends AbstractTag {

	private static class ConstNode {
		private final String name;
		private final Object value;

		public ConstNode(String name, Object value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}
	}

	private String valueName;
	private boolean subconst;
	private Scope scope;

	private ChildrenResultList extractResults(ChildrenResultList from, ChildrenResultList to) {
		if (to == null) {
			to = new ChildrenResultList(1);
		}
		if (from == null) {
			return to;
		}
		for (Object obj : from) {
			if (obj instanceof ChildrenResultList) {
				extractResults((ChildrenResultList) obj, to);
			} else {
				to.add(obj);
			}
		}
		return to;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object doExecute(Context context) {

		Object myValue = null;
		ChildrenResultList childrenResults = executeChildren(Scope.PARAMETER == scope ? context.findContextForEntity(getEntity()) : context);

		if (childrenResults != null) {
			childrenResults = extractResults(childrenResults, null);
			boolean allSubconsts = true;
			boolean hasSubconsts = false;
			for (Object childResult : childrenResults) {
				if (childResult instanceof ConstNode) {
					hasSubconsts = true;
				} else {
					allSubconsts = false;
					break;
				}
			}
			if (allSubconsts) {
				ObjectTree tree = new ObjectTree(valueName);
				for (Object childResult : childrenResults) {
					ConstNode node = (ConstNode) childResult;
					if (node.getValue() != null) {
						tree.addValue(node.getName(), node.getValue());
					}
				}
				myValue = tree.shrink();
			} else if (hasSubconsts) {
				ObjectTree tree = new ObjectTree(valueName);
				for (Object childResult : childrenResults) {
					if (childResult instanceof ConstNode) {
						ConstNode node = (ConstNode) childResult;
						if (node.getValue() != null) {
							tree.addValue(node.getName(), node.getValue());
						}
					} else if(childResult!=null) {
						tree.addValue("value", childResult.toString());
					}
				}
				myValue = tree.shrink();
				// throw new
				// PaxmlRuntimeException("Cannot mix const tag with value tag under the same parent");
			} else {

				ObjectList list = new ObjectList(valueName, false);

				for (Object childResult : childrenResults) {
					if (childResult instanceof ConstNode) {
						ConstNode cn = (ConstNode) childResult;
						if (cn.getValue() != null) {
							list.addValue(cn.getName(), cn.getValue());
						}
					} else {
						if (childResult != null) {
							list.add(childResult);
						}
					}
				}
				myValue = list.shrink();
			}
		}

		if (Scope.LOCAL == scope) {
			final IdExpression idExp = getIdExpression();
			final String id = idExp == null ? null : idExp.getId(context);
			// set the id of the const if it is object container
			if (myValue instanceof IObjectContainer) {
				((IObjectContainer) myValue).setId(id);
			}
			if (context.isConstOverwritable()) {
				context.setConst(id, valueName, myValue, false);
			} else {
				context.addConst(id, valueName, myValue, true);
			}

		} else if (Scope.PARAMETER == scope) {
			context.addConst(valueName, valueName, myValue, false);
		} else if (isSubconst()) {
			myValue = new ConstNode(valueName, myValue);
		} else {
			throw new PaxmlRuntimeException("Internal error: Shouldn't to reach here");
		}
		return myValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void putResultAsConst(Context context, Object result) {
		// cancel the default behavior because the const has already been put on
		// context in the 1st place.
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public boolean isSubconst() {
		return subconst;
	}

	public void setSubconst(boolean subconst) {
		this.subconst = subconst;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, Object> inspectAttributes() {

		Map<String, Object> map = super.inspectAttributes();
		if (map == null) {
			map = new LinkedHashMap<String, Object>();
		}
		map.put("id", getIdExpression());
		map.put("scope", scope);
		map.put("subconst", subconst);
		map.put("valueName", valueName);

		return map;
	}

}
