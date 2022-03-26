package com.arconsis.common

import io.smallrye.mutiny.Uni
import kotlinx.coroutines.delay
import java.time.Duration

fun <T> Uni<T>.retryWithBackoff(duration: Duration = Duration.ofSeconds(3), atMost: Long = 3): Uni<T> = onFailure()
    .retry()
    .withBackOff(duration)
    .atMost(atMost)


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