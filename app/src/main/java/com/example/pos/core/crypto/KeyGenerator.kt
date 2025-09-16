package com.example.pos.core.crypto

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyGenerator @Inject constructor() {

    private val keyPair: KeyPair by lazy {
        generateRsaKeyPair()
    }

    fun getPublicKey(): PublicKey {
        return keyPair.public
    }

    fun getPrivateKey(): PrivateKey {
        return keyPair.private
    }

    private fun generateRsaKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }
}