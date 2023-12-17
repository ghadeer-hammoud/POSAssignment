package com.ghadeer.posassignment.ui.states

import com.ghadeer.posassignment.data.model.Category

data class AddProductState(

    var id: Int = 0,
    var barcode: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var category: Category = Category(),
)
