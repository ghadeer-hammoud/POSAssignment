package com.ghadeer.posassignment.repository.interfaces

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.database.entities.CustomerEntity
import kotlinx.coroutines.flow.Flow

interface ICustomerRepository {

    suspend fun loadCustomers()

    suspend fun getCustomersFlow(filter: String? = null): Flow<List<CustomerEntity>>
    suspend fun getPagedCustomersList(limit: Int,
                                     page: Int,
                                     filtersMap: Map<String, String>,): DataResult<List<CustomerEntity>>

    suspend fun addCustomer(customerEntity: CustomerEntity): DataResult<Int>
    suspend fun addCustomers(customersEntitiesList: List<CustomerEntity>)
    suspend fun getCustomerById(customerId: Int): DataResult<CustomerEntity>
    suspend fun updateCustomerName(customerId: Int, newName: String): DataResult<Int>
    suspend fun deleteCustomer(customerId: Int): DataResult<Int>
}