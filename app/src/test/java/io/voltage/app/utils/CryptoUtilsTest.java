package io.voltage.app.utils;

import android.util.Base64;

import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CryptoUtilsTest {

    @Before
    public void setUp() throws Exception {
        Logger.level(Logger.Level.NONE);

        // need to mock Base64.encodeToString
    }

    @Test
    public void testAesEncryption() {
        final String text = "hello";
        final String threadKey = CryptoUtils.generateThreadKey();

        assertNotNull(threadKey);

        final String encrypted = CryptoUtils.attemptAesEncrypt(threadKey, text);
        final String decrypted = CryptoUtils.attemptAesDecrypt(threadKey, encrypted);

        assertEquals(text, decrypted);
    }

    @Test
    public void testRsaEncryption() {
        final String text = "hello";
        final KeyPair keyPair = CryptoUtils.generateKeyPair();

        assertNotNull(keyPair);

        final String publicKey = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);
        final String privateKey = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.NO_WRAP);

        final String encrypted = CryptoUtils.attemptRsaEncrypt(publicKey, text);
        final String decrypted = CryptoUtils.attemptRsaDecrypt(privateKey, encrypted);

        assertEquals(text, decrypted);
    }
}
