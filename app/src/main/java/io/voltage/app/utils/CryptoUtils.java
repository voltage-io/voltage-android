package io.voltage.app.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.voltage.app.models.Transactions;

public class CryptoUtils {

    private interface Algorithm {
        String RSA = "RSA";
        String MD5 = "MD5";
        String AES = "AES";
        String AES_CBC = "AES/CBC/PKCS7Padding";
        String SHA_256 = "SHA-256";
        String SHA1_PRNG = "SHA1PRNG";
    }

    private static final int KEY_LENGTH = 256;
    private static final int VECTOR_LENGTH = 16;
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");


    public static String checksum(final Transactions transactions) {
        try {
            if (transactions == null) return null;
            return shasum(transactions.getMsgUuids());
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    public static String attemptAesEncrypt(final String keyString, final String text) {
        try {
            if (TextUtils.isEmpty(text)) return null;
            Logger.d("[AES_ENCRYPT text]: " + text);
            Logger.d("[AES_ENCRYPT keyString]: " + keyString);
            final SecretKeySpec key = decodeSecretKey(keyString);
            final String encrypted = encryptAes(key, text);
            Logger.d("[AES_ENCRYPT encrypted]: " + encrypted);
            return encrypted;
        } catch (final Exception e) {
            Logger.ex(e);
            return text;
        }
    }

    public static String attemptAesDecrypt(final String keyString, final String encrypted) {
        try {
            if (TextUtils.isEmpty(encrypted)) return null;
            Logger.d("[AES_DECRYPT encrypted]: " + encrypted);
            Logger.d("[AES_DECRYPT keyString]: " + keyString);
            final SecretKeySpec key = decodeSecretKey(keyString);
            final String decrypted = decryptAes(key, encrypted);
            Logger.d("[AES_DECRYPT decrypted]: " + decrypted);
            return decrypted;
        } catch (final Exception e) {
            Logger.ex(e);
            return encrypted;
        }
    }

    public static String attemptRsaEncrypt(final String keyString, final String text) {
        try {
            if (TextUtils.isEmpty(text)) return null;
            Logger.d("[RSA_ENCRYPT text]: " + text);
            Logger.d("[RSA_ENCRYPT keyString]: " + keyString);
            final PublicKey key = decodePublicKey(keyString);
            final String encrypted = encryptRsa(key, text);
            Logger.d("[RSA_ENCRYPT encrypted]: " + encrypted);
            return encrypted;
        } catch (final Exception e) {
            Logger.ex(e);
            return text;
        }
    }

    public static String attemptRsaDecrypt(final String keyString, final String encrypted) {
        try {
            if (TextUtils.isEmpty(encrypted)) return null;
            Logger.d("[RSA_DECRYPT encrypted]: " + encrypted);
            Logger.d("[RSA_DECRYPT keyString]: " + keyString);
            final PrivateKey key = decodePrivateKey(keyString);
            final String decrypted = decryptRsa(key, encrypted);
            Logger.d("[RSA_DECRYPT decrypted]: " + decrypted);
            return decrypted;
        } catch (final Exception e) {
            Logger.ex(e);
            return encrypted;
        }
    }

    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator rsa = KeyPairGenerator.getInstance(Algorithm.RSA);
            final KeyPair keyPair = rsa.generateKeyPair();
            Logger.d("[GENERATED PRIVATE_KEY]: " + Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.NO_WRAP));
            Logger.d("[GENERATED PUBLIC_KEY]: " + Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP));
            return keyPair;
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    public static String generateThreadKey() {
        try {
            final KeyGenerator generator = KeyGenerator.getInstance(Algorithm.AES);
            generator.init(KEY_LENGTH);
            final byte[] key = generator.generateKey().getEncoded();
            final String threadKey = Base64.encodeToString(key, Base64.NO_WRAP);
            Logger.d("[GENERATED THREAD_KEY]: " + threadKey);
            return threadKey;
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    private static String shasum(final String input) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance(Algorithm.SHA_256);
        digest.update(input.getBytes(UTF8_CHARSET));
        return Base64.encodeToString(digest.digest(), Base64.NO_WRAP);
    }

    private static SecretKeySpec decodeSecretKey(final String keyString) throws Exception {
        final byte[] decoded = Base64.decode(keyString, Base64.NO_WRAP);
        return new SecretKeySpec(decoded, Algorithm.AES_CBC);
    }

    private static PublicKey decodePublicKey(final String keyString) throws Exception {
        final byte[] decoded = Base64.decode(keyString, Base64.NO_WRAP);
        final KeyFactory factory = KeyFactory.getInstance(Algorithm.RSA);
        return factory.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private static PrivateKey decodePrivateKey(final String keyString) throws Exception {
        final byte[] decoded = Base64.decode(keyString, Base64.NO_WRAP);
        final KeyFactory factory = KeyFactory.getInstance(Algorithm.RSA);
        return factory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private static String encryptAes(final Key key, final String text) throws Exception {
        final byte[] input = text.getBytes(UTF8_CHARSET);
        final Cipher cipher = Cipher.getInstance(Algorithm.AES_CBC);
        final byte[] iv = generateVector();
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        final byte[] bytes = cipher.doFinal(input);
        return Base64.encodeToString(concat(iv, bytes), Base64.NO_WRAP);
    }

    private static String decryptAes(final Key key, final String encrypted) throws Exception {
        final byte[] input = Base64.decode(encrypted, Base64.NO_WRAP);
        final Cipher cipher = Cipher.getInstance(Algorithm.AES_CBC);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(vectorRange(input)));
        final byte[] bytes = cipher.doFinal(payloadRange(input));
        return new String(bytes, UTF8_CHARSET);
    }

    private static String encryptRsa(final Key key, final String text) throws Exception {
        final byte[] input = text.getBytes(UTF8_CHARSET);
        final Cipher cipher = Cipher.getInstance(Algorithm.RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        final byte[] bytes = cipher.doFinal(input);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static String decryptRsa(final Key key, final String encrypted) throws Exception {
        final byte[] input = Base64.decode(encrypted, Base64.NO_WRAP);
        final Cipher cipher = Cipher.getInstance(Algorithm.RSA);
        cipher.init(Cipher.DECRYPT_MODE, key);
        final byte[] bytes = cipher.doFinal(input);
        return new String(bytes, UTF8_CHARSET);
    }

    private static byte[] generateVector() throws Exception {
        final SecureRandom random = SecureRandom.getInstance(Algorithm.SHA1_PRNG);
        final byte[] iv = new byte[VECTOR_LENGTH];
        random.nextBytes(iv);
        return iv;
    }

    private static byte[] vectorRange(final byte[] input) {
        return Arrays.copyOfRange(input, 0, VECTOR_LENGTH);
    }

    private static byte[] payloadRange(final byte[] input) {
        return Arrays.copyOfRange(input, VECTOR_LENGTH, input.length);
    }

    private static byte[] concat(final byte[]... inputs) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (final byte[] bytes : inputs) {
            output.write(bytes);
        }
        return output.toByteArray();
    }
}
