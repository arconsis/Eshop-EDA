package com.arconsis.domain.orders

import io.quarkus.kafka.client.serialization.ObjectMapperSerializer

class OrderDtoSerializer : ObjectMapperSerializer<Order>()