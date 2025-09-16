package com.example.pos.data.remote.DTO

data class EncryptedTransactionRequest(
    val encryptedSessionKey: String,
    val iv: String,
    val hmac: String,
    val encryptedData: String
)