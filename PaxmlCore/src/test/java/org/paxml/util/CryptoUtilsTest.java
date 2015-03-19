package org.paxml.util;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.paxml.core.Context;

public class CryptoUtilsTest {
	private static final String store = UUID.randomUUID().toString();

	@Test
	public void testEncryption() {
		String clear = "clear text 123";
		String key = "pwd";

		String encrypted = CryptoUtils.base64Encode(CryptoUtils.encrypt(clear, key));

		String decrypted = CryptoUtils.decrypt(CryptoUtils.base64Decode(encrypted), key);

		Assert.assertEquals(clear, decrypted);
	}

	@Test
	public void testKeyStore() {

		final String pwd = "pass";
		final String keyName = "kn";
		final String keyValue = "kv";

		CryptoUtils.setKey(store, pwd, keyName, null, keyValue);
		String got = CryptoUtils.getKey(store, pwd, keyName, null);

		Assert.assertEquals(keyValue, got);
	}

	@Test
	public void testContextSecret() {
		//CryptoUtils.deleteKeyStore(CryptoUtils.DEFAULT_KEY_STORE_NAME);
		final String keyName = "keyName";
		final String keyValue = "keyValue";

		Context.setSecret(keyName, keyValue);
		String got = Context.getSecret(keyName).getDecrypted();
		Assert.assertEquals(keyValue, got);
		
		Context.setSecret(keyName, keyValue+keyValue);
		got = Context.getSecret(keyName).getDecrypted();
		Assert.assertEquals(keyValue+keyValue, got);
		
		//CryptoUtils.deleteKeyStore(CryptoUtils.DEFAULT_KEY_STORE_NAME);
	}

	@After
	@Before
	public void cleanup() {
		CryptoUtils.deleteKeyStore(store);
	}
}
