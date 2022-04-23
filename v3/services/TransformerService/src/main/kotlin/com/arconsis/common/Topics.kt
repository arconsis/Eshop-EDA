package com.arconsis.common

enum class Topics(val topicName: String) {
    USERS("Users"),
    USERS_RAW("users-db.public.users_outbox_events"),
}