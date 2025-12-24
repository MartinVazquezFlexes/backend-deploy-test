package com.techforb.apiportalrecruiting.core.config.encrypt;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AesEncryptionUtil {

	private static final String ALGORITHM = "AES";

	private String secretKey; // No static, camelCase

	@Value("${SECRET_KEY_AES}")
	public void setSecretKey(String key) {
		this.secretKey = key;
	}

	public String encrypt(String plainText) { // No static
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting data", e);
		}
	}

	public String decrypt(String encryptedText) { // No static
		try {
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
			return new String(decryptedBytes);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting data", e);
		}
	}
}
