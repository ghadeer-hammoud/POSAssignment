package com.ghadeer.posassignment.data.enums

object TaxType {
    const val TAX_INCLUDED = 1
    const val TAX_EXCLUDED = 2

    fun values() = listOf(TAX_INCLUDED, TAX_EXCLUDED)

    fun labels() = listOf("Tax Included", "Tax Excluded")
}