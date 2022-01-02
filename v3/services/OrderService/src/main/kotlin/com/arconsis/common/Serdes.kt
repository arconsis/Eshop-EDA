package com.arconsis.common

import com.arconsis.domain.orders.Order
import com.arconsis.domain.ordersValidations.OrderValidation
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.shipments.Shipment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde

val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
val orderSerde = ObjectMapperSerde(Order::class.java)
val paymentTopicSerde = ObjectMapperSerde(Payment::class.java)
val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)