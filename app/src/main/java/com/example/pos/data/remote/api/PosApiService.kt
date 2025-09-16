package com.example.pos.data.remote.api

import com.example.pos.data.remote.DTO.EncryptedTransactionRequest
import com.example.pos.data.remote.DTO.KeyRotationResponse
import com.example.pos.data.remote.DTO.TransactionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PosApiService {
    @POST("transactions")
    suspend fun sendTransaction(@Body request: EncryptedTransactionRequest): TransactionResponse

    @POST("keys/rotate")
    suspend fun rotateKey(): KeyRotationResponse
}