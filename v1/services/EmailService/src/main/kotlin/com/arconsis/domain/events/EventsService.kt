package com.arconsis.domain.events

import com.arconsis.data.email.EmailDto
import com.arconsis.data.email.EmailRepository
import com.arconsis.domain.shipments.ShipmentEvent
import io.smallrye.reactive.messaging.annotations.Blocking
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsService(
    val emailRepository: EmailRepository,
    @ConfigProperty(name = "email.sender") private val sender: String,
) {

//    @Blocking
//    @Incoming("orders-in")
//    fun consumeOrders(order: Order) {
//        emailRepository.sendEmail(
//            EmailDto(
//                senderEmail = sender,
//                receiverEmail = order.userEmail,
//                subject = EMAIL_SUBJECT_ORDER_PAID,
//                text = "New order with number: ${order.id} just placed! We will inform you with another email about shipment progress."
//            ),
//        )
//    }

//    @Blocking
//    @Incoming("shipments-in")
//    fun consumeShipments(shipmentEvent: ShipmentEvent) {
//        val (_, shipment) = shipmentEvent
//        emailRepository.sendEmail(
//            EmailDto(
//                senderEmail = sender,
//                receiverEmail = shipment.userEmail,
//                subject = EMAIL_SUBJECT_ORDER_PAID,
//                text = "New order with number: ${shipment.orderId} just placed! We will inform you with another email about shipment progress."
//            ),
//        )
//    }

    companion object {
        private const val EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT = "New order was placed!"
        private const val EMAIL_SUBJECT_ORDER_PAID = "New order confirmed!"
    }
}
