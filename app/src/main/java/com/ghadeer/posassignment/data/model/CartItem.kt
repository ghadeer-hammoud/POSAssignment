package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import com.ghadeer.posassignment.data.enums.TaxType
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    var productId: Int,
    var productName: String,
    var barcode: String = "",
    var quantity: Double,
    var price: Double,
    var taxType: Int = TaxType.TAX_INCLUDED,
    var tax: Double = 0.0,
    var discount: Double = 0.0,

    var priceExcTax: Double = 0.0,
    var discountExcTax: Double = 0.0,

    var lineTotal: Double = 0.0,
    var lineTotalExcTax: Double = 0.0,
    var netAmount: Double = 0.0,
    var netAmountExcTax: Double = 0.0,
    var taxAmount: Double = 0.0,
    var total: Double = 0.0,
): Parcelable{

    init {
        calculateValues()
    }

    fun calculateValues(){

        when(taxType){
            TaxType.TAX_INCLUDED -> {

                // TAX Included

                priceExcTax = price / (1 + (tax / 100))
                discountExcTax = discount / (1 + (tax / 100))
                lineTotal = price * quantity
                lineTotalExcTax = priceExcTax * quantity

                netAmount = lineTotal - discount
                netAmountExcTax = netAmount / (1 + (tax / 100))
                taxAmount = netAmountExcTax * tax / 100
                total = netAmount

            }
            else -> {

                // TAX Excluded

                priceExcTax = price
                discountExcTax = discount
                lineTotal = price * quantity
                lineTotalExcTax = priceExcTax * quantity
                netAmount = lineTotal - discount
                netAmountExcTax = netAmount
                taxAmount = netAmountExcTax * tax / 100
                total = netAmount.plus(taxAmount)

            }
        }

    }

    fun hasDiscount() = discount > 0.0
}