package com.ghadeer.posassignment.repository.interfaces

import com.ghadeer.posassignment.data.model.DataResult
import com.ghadeer.posassignment.database.entities.OrderEntity
import com.ghadeer.posassignment.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {

    suspend fun loadOrders()

    suspend fun getOrdersFlow(): Flow<List<OrderEntity>>
    suspend fun getPagedOrdersList(limit: Int,
                                     page: Int,
                                     filtersMap: Map<String, String>,): DataResult<List<OrderEntity>>

    suspend fun addOrder(orderEntity: OrderEntity, orderItemsEntitiesList: List<OrderItemEntity>): DataResult<Int>
    suspend fun addOrders(ordersEntitiesList: List<OrderEntity>)
    suspend fun getOrderItems(orderId: String): DataResult<List<OrderItemEntity>>
    suspend fun getOrderByOrderId(orderId: String): DataResult<OrderEntity>
}