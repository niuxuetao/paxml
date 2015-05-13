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
package org.paxml.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.commons.lang.StringUtils;
import org.paxml.util.CryptoUtils;

public class SecretRepository {

	private static final ConcurrentMap<String, String> masterKeys = new ConcurrentHashMap<String, String>();

	public static String getCurrentUser() {
		return "";
	}

	// TODO: make it not synchronized when supporting web user sessions
	public synchronized static String getCurrentUserMasterKey() {
		final String currentUser = getCurrentUser();
		String key = masterKeys.get(currentUser);
		if (key != null) {
			return CryptoUtils.decrypt(CryptoUtils.base64Decode(key), currentUser);
		}

		key = askForCurrentUserMasterKey();
		if (key != null) {			
			masterKeys.put(currentUser, CryptoUtils.base64Encode(CryptoUtils.encrypt(key, currentUser)));
		}

		return key;
	}

	public static String askForCurrentUserMasterKey() {
		return askForPasswordInput("Please enter secret store password");
	}

	public static String askForSecretValue(String secreyKey) {
		return askForPasswordInput("Please enter secret" + (StringUtils.isBlank(secreyKey) ? "" : " for: " + secreyKey));
	}

	public static String askForPasswordInput(String question) {

		class DummyFrame extends JFrame {
			DummyFrame(String title) {
				super(title);
				setUndecorated(true);
				setVisible(true);
				setLocationRelativeTo(null);
			}
		}
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(new DummyFrame(question), pf, question, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (okCxl == JOptionPane.OK_OPTION) {
			return new String(pf.getPassword());
		}
		return null;
	}
}
