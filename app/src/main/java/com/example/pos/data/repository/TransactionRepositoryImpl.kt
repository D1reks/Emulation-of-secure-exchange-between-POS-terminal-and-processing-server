package com.example.pos.data.repository

import com.example.pos.core.network.PosClient
import com.example.pos.data.mapper.TransactionMapper
import com.example.pos.data.remote.RetrofitPosClient
import com.example.pos.domain.model.Transaction
import com.example.pos.domain.model.TransactionStatus
import com.example.pos.domain.repository.transaction.TransactionRepository
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val posClient: PosClient,
    private val mapper: TransactionMapper
) : TransactionRepository {

    private val transactions = mutableListOf<Transaction>()

    override suspend fun sendTransaction(transaction: Transaction): Result<Transaction> {
        return try {

            val request = mapper.mapToRequest(transaction)

            val response = posClient.sendTransaction(request)

            val updatedTransaction = transaction.copy(
                status = if (response.status == "APPROVED") TransactionStatus.APPROVED
                else TransactionStatus.DECLINED,
                authCode = response.authCode
            )

            transactions.add(updatedTransaction)
            Result.success(updatedTransaction)

        } catch (e: Exception) {

            val failedTransaction = transaction.copy(status = TransactionStatus.TIMEOUT)
            transactions.add(failedTransaction)
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(): List<Transaction> {
        return transactions.toList()
    }

    override suspend fun requestNewKey(): Result<Unit> {
        return try {
            val success = posClient.requestKeyRotation()
            if (success) Result.success(Unit)
            else Result.failure(Exception("Rotation failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}