package com.example.pos.domain.repository.key

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.pos.core.crypto.CryptoManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.PublicKey
import java.security.SecureRandom
import javax.inject.Inject
import com.example.pos.core.crypto.KeyGenerator

class KeyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
    private val keyGenerator: KeyGenerator
): KeyRepository {

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("crypto_keys", Context.MODE_PRIVATE)
    }

    private var currentHmacKey: ByteArray? = null
    private var keyIndex: Int = 0

    override suspend fun getServerPublicKey(): PublicKey {
        return keyGenerator.getPublicKey()
    }

    override suspend fun getCurrentHmacKey(): ByteArray {
        return currentHmacKey ?: loadOrGenerateHmacKey()
    }

    override suspend fun rotateHmacKey(): Boolean {
        return try {
            val newKey = generateNewHmacKey()
            currentHmacKey = newKey
            keyIndex++
            saveKeyToPreferences(newKey, keyIndex)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun generateSessionKey(): ByteArray {
        return cryptoManager.generateAesKey()
    }

    override fun getCurrentKeyIndex(): Int = keyIndex

    private fun loadOrGenerateHmacKey(): ByteArray {
        val savedKey = sharedPrefs.getString("hmac_key", null)
        val savedIndex = sharedPrefs.getInt("key_index", 0)

        return if (savedKey != null) {
            keyIndex = savedIndex
            Base64.decode(savedKey, Base64.DEFAULT)
        } else {
            val newKey = generateNewHmacKey()
            keyIndex = 1
            saveKeyToPreferences(newKey, keyIndex)
            newKey
        }.also { currentHmacKey = it }
    }

    private fun generateNewHmacKey(): ByteArray {
        return ByteArray(32).also {
            SecureRandom().nextBytes(it)
        }
    }

    private fun saveKeyToPreferences(key: ByteArray, index: Int) {
        sharedPrefs.edit()
            .putString("hmac_key", Base64.encodeToString(key, Base64.DEFAULT))
            .putInt("key_index", index)
            .apply()
    }

}