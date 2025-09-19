package com.example.pos.data.remote

import com.example.pos.core.crypto.CryptoManager
import com.example.pos.core.network.PosClient
import com.example.pos.core.tlv.TlvEncoder
import com.example.pos.data.remote.DTO.TransactionRequest
import com.example.pos.data.remote.DTO.TransactionResponse
import com.example.pos.data.remote.api.PosApiService
import javax.inject.Inject
import android.util.Base64
import com.example.pos.core.utils.HexDump
import com.example.pos.core.utils.HexDump.buildPacket
import com.example.pos.data.remote.DTO.EncryptedTransactionRequest
import com.example.pos.domain.repository.key.KeyRepository
import com.example.pos.domain.repository.key.KeyRepositoryImpl

class RetrofitPosClient @Inject constructor(
    private val apiService: PosApiService,
    private val cryptoManager: CryptoManager,
    private val keyRepository: KeyRepository,
    private val tlvEncoder: TlvEncoder
) : PosClient {

    override suspend fun sendTransaction(request: TransactionRequest): TransactionResponse {

        val tlvData = tlvEncoder.encodeTransaction(request)
        println("Raw tlv data")
        println(HexDump.generateHexDump(tlvData))

        val hmacKey = keyRepository.getCurrentHmacKey()
        val hmac = cryptoManager.generateHmac(tlvData, hmacKey)

        val sessionKey = keyRepository.generateSessionKey()
        val publicKey = keyRepository.getServerPublicKey()
        val encryptedSessionKey = cryptoManager.encryptRsa(sessionKey, publicKey)

        val (encryptedData, iv) = cryptoManager.encryptAesGcm(tlvData, sessionKey)

        val packet = buildPacket(encryptedSessionKey, iv, hmac, encryptedData)

        println("Encrypted Packet")
        println(HexDump.generateHexDump(packet))

        val encryptedRequest = EncryptedTransactionRequest(
            encryptedSessionKey = Base64.encodeToString(encryptedSessionKey, Base64.NO_WRAP),
            iv = Base64.encodeToString(iv, Base64.NO_WRAP),
            hmac = Base64.encodeToString(hmac, Base64.NO_WRAP),
            encryptedData = Base64.encodeToString(encryptedData, Base64.NO_WRAP)
        )

        return apiService.sendTransaction(encryptedRequest)
    }

    override suspend fun requestKeyRotation(): Boolean {
        val response = apiService.rotateKey()
        return response.success
    }
}