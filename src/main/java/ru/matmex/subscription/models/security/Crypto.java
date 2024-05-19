package ru.matmex.subscription.models.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.matmex.subscription.services.impl.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Шифровальщик персональных данных
 */
@Component
public class Crypto {
    private final Cipher encryptCipher;
    private final Cipher decryptCipher;
    private final SecretKeySpec secretKey;

    public Crypto(@Value("${crypto.secretkey}") String rawSecretKey) {
        this.secretKey = new SecretKeySpec(rawSecretKey.getBytes(), "AES");
        encryptCipher = initCipher(Cipher.ENCRYPT_MODE);
        decryptCipher = initCipher(Cipher.DECRYPT_MODE);
    }

    /**
     * Зашифровать данные
     *
     * @param plainText исходные текст в виде последоватлельности байт
     * @return зашифрованный текст в виде массив байт
     */
    public byte[] encrypt(byte[] plainText) {
        try {
            return encryptCipher.doFinal(plainText);
        } catch (Exception e) {
            throw new CryptoException("Не удалось зашифровать данные!");
        }
    }

    /**
     * Расшифровать текст
     *
     * @param encryptedText - зашифрованный текст
     * @return расшифрованный текст в виде массива байт
     */
    public byte[] decrypt(byte[] encryptedText) {
        try {
            return decryptCipher.doFinal(encryptedText);
        } catch (Exception e) {
            throw new CryptoException("Не удалось расшифровать данные!");
        }
    }

    /**
     * Инициализировать шифр
     * @param mode - тип шифрования
     * @return - инициализированный шифр
     */
    private Cipher initCipher(int mode) {

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(mode, secretKey);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CryptoException("Не удалось создать шифр");
        }
    }
}
