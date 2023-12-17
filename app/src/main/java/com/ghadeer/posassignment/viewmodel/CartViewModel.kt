package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ghadeer.posassignment.data.model.Cart
import com.ghadeer.posassignment.data.model.CartAction
import com.ghadeer.posassignment.data.enums.CartActionType
import com.ghadeer.posassignment.data.model.CartItem
import com.ghadeer.posassignment.data.model.Customer
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    
) : ViewModel() {

    var cart: Cart? = Cart(StringUtils.getRandomString())
    val cartNotifier: MutableLiveData<CartAction> by lazy {
        MutableLiveData(CartAction())
    }


    fun assignCustomer(customer: Customer?) {
        cart?.assignCustomer(customer)
        cartNotifier.value = CartAction(action = CartActionType.ACTION_UPDATE_CUSTOMER)
    }

    fun addCartItem(cartItem: CartItem) {

        if(cart == null){
            cart = Cart(id = StringUtils.getRandomString())
        }
        cart?.addItem(cartItem)
        cartNotifier.value = CartAction(
            action = CartActionType.ACTION_ADD,
            productId = cartItem.productId,
        )
    }

    fun removeCartItem(productId: Int) {
        cart?.removeItem(productId)
        if (cart?.getItemsCount() == 0)
            clearCurrentCart()
        cartNotifier.value = CartAction(action = CartActionType.ACTION_REMOVE, productId = productId)
    }

    fun updateDiscountAmount(discountAmount: Double) {
        cart?.updateDiscountAmount(discountAmount)
        cartNotifier.value = CartAction(action = CartActionType.ACTION_UPDATE_DISCOUNT)
    }

    fun updateDiscountPercentage(discountPercentage: Double) {
        cart?.updateDiscountPercentage(discountPercentage)
        cartNotifier.value = CartAction(action = CartActionType.ACTION_UPDATE_DISCOUNT)
    }

    fun hasTotalDiscount() = cart?.hasDiscount() ?: true

    fun clearDiscount() {
        updateDiscountAmount(0.0)
        updateDiscountPercentage(0.0)
    }
    

    fun notifyUpdateItem(productId: Int) {
        cartNotifier.value = CartAction(
            action = CartActionType.ACTION_UPDATE_ITEM,
            productId = productId,
        )
    }

    fun clearCurrentCart() {
        cart?.clear()
        cartNotifier.value = CartAction(action = CartActionType.ACTION_CLEAR)
    }
}