package com.ghadeer.posassignment.repository.imp

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.NetworkResult
import com.ghadeer.posassignment.database.entities.CustomerEntity
import com.ghadeer.posassignment.repository.datasources.local.CustomerLocalDataSource
import com.ghadeer.posassignment.repository.datasources.remote.CustomerRemoteDataSource
import com.ghadeer.posassignment.repository.interfaces.ICustomerRepository
import com.ghadeer.posassignment.util.Mapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CustomerRepositoryImp(
    private val customerLocalDataSource: CustomerLocalDataSource,
    private val customerRemoteDataSource: CustomerRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): ICustomerRepository {

    override suspend fun loadCustomers() = withContext(dispatcher){
        when(val netResult = customerRemoteDataSource.loadCustomers()){
            is NetworkResult.Success -> {
                addCustomers(netResult.data.map { Mapper.customerModelToCustomerEntity(it) })
            }
            is NetworkResult.Failure -> {}
            is NetworkResult.Error -> {}
        }
    }

    override suspend fun getCustomersFlow(
        filter: String?,
    ): Flow<List<CustomerEntity>> = withContext(dispatcher) {

        if(customerLocalDataSource.isTableEmpty())
            loadCustomers()

        return@withContext customerLocalDataSource.getCustomers()
    }

    override suspend fun getPagedCustomersList(
        limit: Int,
        page: Int,
        filtersMap: Map<String, String>
    ): DataResult<List<CustomerEntity>> = withContext(dispatcher) {

        if(customerLocalDataSource.isTableEmpty())
            loadCustomers()

        return@withContext DataResult.Success(
            customerLocalDataSource.getPagedCustomersList(
                limit = limit,
                page = page,
                filtersMap = filtersMap,
            )
        )
    }

    override suspend fun addCustomer(customerEntity: CustomerEntity): DataResult<Int> = withContext(dispatcher){
        customerLocalDataSource.addCustomer(customerEntity)
        return@withContext DataResult.Success(1)
    }

    override suspend fun addCustomers(customersEntitiesList: List<CustomerEntity>) = withContext(dispatcher) {
        customerLocalDataSource.addCustomers(customersEntitiesList)
    }

    override suspend fun getCustomerById(customerId: Int): DataResult<CustomerEntity> = withContext(dispatcher) {
        return@withContext when(val customer = customerLocalDataSource.getCustomerById(customerId)){
            null -> DataResult.Error("Cannot find customer with ID = $customerId")
            else -> DataResult.Success(customer)
        }
    }

    override suspend fun updateCustomerName(customerId: Int, newName: String): DataResult<Int> = withContext(dispatcher){
        return@withContext when(customerLocalDataSource.updateCustomerName(customerId, newName)){
            0 -> DataResult.Error("Customer has NOT been deleted.")
            else -> DataResult.Success(1)
        }
    }

    override suspend fun deleteCustomer(customerId: Int): DataResult<Int> = withContext(dispatcher){
        return@withContext when(customerLocalDataSource.deleteCustomer(customerId)){
            0 -> DataResult.Error("Customer has NOT been deleted.")
            else -> DataResult.Success(1)
        }
    }
}