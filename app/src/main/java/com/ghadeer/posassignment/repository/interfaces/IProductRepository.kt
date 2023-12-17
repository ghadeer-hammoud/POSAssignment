package com.ghadeer.posassignment.repository.interfaces

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.database.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

interface IProductRepository {

    suspend fun loadProducts()

    suspend fun getProductsFlow(filter: String? = null): Flow<List<ProductEntity>>
    suspend fun getPagedProductsList(limit: Int,
                                     page: Int,
                                     filtersMap: Map<String, String>,): DataResult<List<ProductEntity>>

    suspend fun addProduct(productEntity: ProductEntity): DataResult<Int>
    suspend fun addProducts(productsEntitiesList: List<ProductEntity>)
    suspend fun updateProduct(productEntity: ProductEntity): DataResult<Int>
    suspend fun deleteProduct(productId: Int): DataResult<Int>
    suspend fun getProductById(productId: Int): DataResult<ProductEntity>
}