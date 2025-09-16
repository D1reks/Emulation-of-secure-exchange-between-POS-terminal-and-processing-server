package com.example.pos

import com.example.pos.core.crypto.DefaultCryptoManager
import junit.framework.TestCase.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest

class CryptoTests {

    @Test
    fun testHmacSha256() {
        val cryptoManager = DefaultCryptoManager()
        val testData = "test data".toByteArray()
        val testKey = "secret key".toByteArray()

        val hmac1 = cryptoManager.generateHmac(testData, testKey)
        val hmac2 = cryptoManager.generateHmac(testData, testKey)

        // step (Два вычисления с одинаковыми данными должны дать одинаковый HMAC)
        assertTrue(MessageDigest.isEqual(hmac1, hmac2))

        // step (Измененные данные должны дать другой HMAC)
        val differentData = "test datb".toByteArray()
        val hmac3 = cryptoManager.generateHmac(differentData, testKey)
        assertFalse(MessageDigest.isEqual(hmac1, hmac3))

        // step (Разные ключи должны дать разные HMAC)
        val differentKey = "secret kez".toByteArray()
        val hmac4 = cryptoManager.generateHmac(testData, differentKey)
        assertFalse(MessageDigest.isEqual(hmac1, hmac4))
    }

}