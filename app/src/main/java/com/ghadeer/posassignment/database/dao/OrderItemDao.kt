package com.ghadeer.posassignment.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ghadeer.posassignment.database.entities.CategoryEntity
import com.ghadeer.posassignment.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderItemDao : BaseDao<OrderItemEntity>("OrderItemEntity") {

    @Query("SELECT * FROM OrderItemEntity WHERE orderId = :orderId")
    abstract suspend fun getOrderItems(orderId: String): List<OrderItemEntity>

    @Query("SELECT (SELECT COUNT(*) FROM CategoryEntity) == 0")
    abstract suspend fun isTableEmpty(): Boolean
}