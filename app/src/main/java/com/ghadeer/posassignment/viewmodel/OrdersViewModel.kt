package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghadeer.posassignment.data.model.Order
import com.ghadeer.posassignment.repository.imp.CustomerRepositoryImp
import com.ghadeer.posassignment.repository.imp.OrderRepositoryImp
import com.ghadeer.posassignment.ui.pagination.OrdersPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepositoryImp,
    private val customerRepository: CustomerRepositoryImp,
)
: ViewModel() {

    companion object {
        private const val TAG = "OrdersViewModel"
    }

    init {
        observeOrders()
    }

    private var searchTerm: String = ""
    private var orderStatusFilterList: MutableList<Int> = mutableListOf()
    private var paymentStatusFilterList: MutableList<Int> = mutableListOf()
    private var startDateFilter: String? = null
    private var endDateFilter: String? = null

    private val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            initialLoadSize = 60
        ),
        pagingSourceFactory = {
            currentPagingSource = OrdersPagingSource(
                orderRepository,
                customerRepository,
                mapOf(
                    "searchTerm" to searchTerm,
                    "orderStatus" to orderStatusFilterList.joinToString(","),
                    "paymentStatus" to paymentStatusFilterList.joinToString(","),
                    "startDate" to (startDateFilter ?: ""),
                    "endDate" to (endDateFilter ?: ""),
                ),
            )

            currentPagingSource!!
        }
    )

    private var currentPagingSource: OrdersPagingSource? = null

    val ordersFlow: Flow<PagingData<Order>> = pager.flow.cachedIn(viewModelScope)

    val ordersTotalCount = MutableLiveData(0)


    private fun observeOrders() = viewModelScope.launch {
        orderRepository.getOrdersFlow().collectLatest {
            ordersTotalCount.value = it.size
            refreshData()
        }
    }

    private fun refreshData() {
        currentPagingSource?.invalidate()
    }

    fun setSearchTerm(searchTerm: String){
        this.searchTerm = searchTerm
        refreshData()
    }

    fun setOrderStatusFilter(orderStatus: Int?){
        this.orderStatusFilterList.clear()
        orderStatus?.let { this.orderStatusFilterList.add(it) }
        refreshData()
    }

    fun setPaymentStatusFilter(paymentStatus: Int?){
        this.paymentStatusFilterList.clear()
        paymentStatus?.let { this.paymentStatusFilterList.add(it) }
        refreshData()
    }

    fun setStartDate(startDate: String?){
        this.startDateFilter = startDate
        refreshData()
    }

    fun setEndDate(endDate: String?){
        this.endDateFilter = endDate
        refreshData()
    }

    fun isUserSearching() =
        searchTerm.isNotEmpty()
                || orderStatusFilterList.isNotEmpty()
                || paymentStatusFilterList.isNotEmpty()
                || startDateFilter != null
                || endDateFilter != null
}