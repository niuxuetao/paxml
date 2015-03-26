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
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.paxml.core.PaxmlRuntimeException;

import com.thoughtworks.xstream.core.util.Base64Encoder;

public class CryptoUtils {

	public static final String DEFAULT_KEY_STORE_NAME = "";
	public static final String DEFAULT_KEY_NAME = "";
	public static final String KEY_STORE_TYPE = "JCEKS";
	public static final String KEY_STORE_EXT = KEY_STORE_TYPE.toLowerCase();
	public static final int KEY_LENGTH_BITS = 128;
	public static final int KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8;
	public static final String KEY_STORE_FOLDER = "keys";
	public static final String KEY_TYPE = "AES";
	public static final String HASH_TYPE = "SHA-1";
	public static final String KEY_VALUE_ENCODING = "UTF-8";
	private static final String DEFAULT_KEY_PASSWORD = "key_pass";

	private static final RWTaskExecutor keyStoreExecutor = new RWTaskExecutor();

	private static SecretKey getSecretKey(String keyValue) {

		byte[] b;
		try {
			MessageDigest sha = MessageDigest.getInstance(HASH_TYPE);
			b = sha.digest(keyValue.getBytes(KEY_VALUE_ENCODING));
		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}
		byte[] kb = new byte[KEY_LENGTH_BYTES];
		// take the left 16 bytes part of the sha-1
		System.arraycopy(b, 0, kb, 0, kb.length);

		return new SecretKeySpec(kb, KEY_TYPE);

	}

	public static String base64Encode(byte[] data) {
		return new Base64Encoder().encode(data);
	}

	public static byte[] base64Decode(String data) {
		return new Base64Encoder().decode(data);
	}

	public static String hexEncode(byte[] data) {
		return new String(Hex.encodeHex(data));
	}

	public static byte[] hexDecode(String data) {
		try {
			return Hex.decodeHex(data.toCharArray());
		} catch (DecoderException e) {
			throw new PaxmlRuntimeException(e);
		}
	}

	public static byte[] encrypt(String data, String password) {

		SecretKey SecKey = getSecretKey(password);
		try {
			KeyGenerator KeyGen = KeyGenerator.getInstance(KEY_TYPE);
			KeyGen.init(KEY_LENGTH_BITS);

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
			KeyGen.init(KEY_LENGTH_BITS);

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

	public static void changeKeyStorePassword(String keyStoreName, final String oldPassword, final String newPassword) {
		final File file = getKeyStoreFile(keyStoreName);
		final String key = file.getAbsolutePath();
		keyStoreExecutor.executeWrite(key, new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				KeyStore keyStore = getKeyStore(file, oldPassword);
				saveKeyStore(file, newPassword, keyStore);
				return null;
			}
		});
	}

	public static String getKey(String keyStoreName, final String keyStorePassword, final String keyName, final String keyPassword) {
		final File file = getKeyStoreFile(keyStoreName);
		final String key = file.getAbsolutePath();
		return keyStoreExecutor.executeRead(key, new Callable<String>() {

			@Override
			public String call() throws Exception {
				KeyStore keyStore = getKeyStore(file, keyStorePassword);
				return getKey(keyStore, keyName, keyPassword);
			}
		});

	}

	private static String getKey(KeyStore keyStore, String keyName, String keyPassword) {
		if (StringUtils.isBlank(keyName)) {
			keyName = DEFAULT_KEY_NAME;
		}
		if (keyPassword == null) {
			keyPassword = DEFAULT_KEY_PASSWORD;
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

	public static boolean deleteKeyStore(String keyStoreName) {
		final File file = getKeyStoreFile(keyStoreName);
		return keyStoreExecutor.executeWrite(file.getAbsolutePath(), new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return file.delete();
			}

		});

	}

	public static void deleteKey(String keyStoreName, final String keyStorePassword, final String keyName) {
		final File file = getKeyStoreFile(keyStoreName);
		keyStoreExecutor.executeWrite(file.getAbsolutePath(), new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				deleteKey(getKeyStore(file, keyStorePassword), keyName);
				return null;
			}

		});
	}

	private static void deleteKey(KeyStore keyStore, String keyName) {
		try {
			if (keyStore.containsAlias(keyName)) {
				keyStore.deleteEntry(keyName);
			}
		} catch (KeyStoreException e) {
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
				setKey(keyStore, keyName, keyPassword, keyValue);
				saveKeyStore(file, keyStorePassword, keyStore);
				return null;
			}
		});
	}

	private static void setKey(KeyStore keyStore, String keyName, String keyPassword, String keyValue) {
		if (StringUtils.isBlank(keyName)) {
			keyName = DEFAULT_KEY_NAME;
		}
		if (keyPassword == null) {
			keyPassword = DEFAULT_KEY_PASSWORD;
		}
		try {
			SecretKey secretKey = new SecretKeySpec(keyValue.getBytes(KEY_VALUE_ENCODING), KEY_TYPE);

			KeyStore.SecretKeyEntry keyStoreEntry = new KeyStore.SecretKeyEntry(secretKey);
			PasswordProtection _keyPassword = new PasswordProtection(keyPassword.toCharArray());
			keyStore.setEntry(keyName, keyStoreEntry, _keyPassword);

		} catch (Exception e) {
			throw new PaxmlRuntimeException(e);
		}

	}

	private static void saveKeyStore(final File file, final String password, final KeyStore ks) {
		file.delete();
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

		KeyStore keyStore;

		final char[] pwd = password.toCharArray();
		if (!file.exists()) {
			FileOutputStream fos = null;
			try {
				file.getParentFile().mkdirs();
				fos = new FileOutputStream(file);
				// keystore file not created yet => create it
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
			// keystore file already exists => load it
			keyStore.load(fis, pwd);

		} catch (Exception e) {
			throw new PaxmlRuntimeException("Cannot read from key store file: " + key, e);
		} finally {
			IOUtils.closeQuietly(fis);
		}

		return keyStore;

	}

}
