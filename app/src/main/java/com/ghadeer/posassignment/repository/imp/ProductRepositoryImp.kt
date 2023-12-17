package com.ghadeer.posassignment.repository.imp

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.NetworkResult
import com.ghadeer.posassignment.database.entities.ProductEntity
import com.ghadeer.posassignment.repository.datasources.local.ProductLocalDataSource
import com.ghadeer.posassignment.repository.datasources.remote.ProductRemoteDataSource
import com.ghadeer.posassignment.repository.interfaces.IProductRepository
import com.ghadeer.posassignment.util.Mapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ProductRepositoryImp(
    private val productLocalDataSource: ProductLocalDataSource,
    private val productRemoteDataSource: ProductRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): IProductRepository {

    override suspend fun loadProducts() = withContext(dispatcher){
        when(val netResult = productRemoteDataSource.loadProducts()){
            is NetworkResult.Success -> {
                addProducts(netResult.data.map { Mapper.productModelToProductEntity(it) })
            }
            is NetworkResult.Failure -> {}
            is NetworkResult.Error -> {}
        }
    }

    override suspend fun getProductsFlow(
        filter: String?,
    ): Flow<List<ProductEntity>> = withContext(dispatcher) {

        if(productLocalDataSource.isTableEmpty())
            loadProducts()

        return@withContext productLocalDataSource.getProducts()
    }

    override suspend fun getPagedProductsList(
        limit: Int,
        page: Int,
        filtersMap: Map<String, String>
    ): DataResult<List<ProductEntity>> = withContext(dispatcher) {

        if(productLocalDataSource.isTableEmpty())
            loadProducts()

        return@withContext DataResult.Success(
            productLocalDataSource.getPagedProductsList(
                limit = limit,
                page = page,
                filtersMap = filtersMap,
            )
        )
    }

    override suspend fun addProduct(productEntity: ProductEntity): DataResult<Int> = withContext(dispatcher){
        productLocalDataSource.addProduct(productEntity)
        return@withContext DataResult.Success(1)
    }

    override suspend fun addProducts(productsEntitiesList: List<ProductEntity>) = withContext(dispatcher) {
        productLocalDataSource.addProducts(productsEntitiesList)
    }

    override suspend fun updateProduct(productEntity: ProductEntity): DataResult<Int> = withContext(dispatcher){
        productLocalDataSource.updateProduct(productEntity)
        return@withContext DataResult.Success(1)
    }

    override suspend fun deleteProduct(productId: Int): DataResult<Int> = withContext(dispatcher){
        return@withContext when(productLocalDataSource.deleteProduct(productId)){
            0 -> DataResult.Error("Cannot delete product.")
            else -> DataResult.Success(1)
        }
    }

    override suspend fun getProductById(productId: Int): DataResult<ProductEntity> = withContext(dispatcher){
        return@withContext when(val productEntity = productLocalDataSource.getProductById(productId)){
            null -> DataResult.Error("Cannot find product with ID = $productId")
            else -> DataResult.Success(productEntity)
        }
    }
}