package com.ghadeer.posassignment.repository.imp

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.NetworkResult
import com.ghadeer.posassignment.database.entities.CategoryEntity
import com.ghadeer.posassignment.repository.datasources.local.CategoryLocalDataSource
import com.ghadeer.posassignment.repository.datasources.remote.CategoryRemoteDataSource
import com.ghadeer.posassignment.repository.interfaces.ICategoryRepository
import com.ghadeer.posassignment.util.Mapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CategoryRepositoryImp(
    private val categoryLocalDataSource: CategoryLocalDataSource,
    private val categoryRemoteDataSource: CategoryRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): ICategoryRepository {

    override suspend fun loadCategories() = withContext(dispatcher){
        when(val netResult = categoryRemoteDataSource.loadCategories()){
            is NetworkResult.Success -> {
                addCategories(netResult.data.map { Mapper.categoryModelToCategoryEntity(it) })
            }
            is NetworkResult.Failure -> {}
            is NetworkResult.Error -> {}
        }
    }

    override suspend fun getCategories(
        filter: String?,
        page: Int?,
        limit: Int?,
    ): Flow<List<CategoryEntity>> = withContext(dispatcher) {

        if(categoryLocalDataSource.isTableEmpty())
            loadCategories()

        return@withContext categoryLocalDataSource.getCategories()
    }

    override suspend fun addCategory(categoryEntity: CategoryEntity): DataResult<Int> = withContext(dispatcher){
        categoryLocalDataSource.addCategory(categoryEntity)
        return@withContext DataResult.Success(1)
    }

    override suspend fun addCategories(categoriesEntitiesList: List<CategoryEntity>) = withContext(dispatcher) {
        categoryLocalDataSource.addCategories(categoriesEntitiesList)
    }

    override suspend fun getCategoryById(categoryId: Int): DataResult<CategoryEntity> = withContext(dispatcher) {
        return@withContext when(val category = categoryLocalDataSource.getCategoryById(categoryId)){
            null -> DataResult.Error("Cannot find category with ID = $categoryId")
            else -> DataResult.Success(category)
        }
    }
}