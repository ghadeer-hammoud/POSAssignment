package com.ghadeer.posassignment.repository.datasources.local

import com.ghadeer.posassignment.database.AppDatabase
import com.ghadeer.posassignment.database.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryLocalDataSource(private val appDatabase: AppDatabase) {

    fun getCategories(filter: String? = null,
                                       page: Int? = null,
                                       limit: Int? = null
    ): Flow<List<CategoryEntity>> {
        return appDatabase.categoryDao().getCategories(filter ?: "")
    }

    suspend fun addCategory(categoryEntity: CategoryEntity) {
        appDatabase.categoryDao().insert(categoryEntity)
    }

    suspend fun addCategories(categoriesEntitiesList: List<CategoryEntity>){
        appDatabase.categoryDao().insert(categoriesEntitiesList)
    }

    suspend fun getCategoryById(categoryId: Int): CategoryEntity? {
        return appDatabase.categoryDao().getCategoryById(categoryId)
    }

    suspend fun isTableEmpty(): Boolean{
        return appDatabase.categoryDao().isTableEmpty()
    }
}