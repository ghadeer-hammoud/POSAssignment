package com.ghadeer.posassignment.repository.datasources.remote

import com.ghadeer.posassignment.data.model.Category
import com.ghadeer.posassignment.data.model.NetworkResult

class CategoryRemoteDataSource {

    suspend fun loadCategories(): NetworkResult<List<Category>> {

        return try {
            val categoriesList = listOf<Category>()
            NetworkResult.Success(categoriesList)
        } catch (e: Exception){
            NetworkResult.Failure(message = e.localizedMessage)
        }
    }

}