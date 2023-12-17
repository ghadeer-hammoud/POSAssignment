package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val barcode: String = "",
    val category: Category = Category(),
): Parcelable
