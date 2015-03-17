package org.paxml.user;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.paxml.util.CryptoUtils;

public class UserKeyRepository {

	private static final ConcurrentMap<String, String> cachedKeys = new ConcurrentHashMap<String, String>();

	public static String getCurrentUserMasterKey() {
		final String currentUser = "";
		String key = cachedKeys.get(currentUser);
		if (key == null) {
			key = askForCurrentUserMasterKey();
			if (key != null) {
				key = CryptoUtils.base64Encode(CryptoUtils.encrypt(key, currentUser));
				cachedKeys.put(currentUser, key);
			}

		}
		if (key == null) {
			return null;
		}
		return CryptoUtils.decrypt(CryptoUtils.base64Decode(key), currentUser);
	}

	public static String askForCurrentUserMasterKey() {
		String title = "Pease enter key store password";
		class DummyFrame extends JFrame {
			DummyFrame(String title) {
				super(title);
				setUndecorated(true);
				setVisible(true);
				setLocationRelativeTo(null);
			}
		}
		JPasswordField pf = new JPasswordField();
		int okCxl = JOptionPane.showConfirmDialog(new DummyFrame(title), pf, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (okCxl == JOptionPane.OK_OPTION) {
			return new String(pf.getPassword());
		}
		return null;
	}
}
