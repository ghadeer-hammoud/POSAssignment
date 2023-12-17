package com.ghadeer.posassignment.data.enums

import com.ghadeer.posassignment.R

object OrderStatus {
    const val PENDING = 1
    const val COMPLETED = 2

    fun values() = listOf(PENDING, COMPLETED)
    fun labels() = listOf("Pending", "Completed")
    fun indicatorColors() = listOf(R.color.red_900, R.color.green_800)
}