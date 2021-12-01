package com.arconsis.domain.orders

import com.arconsis.data.email.EmailDto
import com.arconsis.data.email.EmailRepository
import com.arconsis.data.users.UsersRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    val emailRepository: EmailRepository,
    val usersRepository: UsersRepository,
    @ConfigProperty(name = "email.sender") private val sender: String,
) {
    fun handleOrderEvents(order: Order) {
        when (order.status) {
            OrderStatus.PAID -> handlePaidOrders(order)
            OrderStatus.SHIPPED -> handleOutForShipmentOrders(order)
            else -> return
        }
    }

    private fun handlePaidOrders(order: Order) {
        val user = usersRepository.getUser(order.userId)
        if (user != null) {
            emailRepository.sendEmail(
                EmailDto(
                    senderEmail = sender,
                    receiverEmail = user.email,
                    subject = EMAIL_SUBJECT_ORDER_PAID,
                    text = "New order with number: ${order.id} just placed! We will inform you with another email about shipment progress."
                ),
            )
        }
    }

    private fun handleOutForShipmentOrders(order: Order) {
        val user = usersRepository.getUser(order.userId)
        if (user != null) {
            emailRepository.sendEmail(
                EmailDto(
                    senderEmail = sender,
                    receiverEmail = user.email,
                    subject = EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT,
                    text = "New order with number: ${order.id} is out for shipment and will be delivered in few days!"
                ),
            )
        }
    }

    companion object {
        private const val EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT = "New order was placed!"
        private const val EMAIL_SUBJECT_ORDER_PAID = "New order confirmed!"
    }
}