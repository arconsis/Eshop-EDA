package com.arconsis.data

import com.arconsis.domain.orders.CreateOrder
import com.arconsis.domain.orders.Order
import com.arconsis.domain.orders.OrderStatus
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "orders")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
class OrderEntity(
	@Id
    @GeneratedValue
    var id: UUID? = null,

	@Column(name = "user_id", nullable = false)
    var userId: UUID,

	@Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    @Type( type = "pgsql_enum" )
    var status: OrderStatus,

	@Column(nullable = false)
    var amount: Double,

	@Column(nullable = false)
    var currency: String,

	@Column(name = "product_id", nullable = false)
    var productId: String,

	@Column(nullable = false)
    var quantity: Int,

	@Column(name = "created_at")
    var createdAt: Instant? = null,

	@Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun OrderEntity.toOrder() = Order(
    userId = userId,
    orderId = id!!,
    amount = amount,
    currency = currency,
    productId = productId,
    quantity = quantity,
    status = status,
)

fun CreateOrder.toOrderEntity(status: OrderStatus) = OrderEntity(
    userId = userId,
    amount = amount,
    currency = currency,
    productId = productId,
    quantity = quantity,
    status = status,
)