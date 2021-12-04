package com.arconsis.data.payments

import com.arconsis.data.PostgreSQLEnumType
import com.arconsis.domain.orders.Order
import com.arconsis.domain.payments.CreatePayment
import com.arconsis.domain.payments.Payment
import com.arconsis.domain.payments.PaymentStatus
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity(name = "payments")
@TypeDef(
    name = "pgsql_enum",
    typeClass = PostgreSQLEnumType::class
)
class PaymentEntity(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(name = "transaction_id", nullable = true)
    var transactionId: UUID?,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "order_id", nullable = false)
    var orderId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    @Type(type = "pgsql_enum")
    var status: PaymentStatus,

    @Column(nullable = false)
    var amount: Double,

    @Column(nullable = false)
    var currency: String,

    @CreationTimestamp
    @Column(name = "created_at")
    var createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null,
)

fun PaymentEntity.toPayment() = Payment(
    id = id!!,
    transactionId = transactionId,
    orderId = orderId,
    userId = userId,
    amount = amount,
    currency = currency,
    status = status,
)

fun Payment.toPaymentEntity() = PaymentEntity(
    transactionId = transactionId,
    userId = userId,
    orderId = orderId,
    amount = amount,
    currency = currency,
    status = status,
)

fun Order.toCreatePayment() = CreatePayment(
    userId = userId,
    orderId = id,
    amount = amount,
    currency = currency,
)