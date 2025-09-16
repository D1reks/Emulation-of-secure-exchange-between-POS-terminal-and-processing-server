package com.example.pos.core.crypto

import java.security.PrivateKey
import java.security.PublicKey

interface CryptoManager {
    fun generateHmac(data: ByteArray, key: ByteArray): ByteArray
    fun verifyHmac(data: ByteArray, key: ByteArray, hmacToVerify: ByteArray): Boolean
    fun encryptAesGcm(data: ByteArray, key: ByteArray): Pair<ByteArray, ByteArray>
    fun decryptAesGcm(encryptedData: ByteArray, key: ByteArray, iv: ByteArray): ByteArray
    fun encryptRsa(data: ByteArray, publicKey: PublicKey): ByteArray
    fun decryptRsa(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray
    fun generateAesKey(): ByteArray
    fun loadPublicKeyFromPem(pemString: String): PublicKey
}

