package com.ghadeer.posassignment.repository.imp

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.data.model.NetworkResult
import com.ghadeer.posassignment.database.entities.OrderEntity
import com.ghadeer.posassignment.database.entities.OrderItemEntity
import com.ghadeer.posassignment.repository.datasources.local.OrderItemLocalDataSource
import com.ghadeer.posassignment.repository.datasources.local.OrderLocalDataSource
import com.ghadeer.posassignment.repository.datasources.remote.OrderRemoteDataSource
import com.ghadeer.posassignment.repository.interfaces.IOrderRepository
import com.ghadeer.posassignment.util.Mapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OrderRepositoryImp(
    private val orderLocalDataSource: OrderLocalDataSource,
    private val orderItemLocalDataSource: OrderItemLocalDataSource,
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): IOrderRepository {

    override suspend fun loadOrders() = withContext(dispatcher){
        when(val netResult = orderRemoteDataSource.loadOrders()){
            is NetworkResult.Success -> {
                addOrders(netResult.data.map { Mapper.orderModelToOrderEntity(it) })
            }
            is NetworkResult.Failure -> {}
            is NetworkResult.Error -> {}
        }
    }

    override suspend fun getOrdersFlow(): Flow<List<OrderEntity>> = withContext(dispatcher) {

        if(orderLocalDataSource.isTableEmpty())
            loadOrders()

        return@withContext orderLocalDataSource.getOrders()
    }

    override suspend fun getPagedOrdersList(
        limit: Int,
        page: Int,
        filtersMap: Map<String, String>
    ): DataResult<List<OrderEntity>> = withContext(dispatcher) {

        if(orderLocalDataSource.isTableEmpty())
            loadOrders()

        return@withContext DataResult.Success(
            orderLocalDataSource.getPagedOrdersList(
                limit = limit,
                page = page,
                filtersMap = filtersMap,
            )
        )
    }

    override suspend fun addOrder(orderEntity: OrderEntity, orderItemsEntitiesList: List<OrderItemEntity>): DataResult<Int> = withContext(dispatcher){
        orderLocalDataSource.addOrder(orderEntity)
        orderItemLocalDataSource.addOrderItems(orderItemsEntitiesList)
        return@withContext DataResult.Success(1)
    }

    override suspend fun addOrders(ordersEntitiesList: List<OrderEntity>) = withContext(dispatcher) {
        orderLocalDataSource.addOrders(ordersEntitiesList)
    }

    override suspend fun getOrderItems(orderId: String): DataResult<List<OrderItemEntity>> = withContext(dispatcher) {
        return@withContext DataResult.Success(orderItemLocalDataSource.getOrderItems(orderId))
    }


    override suspend fun getOrderByOrderId(orderId: String): DataResult<OrderEntity> = withContext(dispatcher){
        return@withContext when(val orderEntity = orderLocalDataSource.getOrderById(orderId)){
            null -> DataResult.Error("Cannot find order with ID = $orderId")
            else -> DataResult.Success(orderEntity)
        }
    }
}