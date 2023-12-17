package com.ghadeer.posassignment.repository.datasources.local

import com.ghadeer.posassignment.database.AppDatabase
import com.ghadeer.posassignment.database.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductLocalDataSource(private val appDatabase: AppDatabase) {

    fun getProducts(filter: String? = null): Flow<List<ProductEntity>> {
        return appDatabase.productDao().getProductListFlow(filter ?: "")
    }

    suspend fun getPagedProductsList(
        limit: Int,
        page: Int,
        filtersMap: Map<String, String>,
    ): List<ProductEntity> {

        val searchTerm = filtersMap["searchTerm"] ?: ""
        val categoriesIdsList = when (filtersMap["categories"]?.isEmpty()) {
            null, true -> emptyList()
            false -> filtersMap["categories"]?.split(",")?.toList()?.map { it.toInt() } ?: emptyList()
        }

        return appDatabase.productDao().getPagedProductList(
            limit = limit,
            offset = page,
            searchTerm = searchTerm,
            categoriesIdsList = categoriesIdsList,
            categoriesIdsListSize = categoriesIdsList.size,
        )
    }

    suspend fun addProduct(productEntity: ProductEntity) {
        appDatabase.productDao().insert(productEntity)
    }

    suspend fun addProducts(productsEntitiesList: List<ProductEntity>){
        appDatabase.productDao().insert(productsEntitiesList)
    }

    suspend fun isTableEmpty(): Boolean{
        return appDatabase.productDao().isTableEmpty()
    }

    suspend fun updateProduct(productEntity: ProductEntity) {
        appDatabase.productDao().update(productEntity)
    }

    suspend fun deleteProduct(productId: Int): Int {
        return appDatabase.productDao().deleteProductById(productId)
    }

    suspend fun getProductById(productId: Int): ProductEntity? {
        return appDatabase.productDao().getProductById(productId)
    }
}