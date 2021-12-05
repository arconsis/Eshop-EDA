package com.arconsis.domain.orders

import com.arconsis.data.email.EmailDto
import com.arconsis.data.email.EmailRepository
import com.arconsis.data.users.UsersRepository
import com.arconsis.domain.users.User
import io.smallrye.mutiny.Uni
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class OrdersService(
    val emailRepository: EmailRepository,
    val usersRepository: UsersRepository,
    @ConfigProperty(name = "email.sender") private val sender: String,
) {
    fun handleOrderEvents(order: Order): Uni<Void> {
        return when (order.status) {
            OrderStatus.PAID -> handlePaidOrders(order)
            OrderStatus.SHIPPED -> handleOutForShipmentOrders(order)
            else -> return Uni.createFrom().voidItem()
        }
    }

    private fun handlePaidOrders(order: Order): Uni<Void> {
        return usersRepository.getUser(order.userId)
            .sendEmail(order)
            .map {
                null
            }
    }

    private fun handleOutForShipmentOrders(order: Order): Uni<Void> {
        return usersRepository.getUser(order.userId)
            .sendEmail(order)
            .map {
                null
            }
    }

    private fun Uni<User?>.sendEmail(order: Order) = flatMap { user ->
        if (user != null) {
            val emailDto = EmailDto(
                senderEmail = sender,
                receiverEmail = user.email,
                subject = getEmailSubject(order.status),
                text = getEmailText(order.id, order.status)
            )
            emailRepository.sendEmail(emailDto)
        } else {
            Uni.createFrom().voidItem()
        }
    }

    private fun getEmailSubject(orderStatus: OrderStatus): String = when (orderStatus) {
        OrderStatus.SHIPPED -> EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT
        else -> EMAIL_SUBJECT_ORDER_PAID
    }

    private fun getEmailText(orderId: UUID, orderStatus: OrderStatus): String = when (orderStatus) {
        OrderStatus.SHIPPED -> "New order with number: $orderId is out for shipment and will be delivered in few days!"
        else -> "New order with number: $orderId just placed! We will inform you with another email about shipment progress."
    }

    companion object {
        private const val EMAIL_SUBJECT_ORDER_OUT_FOR_SHIPMENT = "New order was placed!"
        private const val EMAIL_SUBJECT_ORDER_PAID = "New order confirmed!"
    }
}