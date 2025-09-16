package com.example.pos.domain.repository.key

import java.security.PublicKey

interface KeyRepository {
    suspend fun getServerPublicKey(): PublicKey
    suspend fun getCurrentHmacKey(): ByteArray
    suspend fun rotateHmacKey(): Boolean
    fun generateSessionKey(): ByteArray
    fun getCurrentKeyIndex(): Int
}