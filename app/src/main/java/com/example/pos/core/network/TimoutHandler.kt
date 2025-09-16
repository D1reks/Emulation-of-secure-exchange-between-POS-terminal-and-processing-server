package com.example.pos.core.network

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.io.IOException
import javax.inject.Inject

class TimeoutHandler @Inject constructor(
){
    suspend fun <T> withRetry(
        maxRetries: Int,
        timeoutMs: Long,
        block: suspend () -> T
    ): T {
        var lastException: Exception? = null

        for (attempt in 1..maxRetries) {
            try {
                return withTimeout(timeoutMs) {
                    block()
                }
            } catch (e: TimeoutCancellationException) {

                lastException = e
                delay(1000L)

            } catch (e: Exception) {

                lastException = e
                break

            }
        }

        throw lastException ?: IOException("All retry attempts failed")
    }
}