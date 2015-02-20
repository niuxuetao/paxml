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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.paxml.core.Context;
import org.paxml.core.IEntityExecutionListener;
import org.paxml.core.IEntity;
import org.paxml.core.InMemoryResource;
import org.paxml.core.PaxmlResource;
import org.springframework.core.io.Resource;

/**
 * The base impl for paxml entity tags.
 * 
 * @author Xuetao Niu
 * 
 */
public abstract class AbstractPaxmlEntity extends AbstractTag implements IEntity {
	private long timestamp;

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Object execute(Context context) {
		final List<IEntityExecutionListener> listeners = context.getEntityExecutionListeners(false);
		try {

			if (listeners != null) {
				for (IEntityExecutionListener listener : listeners) {
					listener.onEntry(this, context);
				}
			}
			Object result = super.execute(context);

			return result;

		} finally {
			try {
				if (listeners != null) {
					for (IEntityExecutionListener listener : listeners) {
						listener.onExit(this, context);
					}
				}
			} finally {
				context.closeAllCloseables();
			}
		}
	}

	@Override
	public boolean isCachable() {
		PaxmlResource res = getResource();
		return res != null && !(getResource() instanceof InMemoryResource);
	}

	@Override
	public boolean isModified() {
		Resource res = getResource().getSpringResource();
		if (!res.exists()) {
			return true;
		}
		final File file;
		try {
			file = res.getFile();

		} catch (IOException e) {
			// if file is not obtainable, it is not modifiable.
			return false;
		}
		return !file.exists() || file.lastModified() != timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
