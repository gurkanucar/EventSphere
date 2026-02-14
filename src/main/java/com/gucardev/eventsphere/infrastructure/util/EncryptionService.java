package com.gucardev.eventsphere.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

@Slf4j
@Service
public class EncryptionService {

    private final SecretKey encryptionKey;
    private final Base64 base64Codec;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int AES_KEY_LENGTH_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public EncryptionService(@Value("${app-specific-configs.security.encryption.secret}") String encryptionKeyString) {
        this.base64Codec = new Base64();

        try {
            byte[] decodedKey = base64Codec.decode(encryptionKeyString);

            if (decodedKey.length != AES_KEY_LENGTH_BYTES) {
                throw new IllegalArgumentException("Invalid encryption key length. Expected " + AES_KEY_LENGTH_BYTES + " bytes.");
            }

            this.encryptionKey = new SecretKeySpec(decodedKey, "AES");
            log.info("EncryptionService initialized successfully");
        } catch (IllegalArgumentException e) {
            log.error("Failed to initialize encryption key", e);
            throw new TokenEncryptionException("Failed to initialize encryption key.", e);
        }
    }

    public String encryptToken(String token) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, parameterSpec);

            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            byte[] cipherText = cipher.doFinal(tokenBytes);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            byte[] encryptedBytes = byteBuffer.array();

            return base64Codec.encodeToString(encryptedBytes);

        } catch (GeneralSecurityException e) {
            log.error("Error encrypting token", e);
            throw new TokenEncryptionException("Error encrypting token", e);
        }
    }

    public String decryptToken(String encryptedToken) {
        try {
            byte[] decodedToken = base64Codec.decode(encryptedToken);

            if (decodedToken.length < GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted token: too short");
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedToken);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, parameterSpec);

            byte[] decryptedBytes = cipher.doFinal(cipherText);

            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (GeneralSecurityException | IllegalArgumentException e) {
            log.debug("Error decrypting token: {}", e.getMessage());
            throw new TokenEncryptionException("Error decrypting token. It may be invalid or tampered.", e);
        }
    }

    public static class TokenEncryptionException extends RuntimeException {
        public TokenEncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}