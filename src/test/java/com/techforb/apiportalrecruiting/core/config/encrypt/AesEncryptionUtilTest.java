package com.techforb.apiportalrecruiting.core.config.encrypt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class AesEncryptionUtilTest {

    @Autowired
    private AesEncryptionUtil aesEncryptionUtil;

    @Test
    void encrypt_shouldReturnEncryptedText() {
        String plainText = "hola-mundo";

        String encrypted = aesEncryptionUtil.encrypt(plainText);

        Assertions.assertNotNull(encrypted);
        Assertions.assertNotEquals(plainText, encrypted);
    }

    @Test
    void decrypt_shouldReturnOriginalText() {
        String plainText = "hola-mundo";

        String encrypted = aesEncryptionUtil.encrypt(plainText);
        String decrypted = aesEncryptionUtil.decrypt(encrypted);

        Assertions.assertEquals(plainText, decrypted);
    }

    @Test
    void decrypt_shouldFailWithInvalidInput() {
        Assertions.assertThrows(RuntimeException.class, () ->
                aesEncryptionUtil.decrypt("texto-no-base64")
        );
    }
}
