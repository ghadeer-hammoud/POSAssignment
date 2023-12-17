package com.ghadeer.posassignment.repository.datasources.local

import com.ghadeer.posassignment.database.AppDatabase
import com.ghadeer.posassignment.database.entities.CustomerEntity
import kotlinx.coroutines.flow.Flow

class CustomerLocalDataSource(private val appDatabase: AppDatabase) {

    fun getCustomers(filter: String? = null): Flow<List<CustomerEntity>> {
        return appDatabase.customerDao().getCustomersListFlow(filter ?: "")
    }

    suspend fun getPagedCustomersList(
        limit: Int,
        page: Int,
        filtersMap: Map<String, String>,
    ): List<CustomerEntity> {

        val searchTerm = filtersMap["searchTerm"] ?: ""
        return appDatabase.customerDao().getPagedCustomerList(
            limit = limit,
            offset = page,
            searchTerm = searchTerm,
        )
    }

    suspend fun addCustomer(customerEntity: CustomerEntity) {
        appDatabase.customerDao().insert(customerEntity)
    }

    suspend fun addCustomers(customersEntitiesList: List<CustomerEntity>){
        appDatabase.customerDao().insert(customersEntitiesList)
    }

    suspend fun getCustomerById(customerId: Int): CustomerEntity? {
        return appDatabase.customerDao().getCustomerById(customerId)
    }

    suspend fun updateCustomerName(customerId: Int, newName: String): Int {
        return appDatabase.customerDao().updateCustomerName(customerId, newName)
    }

    suspend fun deleteCustomer(customerId: Int): Int {
        return appDatabase.customerDao().deleteCustomer(customerId)
    }

    suspend fun isTableEmpty(): Boolean{
        return appDatabase.customerDao().isTableEmpty()
    }
}