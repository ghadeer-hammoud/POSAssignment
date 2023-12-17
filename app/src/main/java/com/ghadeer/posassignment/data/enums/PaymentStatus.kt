package com.ghadeer.posassignment.data.enums

import com.ghadeer.posassignment.R

object PaymentStatus {
    const val UNPAID = 1
    const val PAID = 2

    fun values() = listOf(UNPAID, PAID)
    fun labels() = listOf("Unpaid", "Paid")
    fun indicatorColors() = listOf(R.color.red_900, R.color.green_800)
}