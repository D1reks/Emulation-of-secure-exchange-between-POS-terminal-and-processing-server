package com.example.pos.core.tlv

import com.example.pos.data.remote.DTO.TransactionRequest
import com.example.pos.data.remote.DTO.TransactionResponse
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class TlvEncoder {
    companion object {
        const val TAG_PAN: Byte = 0x10
        const val TAG_AMOUNT: Byte = 0x20
        const val TAG_TRANSACTION_ID: Byte = 0x30
        const val TAG_MERCHANT_ID: Byte = 0x40
    }

    fun encodeTransaction(transaction: TransactionRequest) : ByteArray {

        val tlvData = ByteArrayOutputStream()

        encodeTlvField(tlvData, TAG_PAN, transaction.cardPan.toByteArray(Charsets.UTF_8))

        val amountBytes = convertToMiddleEndian(transaction.amount)
        encodeTlvField(tlvData, TAG_AMOUNT, amountBytes)

        encodeTlvField(tlvData, TAG_TRANSACTION_ID, transaction.transactionId.toByteArray(Charsets.UTF_8))

        encodeTlvField(tlvData, TAG_MERCHANT_ID, transaction.merchantId.toByteArray(Charsets.UTF_8))

        return tlvData.toByteArray()
    }

    fun decodeTransaction(data: ByteArray) : TransactionResponse {

        val input = ByteArrayInputStream(data)
        var status = ""
        var authCode: String? = null
        var timestamp = 0L

        while (input.available() > 0) {
            val tag = input.read().toByte()
            val length = (input.read() shl 8) or input.read()
            val value = ByteArray(length).apply { input.read(this) }

            when (tag) {
                0x01.toByte() -> status = String(value, Charsets.UTF_8)
                0x02.toByte() -> authCode = String(value, Charsets.UTF_8)
                0x03.toByte() -> timestamp = convertFromMiddleEndian(value)
            }
        }

        return TransactionResponse(status, authCode, timestamp)
    }

    private fun encodeTlvField(output: ByteArrayOutputStream, tag: Byte, value: ByteArray) {

        output.write(tag.toInt())
        output.write((value.size shr 8) and 0xFF)
        output.write(value.size and 0xFF)
        output.write(value)

    }

    private fun convertToMiddleEndian(value: Long) : ByteArray {

        val bytes = ByteBuffer.allocate(8).putLong(value).array()
        return bytes.reversedArray()

    }

    private fun convertFromMiddleEndian(bytes: ByteArray) : Long {

        val reversed = bytes.reversedArray()
        return ByteBuffer.wrap(reversed).long

    }
}