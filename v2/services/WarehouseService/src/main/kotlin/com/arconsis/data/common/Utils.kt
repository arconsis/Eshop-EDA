package com.arconsis.data.common

import io.smallrye.mutiny.Uni

fun <T> T.toUni(): Uni<T> = Uni.createFrom().item(this)
