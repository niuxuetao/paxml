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
package org.paxml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.core.PaxmlRuntimeException;

public class CryptoUtils {

	public static final String DEFAULT_KEY_STORE_NAME = "";
	public static final String DEFAULT_KEY_NAME = "";
	public static final String KEY_STORE_TYPE = "JCEKS";
	public static final String KEY_STORE_EXT = KEY_STORE_TYPE.toLowerCase();
	public static final int KEY_LENGTH = 128;
	public static final String KEY_STORE_FOLDER = "keys";
	public static final String KEY_TYPE = "AES";
	public static final String KEY_VALUE_ENCODING = "UTF-8";

	private static final RWTaskExecutor keyStoreExecutor = new RWTaskExecutor();

	// the below thread-unsafe cache will be made thread-safe by using the
	// keyStoreExecutor
	// above.
	private static final Map<String, KeyStore> keyStoreCache = new HashMap<String, KeyStore>();

	private static SecretKey getSecretKey(String keyValue) {
		byte[] b;
		try {
			b = keyValue.getBytes(KEY_VALUE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new PaxmlRuntimeException(e);
		}

		return new SecretKeySpec(b, KEY_TYPE);

	}

	public static byte[] encrypt(String data, String password) {

		SecretKey SecKey = getSecretKey(password);
		try {
			KeyGenerator KeyGen = KeyGenerator.getInstance(KEY_TYPE);
			KeyGen.init(KEY_LENGTH);

			Cipher cipher = Cipher.getInstance(KEY_TYPE);

			byte[] clear = data.getBytes(KEY_VALUE_ENCODING);

			cipher.init(Cipher.ENCRYPT_MODE, SecKey);
			return cipher.doFinal(clear);
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}

	}

	public static String decrypt(byte[] data, String password) {
		SecretKey SecKey = getSecretKey(password);
		try {
			KeyGenerator KeyGen = KeyGenerator.getInstance(KEY_TYPE);
			KeyGen.init(128);

			Cipher cipher = Cipher.getInstance(KEY_TYPE);

			cipher.init(Cipher.DECRYPT_MODE, SecKey);
			byte[] clear = cipher.doFinal(data);
			return new String(clear, KEY_VALUE_ENCODING);
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}
	}

	private static File getKeyStoreFile(String keyStoreName) {
		if (StringUtils.isBlank(keyStoreName)) {
			keyStoreName = DEFAULT_KEY_STORE_NAME;
		}
		return PaxmlUtils.getFileUnderPaxmlHome(KEY_STORE_FOLDER + File.separatorChar + keyStoreName + "." + KEY_STORE_EXT, true);
	}

	public static String getKey(String keyStoreName, final String keyStorePassword, final String keyName, final String keyPassword) {
		final File file = getKeyStoreFile(keyStoreName);
		final String key = file.getAbsolutePath();
		return keyStoreExecutor.executeRead(key, new Callable<String>() {

			@Override
			public String call() throws Exception {
				KeyStore keyStore = getKeyStore(file, keyStorePassword);
				return getKey(keyStore, keyName, keyPassword == null ? keyStorePassword : keyPassword);
			}
		});

	}

	private static String getKey(KeyStore keyStore, String keyName, String keyPassword) {
		if (StringUtils.isBlank(keyName)) {
			keyName = DEFAULT_KEY_NAME;
		}
		PasswordProtection _keyPassword = new PasswordProtection(keyPassword.toCharArray());
		KeyStore.Entry entry;
		try {
			if (!keyStore.containsAlias(keyName)) {
				return null;
			}
			entry = keyStore.getEntry(keyName, _keyPassword);
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}
		SecretKey key = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
		try {
			return new String(key.getEncoded(), KEY_VALUE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new PaxmlRuntimeException(e);
		}

	}

	public static void setKey(String keyStoreName, final String keyStorePassword, final String keyName, final String keyPassword, final String keyValue) {
		final File file = getKeyStoreFile(keyStoreName);
		final String key = file.getAbsolutePath();
		keyStoreExecutor.executeWrite(key, new Callable<Void>() {

			@Override
			public Void call() throws Exception {

				KeyStore keyStore = getKeyStore(file, keyStorePassword);
				setKey(keyStore, keyName, keyPassword == null ? keyStorePassword : keyPassword, keyValue);
				saveKeyStore(file, keyStorePassword, keyStore);
				// update in the key store cache to propagate the changes to
				// other threads.
				keyStoreCache.put(key, keyStore);
				return null;
			}
		});
	}

	private static void setKey(KeyStore keyStore, String keyName, String keyPassword, String keyValue) {
		if (StringUtils.isBlank(keyName)) {
			keyName = DEFAULT_KEY_NAME;
		}
		try {
			SecretKey secretKey = getSecretKey(keyValue);

			KeyStore.SecretKeyEntry keyStoreEntry = new KeyStore.SecretKeyEntry(secretKey);
			PasswordProtection _keyPassword = new PasswordProtection(keyPassword.toCharArray());
			keyStore.setEntry(keyName, keyStoreEntry, _keyPassword);

		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}

	}

	private static void saveKeyStore(final File file, final String password, final KeyStore ks) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			ks.store(fos, password.toCharArray());
		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot write to key store file: " + file.getAbsolutePath(), e);
		} finally {
			IOUtils.closeQuietly(fos);
		}

	}

	private static KeyStore getKeyStore(final File file, final String password) {
		final String key = file.getAbsolutePath();

		KeyStore keyStore = keyStoreCache.get(key);
		if (keyStore != null) {
			return keyStore;
		}

		final char[] pwd = password.toCharArray();
		if (!file.exists()) {
			FileOutputStream fos = null;
			try {
				file.getParentFile().mkdirs();
				fos = new FileOutputStream(file);
				// .keystore file not created yet => create it
				keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
				keyStore.load(null, null);
				keyStore.store(fos, pwd);
			} catch (Exception e) {
				throw new PaxmlRuntimeException("Cannot create new key store file: " + key, e);
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(file);
			keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
			// .keystore file already exists => load it
			keyStore.load(fis, pwd);

		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot read from key store file: " + key, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}
		// put to concurrent cache to propagate the key store to other
		// threads
		keyStoreCache.put(key, keyStore);
		return keyStoreCache.get(key);

	}

}
