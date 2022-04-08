package com.arconsis.common

import kotlinx.coroutines.delay

suspend inline fun <T> retryWithBackoff(atMost: Int = 3, initialDelayMillis: Long = 300, block: () -> T): T {
    var currentDelayMillis = initialDelayMillis
    repeat(atMost - 1) {
        try {
            return block()
        } catch (e: Throwable) {
            // TODO: Add logging or finer grained control if we should continue to retry
            delay(currentDelayMillis)
            currentDelayMillis *= 2
        }
    }
    return block()
}