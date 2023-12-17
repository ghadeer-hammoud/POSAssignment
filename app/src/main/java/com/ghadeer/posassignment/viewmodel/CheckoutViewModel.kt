package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghadeer.posassignment.data.enums.OrderStatus
import com.ghadeer.posassignment.data.enums.PaymentStatus
import com.ghadeer.posassignment.data.model.*
import com.ghadeer.posassignment.repository.imp.OrderRepositoryImp
import com.ghadeer.posassignment.ui.states.MainState
import com.ghadeer.posassignment.util.Mapper
import com.ghadeer.posassignment.util.StringUtils
import com.ghadeer.posassignment.util.getDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepositoryImp
): ViewModel() {

    private val _checkoutState = MutableStateFlow(MainState<String>())
    val checkoutState = _checkoutState.asStateFlow()

    fun doCheckout(cart: Cart) = viewModelScope.launch{

        _checkoutState.update { it.showProgress() }

        val orderId = StringUtils.getRandomString(length = 10)

        val order = Order(
            orderId = orderId,
            orderStatus = OrderStatus.COMPLETED,
            paymentStatus = PaymentStatus.PAID,
            customer = cart.customer,
            createdAt = getDate(),
            items = cart.items.map {
                OrderItem(
                    orderId = orderId,
                    productId = it.productId,
                    productName = it.productName,
                    productBarcode = it.barcode,
                    quantity = it.quantity,
                    price = it.price,
                    taxType = it.taxType,
                    tax = it.tax,
                    lineTotal = it.lineTotal,
                    discount = it.discount,
                    netAmount = it.netAmount,
                    taxAmount = it.taxAmount,
                    total = it.total
                )
            }
        )

        when(val res = orderRepository.addOrder(
            Mapper.orderModelToOrderEntity(order),
            order.items.map { Mapper.orderItemModelToOrderItemEntity(it) })
        ){
            is DataResult.Success -> {
                _checkoutState.update { it.successWith(message = "Order placed successfully.") }
            }
            is DataResult.Error -> {
                _checkoutState.update { it.failureWith(message = res.message) }
            }
        }
        _checkoutState.update { it.empty() }
    }
}