package com.ghadeer.posassignment.data.model

import com.ghadeer.posassignment.data.enums.CartActionType

data class CartAction(
    val action: CartActionType = CartActionType.ACTION_NOTHING,
    val productId: Int = 0, // affected product
)
