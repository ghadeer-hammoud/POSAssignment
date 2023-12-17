package com.ghadeer.posassignment.repository.datasources.local

import com.ghadeer.posassignment.database.AppDatabase
import com.ghadeer.posassignment.database.entities.OrderItemEntity

class OrderItemLocalDataSource(private val appDatabase: AppDatabase) {

    suspend fun getOrderItems(orderId: String): List<OrderItemEntity> {
        return appDatabase.orderItemDao().getOrderItems(orderId)
    }

    suspend fun addOrderItems(orderItemsEntitiesList: List<OrderItemEntity>){
        appDatabase.orderItemDao().insert(orderItemsEntitiesList)
    }

    suspend fun isTableEmpty(): Boolean{
        return appDatabase.orderItemDao().isTableEmpty()
    }
}