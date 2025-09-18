package com.example.pos

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pos.core.crypto.CryptoManager
import com.example.pos.core.crypto.DefaultCryptoManager
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyPairGenerator
import java.security.SecureRandom

@RunWith(AndroidJUnit4::class)
class CryptoIntegrationTest {

    private lateinit var cryptoManager: CryptoManager

    private val secureRandom = SecureRandom()

    @Before
    fun setup() {
        cryptoManager = DefaultCryptoManager()
    }

    @Test
    fun testHmacGenerationAndVerification() {
        val testData = "test data".toByteArray()
        val key = ByteArray(32).apply {
            secureRandom.nextBytes(this)
        }

        val hmac = cryptoManager.generateHmac(testData, key)

        val wrongKey = ByteArray(32).apply { secureRandom.nextBytes(this) }
        assertFalse("HMAC verification should fail with wrong key",
            cryptoManager.verifyHmac(testData, wrongKey, hmac))

        assertTrue("HMAC should not be empty", hmac.isNotEmpty())
        assertTrue("HMAC verification should pass",
            cryptoManager.verifyHmac(testData, key, hmac))
    }

    @Test
    fun testAesEncryptionDecryption() {
        val originalData = "secret message".toByteArray()
        val key = ByteArray(32).apply {
            secureRandom.nextBytes(this)
        }

        val (encryptedData, iv) = cryptoManager.encryptAesGcm(originalData, key)
        val decryptedData = cryptoManager.decryptAesGcm(encryptedData, key, iv)

        assertEquals("Decrypted data should match original",
            originalData.decodeToString(),
            decryptedData.decodeToString())
    }

    @Test
    fun testRsaEncryption() {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()

        val data = "session key".toByteArray()
        val encrypted = cryptoManager.encryptRsa(data, keyPair.public)

        assertTrue("Encrypted data should not be empty", encrypted.isNotEmpty())
        assertFalse("Encrypted data should differ from original",
            data.contentEquals(encrypted))
    }
}