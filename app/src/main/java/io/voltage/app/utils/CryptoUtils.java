package io.voltage.app.utils;

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
            return digest(transactions.getMsgUuids(), Algorithm.SHA_256);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
    }

    public static String attemptAesEncrypt(final String keyString, final String text) {
        try {
            if (text == null) return null;
            return encrypt(decodeSecretKey(keyString), text, Algorithm.AES_CBC);
        } catch (final Exception e) {
            Logger.ex(e);
            return text;
        }
    }

    public static String attemptAesDecrypt(final String keyString, final String encrypted) {
        try {
            if (encrypted == null) return null;
            return decrypt(decodeSecretKey(keyString), encrypted, Algorithm.AES_CBC);
        } catch (final Exception e) {
            Logger.ex(e);
            return encrypted;
        }
    }

    public static String attemptRsaEncrypt(final String keyString, final String text) {
        try {
            if (text == null) return null;
            return encrypt(decodePublicKey(keyString), text, Algorithm.RSA);
        } catch (final Exception e) {
            Logger.ex(e);
            return text;
        }
    }


    public static String attemptRsaDecrypt(final String keyString, final String encrypted) {
        try {
            if (encrypted == null) return null;
            return decrypt(decodePrivateKey(keyString), encrypted, Algorithm.RSA);
        } catch (final Exception e) {
            Logger.ex(e);
            return encrypted;
        }
    }

    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator rsa = KeyPairGenerator.getInstance(Algorithm.RSA);
            return rsa.generateKeyPair();
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
            return Base64.encodeToString(key, Base64.NO_WRAP);
        } catch (final Exception e) {
            Logger.ex(e);
            return null;
        }
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
        return factory.generatePrivate(new X509EncodedKeySpec(decoded));
    }

    private static String encrypt(final Key key, final String text, final String algorithm) throws Exception {
        final byte[] input = text.getBytes(UTF8_CHARSET);
        final Cipher cipher = Cipher.getInstance(algorithm);
        final byte[] iv = generateVector();
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        final byte[] bytes = cipher.doFinal(input);
        return Base64.encodeToString(concat(iv, bytes), Base64.NO_WRAP);
    }

    private static String decrypt(final Key key, final String encrypted, final String algorithm) throws Exception {
        final byte[] input = Base64.decode(encrypted, Base64.NO_WRAP);
        final Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(vectorRange(input)));
        final byte[] bytes = cipher.doFinal(payloadRange(input));
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

    private static String digest(final String input, final String algorithm) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(input.getBytes(UTF8_CHARSET));
        return Base64.encodeToString(digest.digest(), Base64.NO_WRAP);
    }


    // =================================================


//    public static String createUuid(final String message) {
//        final byte[] bytes = encode(message, Algorithm.MD5);
//        final StringBuilder hexString = toHexadecimal(bytes);
//        hexString.insert('-', 8).insert('-', 12);
//        hexString.insert('-', 16).insert('-', 20);
//        return StringUtils.left(hexString.toString(), 36);
//    }
//
//    private static StringBuilder toHexadecimal(final byte[] bytes) {
//        final StringBuilder hexString = new StringBuilder();
//        for (final byte b : bytes) {
//            hexString.append(Integer.toHexString(0xFF & b));
//        }
//        return hexString;
//    }

}
