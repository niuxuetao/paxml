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

import org.apache.commons.lang.StringUtils;
import org.paxml.annotation.Tag;
import org.paxml.core.Context;
import org.paxml.security.Secret;
import org.paxml.security.SecretRepository;

/**
 * Secret tag impl.
 * 
 * 
 * @author Xuetao Niu
 * 
 */
@Tag(name = "secret")
public class SecretTag extends BeanTag {
	/**
	 * If name not given, then do not touch key store; if given save to key
	 * store.
	 * 
	 */
	private String name;
	private boolean store = true;

	@Override
	protected Object doInvoke(Context context) throws Exception {
		Object clearValue = getValue();
		if (clearValue == null) {
			if (StringUtils.isEmpty(name)) {
				if (store) {
					return Context.setSecret(name, SecretRepository.askForSecretValue(name));
				} else {
					return new Secret(name, SecretRepository.askForSecretValue(name));
				}
			} else {
				if (store) {
					Secret sec = Context.getSecret(name);
					if (sec != null) {
						return sec;
					}
					return Context.setSecret(name, SecretRepository.askForSecretValue(name));
				} else {
					return new Secret(name, SecretRepository.askForSecretValue(name));
				}

			}
		} else {
			if (StringUtils.isEmpty(name)) {
				if (store) {
					return Context.setSecret(name, clearValue.toString());
				} else {
					return new Secret(null, clearValue.toString());
				}
			} else {
				if (store) {
					return Context.setSecret(name, clearValue.toString());
				} else {
					return new Secret(name, clearValue.toString());
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isStore() {
		return store;
	}

	public void setStore(boolean store) {
		this.store = store;
	}

}
