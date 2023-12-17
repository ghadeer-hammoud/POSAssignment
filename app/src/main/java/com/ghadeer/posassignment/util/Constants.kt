package com.ghadeer.posassignment.util

import androidx.navigation.NavOptions
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.model.CartItem

object Constants {

    val NAV_OPTIONS = NavOptions.Builder()
        .setLaunchSingleTop(true)  // Used to prevent multiple copies of the same destination
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()




    val defaultCartItem = CartItem(
        productId = 0,
        productName = "",
        barcode = "",
        quantity = 0.0,
        price = 0.0,
        taxType = 0,
        tax = 0.0,
        discount = 0.0,
        priceExcTax = 0.0,
        discountExcTax = 0.0,
        lineTotal = 0.0,
        lineTotalExcTax = 0.0,
        netAmount = 0.0,
        netAmountExcTax = 0.0,
        taxAmount = 0.0,
        total = 0.0
    )
}