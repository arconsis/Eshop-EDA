package com.arconsis.common

interface Message<T, P> {
    val type: T
    val payload: P
}