package org.paxml.util;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.Test;

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
		CryptoUtils.deleteKeyStore(store);
		CryptoUtils.setKey(store, pwd, keyName, null, keyValue);
		String got = CryptoUtils.getKey(store, pwd, keyName, null);
		
		Assert.assertEquals(keyValue, got);
	}
	@AfterClass
	public static void cleanup(){
		CryptoUtils.deleteKeyStore(store);
	}
}
