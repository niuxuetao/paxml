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
