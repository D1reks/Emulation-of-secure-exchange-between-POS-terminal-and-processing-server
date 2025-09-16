package com.example.pos.core.utils

object HexDump {

    fun generateHexDump(data: ByteArray) : String {
        val result = StringBuilder()
        val hex = StringBuilder()
        val ascii = StringBuilder()

        for ((index, byte) in data.withIndex()) {

            hex.append("%02X ".format(byte))

            ascii.append(if (byte in 32..126) byte.toChar() else ".")

            if ((index + 1) % 16 == 0 || index == data.size - 1) {
                result.append("%-48s | %-16s\n".format(hex.toString(), ascii.toString()))
                hex.clear()
                ascii.clear()
            }
        }

        return result.toString()
    }

    fun buildPacket(
        encryptedSessionKey: ByteArray,
        iv: ByteArray,
        hmac: ByteArray,
        encryptedData: ByteArray
    ) : ByteArray {
        val packet = mutableListOf<Byte>()

        packet.addAll(encryptedSessionKey.toList())

        packet.addAll(iv.toList())

        packet.addAll(hmac.toList())

        packet.addAll(encryptedData.toList())

        return packet.toByteArray()
    }

}