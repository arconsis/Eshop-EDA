package com.arconsis.domain.events

import com.arconsis.common.Topics
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import com.arconsis.domain.ordersValidations.OrderValidation
import com.arconsis.domain.ordersValidations.isValid
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.isSuccess
import com.arconsis.domain.shipments.Shipment
import com.arconsis.domain.shipments.isOutForShipment
import io.quarkus.kafka.client.serialization.ObjectMapperSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Produced
import javax.enterprise.inject.Produces

class StreamsService {

    @Produces
    fun createTopology(): Topology {
        val builder = StreamsBuilder()
        val orderSerde = ObjectMapperSerde(Order::class.java)
        val ordersTable = builder.table(Topics.ORDERS.topicName, Consumed.with(Serdes.String(), orderSerde))

        createOrdersValidationStream(builder, ordersTable, orderSerde)
        createPaymentsStream(builder, ordersTable, orderSerde)
        createShipmentsStream(builder, ordersTable, orderSerde)
        return builder.build()
    }

    private fun createOrdersValidationStream(
        builder: StreamsBuilder,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>,
    ) {
        val orderValidationSerde = ObjectMapperSerde(OrderValidation::class.java)

        builder
            .stream(
                Topics.ORDERS_VALIDATIONS.topicName,
                Consumed.with(Serdes.String(), orderValidationSerde)
            )
            .filter { _, orderValidation ->
                orderValidation.isValid
            }
            .join(ordersTable) { _, order ->
                order
            }
            .mapValues { orderValidation ->
                val validOrder = orderValidation.copy(status = OrderStatus.VALID)
                validOrder
            }
            .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
    }

    private fun createPaymentsStream(
        builder: StreamsBuilder,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>,
    ) {
        val paymentTopicSerde = ObjectMapperSerde(Payment::class.java)

        builder
            .stream(
                Topics.PAYMENTS.topicName,
                Consumed.with(Serdes.String(), paymentTopicSerde)
            )
            .filter { _, payment -> payment.isSuccess }
            .join(ordersTable) { _, order ->
                order
            }
            .map { _, orderValidation ->
                val paidOrder = orderValidation.copy(status = OrderStatus.PAID)
                KeyValue.pair(paidOrder.userId.toString(), paidOrder)
            }
            .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
    }

    private fun createShipmentsStream(
        builder: StreamsBuilder,
        ordersTable: KTable<String, Order>,
        orderSerde: ObjectMapperSerde<Order>,
    ) {
        val shipmentTopicSerde = ObjectMapperSerde(Shipment::class.java)

        builder
            .stream(
                Topics.SHIPMENTS.topicName,
                Consumed.with(Serdes.String(), shipmentTopicSerde)
            )
            .filter { _, shipment -> shipment.isOutForShipment }
            .join(ordersTable) { _, order ->
                order
            }
            .map { _, orderValidation ->
                val outForShipmentOrder = orderValidation.copy(status = OrderStatus.OUT_FOR_SHIPMENT)
                KeyValue.pair(outForShipmentOrder.userId.toString(), outForShipmentOrder)
            }
            .to(Topics.ORDERS.topicName, Produced.with(Serdes.String(), orderSerde))
    }
}