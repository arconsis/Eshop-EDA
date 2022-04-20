package com.arconsis.common

import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.groups.UniAndGroup2
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

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)

fun <T, R> UniAndGroup2<T, R>.asPair(): Uni<Pair<T, R>> = combinedWith { first, second -> Pair(first, second) }