package com.arconsis.common

import io.smallrye.mutiny.Uni
import java.time.Duration

fun <T> Uni<T>.retryWithBackoff(duration: Duration = Duration.ofSeconds(3), atMost: Long = 3): Uni<T> = onFailure()
    .retry()
    .withBackOff(duration)
    .atMost(atMost)