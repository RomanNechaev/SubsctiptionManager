package ru.matmex.subscription.models.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Component
/**
 * Шифровальщик персональных данных
 */
public class Crypto {
    Cipher cipher;
    /**
     * Алгоритм шифрования
     */
    String transformation = "AES/ECB/PKCS5Padding";
    private static final Logger logger = LoggerFactory.getLogger(Crypto.class);
    private final SecretKeySpec secretKey;

    public Crypto(@Value("${crypto.secretkey}") String rawSecretKey) {
        this.secretKey = new SecretKeySpec(rawSecretKey.getBytes(), "AES");
    }

    /**
     * Зашифровать данные
     *
     * @param plainText исходные текст в виде последоватлельности байт
     * @return зашифрованный текст в виде массив байт
     */
    public byte[] encrypt(byte[] plainText) {
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(plainText);
        } catch (Exception e) {
            logger.error("Не удалось зашифровать данные!");
        }
        return null;
    }

    /**
     * Расшифровать текст
     *
     * @param encryptedText - зашифрованный текст
     * @return расшифрованный текст в виде массива байт
     */
    public byte[] decrypt(byte[] encryptedText) {
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(encryptedText);
        } catch (Exception e) {
            logger.error("Не удалось расшифровать данные!");
        }
        return null;
    }
}
