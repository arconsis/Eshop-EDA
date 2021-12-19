package com.arconsis.common

import com.arconsis.domain.inventory.Inventory
import com.arconsis.domain.orders.Order
import com.arconsis.domain.ordervalidations.OrderValidation
import com.arconsis.domain.shipments.Shipment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde


val inventoryTopicSerde = ObjectMapperSerde(Inventory::class.java)
val orderTopicSerde = ObjectMapperSerde(Order::class.java)
val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)
val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)