package com.example.pos.data.remote.DTO

data class TransactionRequest(
    val cardPan: String,
    val amount: Long,
    val merchantId: String,
    val transactionId: String
)