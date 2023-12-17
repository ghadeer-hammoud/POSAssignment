package com.ghadeer.posassignment.repository.datasources.remote

import com.ghadeer.posassignment.data.model.NetworkResult
import com.ghadeer.posassignment.data.model.Order

class OrderRemoteDataSource {

    suspend fun loadOrders(): NetworkResult<List<Order>> {

        return try {
            val ordersList = listOf<Order>()
            NetworkResult.Success(ordersList)
        } catch (e: Exception){
            NetworkResult.Failure(message = e.localizedMessage)
        }
    }

}