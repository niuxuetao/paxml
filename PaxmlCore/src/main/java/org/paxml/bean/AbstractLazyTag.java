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

import java.util.Iterator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.paxml.core.Context;

/**
 * Random tag impl.
 * 
 * @author Xuetao Niu
 * 
 */

public abstract class AbstractLazyTag extends BeanTag {
    private boolean lazy;
    protected abstract Iterator getIterator(Context context)throws Exception;
	@Override
	protected Object doInvoke(Context context) throws Exception {
		Iterator it=getIterator(context);
		if (lazy) {
			return it;
		}else{			
			return CollectionUtils.collect(it, new Transformer(){

				@Override
				public Object transform(Object obj) {
					return obj;
				}
				
			});
		}
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}
    
    
}
