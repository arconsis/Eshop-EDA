package com.arconsis.common

enum class Topics(val topicName: String) {
    ORDERS("Orders"),
    USERS("Users"),
    USERS_RAW("postgres.public.users_outbox_events"),
}