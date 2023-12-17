package com.ghadeer.posassignment.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ghadeer.posassignment.data.enums.OrderStatus
import com.ghadeer.posassignment.data.enums.PaymentStatus
import com.ghadeer.posassignment.util.getDate

@Entity
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: String = "",
    val orderStatus: Int = OrderStatus.PENDING,
    val paymentStatus: Int = PaymentStatus.UNPAID,
    val customerId: Int? = null,
    val createdAt: String = getDate(),
)