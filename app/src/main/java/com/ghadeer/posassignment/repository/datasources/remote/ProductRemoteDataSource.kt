package com.ghadeer.posassignment.repository.datasources.remote

import com.ghadeer.posassignment.data.model.NetworkResult
import com.ghadeer.posassignment.data.model.Product

class ProductRemoteDataSource {

    suspend fun loadProducts(): NetworkResult<List<Product>> {

        return try {
            val productsList = listOf<Product>(
//                Product(1, "Product 1", 5.0),
//                Product(2, "Product 2", 15.0),
//                Product(3, "Product 3", 6.0),
//                Product(4, "Product 4", 18.0),
//                Product(5, "Product 5", 22.0),
//                Product(6, "Product 6", 9.0),
//                Product(7, "Product 7", 11.0),
//                Product(8, "Product 8", 33.0),
            )
            NetworkResult.Success(productsList)
        } catch (e: Exception){
            NetworkResult.Failure(message = e.localizedMessage)
        }
    }

}