package com.example.pos.domain.repository

import com.example.pos.domain.model.Transaction

interface TransactionRepository {
    suspend fun sendTransaction(transaction: Transaction): Result<TransactionResposnse>
    suspend fun getTransactions(): List<Transaction>
    suspend fun requestNewKey(): Result<Unit>
}