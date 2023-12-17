package com.ghadeer.posassignment.repository.interfaces

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.database.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {

    suspend fun loadCategories()

    suspend fun getCategories(
        filter: String? = null,
        page: Int? = null,
        limit: Int? = null,
    ): Flow<List<CategoryEntity>>

    suspend fun addCategory(categoryEntity: CategoryEntity): DataResult<Int>
    suspend fun addCategories(categoriesEntitiesList: List<CategoryEntity>)
    suspend fun getCategoryById(categoryId: Int): DataResult<CategoryEntity>
}