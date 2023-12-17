package com.ghadeer.posassignment.data.model

data class Cart(
    val id: String,
    val items: MutableList<CartItem> = mutableListOf(),
    var customer: Customer? = null,
    var discountAmount: Double = 0.0,
    var discountPercentage: Double = 0.0,
) {

    fun addItem(cartItem: CartItem) {

        when(val item = items.find { it.productId == cartItem.productId }){
            null -> items.add(cartItem)
            else -> {
                //Update product
                updateItemQty(cartItem.productId, item.quantity.plus(cartItem.quantity))
            }
        }
    }

    fun updateItemQty(productId: Int, qty: Double) {
        items.find { productId == it.productId }?.apply {
            this.quantity = qty
            this.calculateValues()
        }
    }


    fun decreaseItemQty(id: Int, qty: Double) {
        items.find { id == it.productId  }?.let {
            it.quantity = it.quantity - qty
            if (it.quantity < 0) it.quantity = 0.0
            it.calculateValues()
        }
    }


    fun updateItemPrice(productId: Int, newPrice: Double) {
        items.find { it.productId == productId }?.apply {
            this.price = newPrice
            this.calculateValues()
        }
    }

    fun removeItem(id: Int) {
        val p = items.find { id == it.productId }
        items.remove(p)
    }

    fun updateDiscountAmount(amount: Double) {
        this.discountAmount = amount
        splitDiscountOnItems(this.discountAmount)
    }

    fun updateDiscountPercentage(percentage: Double) {
        this.discountPercentage = percentage
    }

    private fun splitDiscountOnItems(discountAmount: Double) {
        items.forEach {
            it.discount = it.lineTotal / getLineTotal() * discountAmount
            it.calculateValues()
        }
    }

    fun clear(){
        items.clear()
        customer = null
        discountAmount = 0.0
        discountPercentage = 0.0
    }
    fun isEmpty():Boolean = items.isEmpty()

    fun hasDiscount() = this.discountAmount > 0.0

     fun assignCustomer(customer: Customer?){
        this.customer = customer
    }

    fun getLineTotal(): Double = items.sumOf { it.lineTotal }

    fun getItemsCount(): Int = items.size

    fun getItemsQuantity(): Double = items.sumOf { it.quantity }

    fun getTotalDiscount(): Double = items.sumOf { it.discount }

    fun getNetTotal(): Double = items.sumOf { it.netAmount }

    fun getTotalTax(): Double = items.sumOf { it.taxAmount }

    fun getGrandTotal(): Double = items.sumOf { it.total }

}



