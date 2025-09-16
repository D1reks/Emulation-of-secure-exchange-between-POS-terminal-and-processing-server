package com.example.pos.domain.repository.transaction

import com.example.pos.data.remote.DTO.TransactionResponse
import com.example.pos.domain.model.Transaction
import kotlinx.coroutines.flow.StateFlow

interface TransactionRepository {
    suspend fun sendTransaction(transaction: Transaction) : Result<Transaction>
    suspend fun getTransactions() : List<Transaction>
    suspend fun requestNewKey() : Result<Unit>
}