package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SalesProduct(
    val id: Int = 0,
    val barcode: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val qtyInCart: Double = 0.0,
    val qtyInStock: Double = 0.0,
): Parcelable
