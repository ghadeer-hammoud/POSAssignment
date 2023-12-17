package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import com.ghadeer.posassignment.data.enums.OrderStatus
import com.ghadeer.posassignment.data.enums.PaymentStatus
import com.ghadeer.posassignment.util.getDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: Int = 0,
    val orderId: String = "",
    val orderStatus: Int = OrderStatus.PENDING,
    val paymentStatus: Int = PaymentStatus.UNPAID,
    val customer: Customer? = null,
    val createdAt: String = getDate(),
    val items: List<OrderItem> = mutableListOf(),
): Parcelable
