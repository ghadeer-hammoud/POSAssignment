package com.ghadeer.posassignment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghadeer.posassignment.data.model.Customer
import com.ghadeer.posassignment.repository.imp.CategoryRepositoryImp
import com.ghadeer.posassignment.repository.imp.CustomerRepositoryImp
import com.ghadeer.posassignment.ui.pagination.CustomersPagingSource
import com.ghadeer.posassignment.util.Mapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customerRepository: CustomerRepositoryImp,
)
: ViewModel() {

    companion object {
        private const val TAG = "CustomersViewModel"
    }

    init {
        observeCustomers()
    }

    private var searchTerm: String = ""

    private val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            initialLoadSize = 60
        ),
        pagingSourceFactory = {
            currentPagingSource = CustomersPagingSource(
                customerRepository,
                mapOf(
                    "searchTerm" to searchTerm,
                ),
            )

            currentPagingSource!!
        }
    )

    private var currentPagingSource: CustomersPagingSource? = null

    val customersFlow: Flow<PagingData<Customer>> = pager.flow.cachedIn(viewModelScope)

    val customersTotalCount = MutableLiveData(0)


    private fun observeCustomers() = viewModelScope.launch {
        customerRepository.getCustomersFlow().collectLatest {
            customersTotalCount.value = it.size
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

    fun isUserSearching() = searchTerm.isNotEmpty()


    fun addCustomer(customerName: String) = viewModelScope.launch {

        val customer = Customer(name = customerName)
        customerRepository.addCustomer(Mapper.customerModelToCustomerEntity(customer))
    }

    fun updateCustomerName(customerId: Int, newName: String) = viewModelScope.launch {
        customerRepository.updateCustomerName(customerId, newName)
    }

    fun deleteCustomer(customerId: Int) = viewModelScope.launch {
        customerRepository.deleteCustomer(customerId)
    }
}