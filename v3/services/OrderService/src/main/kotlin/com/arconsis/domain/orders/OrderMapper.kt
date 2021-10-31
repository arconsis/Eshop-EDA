package com.arconsis.domain.orders

import com.arconsis.presentation.orders.dto.OrderCreateDto
import java.util.*

fun OrderCreateDto.toPendingOrder(orderNo: UUID) = Order(
    userId = userId,
    orderNo = orderNo,
    amount = amount,
    currency = currency,
    productId = productId,
    quantity = quantity,
    status = OrderStatus.PENDING
)