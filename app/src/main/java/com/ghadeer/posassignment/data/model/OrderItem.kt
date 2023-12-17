package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import com.ghadeer.posassignment.data.enums.TaxType
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val id: Int = 0,
    val orderId: String = "",
    var productId: Int = 0,
    var productName: String = "",
    var productBarcode: String = "",
    var quantity: Double = 0.0,
    var price: Double,
    var taxType: Int = TaxType.TAX_INCLUDED,
    var tax: Double = 0.0,

    var lineTotal: Double = 0.0,
    var discount: Double = 0.0,
    var netAmount: Double = 0.0,
    var taxAmount: Double = 0.0,
    var total: Double = 0.0,
): Parcelable
