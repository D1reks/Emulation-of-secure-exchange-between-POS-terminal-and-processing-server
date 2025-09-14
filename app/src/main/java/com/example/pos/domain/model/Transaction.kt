package com.example.pos.domain.model

import java.util.UUID

data class Transaction (
    val id: String = UUID.randomUUID().toString(),
    val cardPlan: String,
    val amount: Long,
    val merchantId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: TransactionStatus? = null,
    val authCode: String? = null
)

enum class TransactionStatus {APPROVED, DECLINED, PENDING, TIMEOUT}