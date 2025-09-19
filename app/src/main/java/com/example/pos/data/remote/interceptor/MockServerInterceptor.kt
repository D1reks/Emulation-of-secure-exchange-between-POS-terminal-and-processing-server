package com.example.pos.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.SocketTimeoutException
import java.util.Random

class MockServerInterceptor : Interceptor {

    private val random = Random()
    private var transactionCount = 0

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        Thread.sleep(random.nextInt(500).toLong())

        // тут эмуляция потери 5% запросов
        if(random.nextDouble()<=0.05) {

            throw SocketTimeoutException("Mock timeout")

        }

        return when {

            request.url.pathSegments.contains("transactions")->
                handleTransactionRequest(request)

            request.url.pathSegments.contains("keys")->
                handleKeysRotationRequest(request)

            else -> chain.proceed(request)
        }

    }

    private fun handleTransactionRequest(request: Request): Response {
        transactionCount++

        val isApproved = random.nextDouble() >= 0.03

        val responseBody = """
            {
             "status": "${if (isApproved) "APPROVED" else "DECLINED"}",
            "authCode": "${if (isApproved) generateAuthCode() else null}",
            "timestamp": ${System.currentTimeMillis()}
            }
            """.trimIndent()

        return buildMockResponse(request, responseBody)
    }

    private fun handleKeysRotationRequest(request: Request): Response {
        val responseBody = """{"success":true}"""
        return buildMockResponse(request,responseBody)
    }

    private fun buildMockResponse(request: Request, body: String): Response {

        return Response.Builder()
            .code(200)
            .message("Ok")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private fun generateAuthCode() : String = (10000..99999).random().toString()

}