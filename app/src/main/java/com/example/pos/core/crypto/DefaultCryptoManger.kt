package com.example.pos.core.crypto

import java.security.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class DefaultCryptoManager @Inject constructor(
) : CryptoManager {

    companion object {
        private const val AES_KEY_SIZE = 256
        private const val GCM_TAG_LENGTH = 128
        private const val GCM_IV_LENGTH = 12
        private const val RSA_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }

    override fun generateHmac(data: ByteArray, key: ByteArray): ByteArray {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(key, "HmacSHA256")
            mac.init(secretKeySpec)
            mac.doFinal(data)
        } catch (e: Exception) {
            throw Exception("HMAC generation failed", e)
        }
    }

    override fun verifyHmac(data: ByteArray, key: ByteArray, hmacToVerify: ByteArray) : Boolean {
        val calculatedHmac = generateHmac(data, key)
        return MessageDigest.isEqual(calculatedHmac, hmacToVerify)
    }

    override fun encryptAesGcm(data: ByteArray, key: ByteArray): Pair<ByteArray, ByteArray> {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val iv = ByteArray(GCM_IV_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            val secretKey = SecretKeySpec(key, "AES")

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)
            val encryptedData = cipher.doFinal(data)

            Pair(encryptedData, iv)
        } catch (e: Exception) {
            throw Exception("AES-GCM encryption failed", e)
        }
    }

    override fun decryptAesGcm(encryptedData: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            val secretKey = SecretKeySpec(key, "AES")

            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)
            cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            throw Exception("AES-GCM decryption failed", e)
        }
    }

    override fun encryptRsa(data: ByteArray, publicKey: PublicKey): ByteArray {
        return try {
            val cipher = Cipher.getInstance(RSA_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            cipher.doFinal(data)
        } catch (e: Exception) {
            throw Exception("RSA encryption failed", e)
        }
    }

   override fun decryptRsa(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray {
        return try {
            val cipher = Cipher.getInstance(RSA_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            throw Exception("RSA decryption failed", e)
        }
    }

   override fun generateAesKey(): ByteArray {
        return ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }
    }

//   override fun loadPublicKeyFromPem(pemString: String): PublicKey {
//        return try {
//            val publicKeyPem = pemString
//                .replace("-----BEGIN PUBLIC KEY-----", "")
//                .replace("-----END PUBLIC KEY-----", "")
//                .replace("\\s".toRegex(), "")
//
//            val keyBytes = Base64.decode(publicKeyPem, Base64.DEFAULT)
//            val keySpec = X509EncodedKeySpec(keyBytes)
//            val keyFactory = KeyFactory.getInstance("RSA")
//            keyFactory.generatePublic(keySpec)
//        } catch (e: Exception) {
//            throw Exception("Failed to load public key from PEM", e)
//        }
//    }
//
    override fun loadPublicKeyFromPem(pemString: String): PublicKey {

        throw UnsupportedOperationException("Use KeyGenerator instead")
}
}