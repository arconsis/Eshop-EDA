package com.arconsis.common

enum class Topics(val topicName: String) {
    USERS("Users"),
    USERS_RAW("postgres.public.users_outbox_events"),
}