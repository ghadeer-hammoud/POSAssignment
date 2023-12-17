package com.ghadeer.posassignment.repository.datasources.remote

import com.ghadeer.posassignment.data.model.Customer
import com.ghadeer.posassignment.data.model.NetworkResult

class CustomerRemoteDataSource {

    suspend fun loadCustomers(): NetworkResult<List<Customer>> {

        return try {
            val customersList = listOf<Customer>(
            )
            NetworkResult.Success(customersList)
        } catch (e: Exception){
            NetworkResult.Failure(message = e.localizedMessage)
        }
    }

}