package com.ghadeer.posassignment.repository.datasources.local

import android.util.Log
import com.ghadeer.posassignment.database.AppDatabase
import com.ghadeer.posassignment.database.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

class OrderLocalDataSource(private val appDatabase: AppDatabase) {

    fun getOrders(filter: String? = null): Flow<List<OrderEntity>> {
        return appDatabase.orderDao().getOrdersListFlow()
    }

    suspend fun getPagedOrdersList(
        limit: Int,
        page: Int,
        filtersMap: Map<String, String>,
    ): List<OrderEntity> {

        val searchTerm = filtersMap["searchTerm"] ?: ""
        val orderStatusList = when (filtersMap["orderStatus"]?.isEmpty()) {
            null, true -> emptyList()
            false -> filtersMap["orderStatus"]?.split(",")?.toList()?.map { it.toInt() } ?: emptyList()
        }
        val paymentStatusList = when (filtersMap["paymentStatus"]?.isEmpty()) {
            null, true -> emptyList()
            false -> filtersMap["paymentStatus"]?.split(",")?.toList()?.map { it.toInt() } ?: emptyList()
        }
        val startDate = when(filtersMap["startDate"]?.isEmpty()){
            null, true -> null
            false -> filtersMap["startDate"]
        }
        val endDate = when(filtersMap["endDate"]?.isEmpty()){
            null, true -> null
            false -> filtersMap["endDate"]
        }

        return appDatabase.orderDao().getPagedOrdersList(
            limit = limit,
            offset = page,
            searchTerm = searchTerm,
            orderStatusList = orderStatusList,
            orderStatusListSize = orderStatusList.size,
            paymentStatusList = paymentStatusList,
            paymentStatusListSize = paymentStatusList.size,
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun addOrder(orderEntity: OrderEntity) {
        appDatabase.orderDao().insert(orderEntity)
    }

    suspend fun addOrders(ordersEntitiesList: List<OrderEntity>){
        appDatabase.orderDao().insert(ordersEntitiesList)
    }

    suspend fun getOrderById(orderId: String): OrderEntity? {
        return appDatabase.orderDao().getOrderOrderId(orderId)
    }

    suspend fun isTableEmpty(): Boolean{
        return appDatabase.orderDao().isTableEmpty()
    }
}