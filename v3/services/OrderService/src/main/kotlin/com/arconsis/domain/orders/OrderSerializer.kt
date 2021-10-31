package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer

class OrderSerializer : ObjectMapperSerializer<Order>()