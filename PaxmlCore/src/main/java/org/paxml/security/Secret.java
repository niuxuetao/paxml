package org.paxml.security;

import org.paxml.util.CryptoUtils;

public class Secret {

	private final String encrypted;
	private final String name;

	public Secret(String name, String clearSecret) {
		encrypted = CryptoUtils.base64Encode(CryptoUtils.encrypt(clearSecret, SecretRepository.getCurrentUserMasterKey()));
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getEncrypted() {
		return encrypted;
	}

	public String getDecrypted() {
		return CryptoUtils.decrypt(CryptoUtils.base64Decode(encrypted), SecretRepository.getCurrentUserMasterKey());
	}

	@Override
	public String toString() {
		return "******";
	}
}
