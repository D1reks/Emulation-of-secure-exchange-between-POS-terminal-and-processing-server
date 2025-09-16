package com.example.pos.data.remote.DTO

data class TransactionResponse(
    val status: String,
    val authCode: String?,
    val timeStamp: Long
)