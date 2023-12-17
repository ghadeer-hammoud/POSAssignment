package com.ghadeer.posassignment.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ghadeer.posassignment.database.entities.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class OrderDao : BaseDao<OrderEntity>("OrderEntity") {

    @Query("SELECT * FROM OrderEntity")
    abstract fun getOrdersListFlow(): Flow<List<OrderEntity>>

    @Query(
        "SELECT DISTINCT o.* FROM OrderEntity o " +
                "WHERE (o.orderId LIKE '%'||:searchTerm||'%')" +
                "AND (CASE WHEN :orderStatusListSize > 0 THEN o.orderStatus IN (:orderStatusList) ELSE 1 END) " +
                "AND (CASE WHEN :paymentStatusListSize > 0 THEN o.paymentStatus IN (:paymentStatusList) ELSE 1 END) " +
                "AND (CASE WHEN :startDate IS NOT NULL THEN o.createdAt >= :startDate ELSE 1 END) " +
                "AND (CASE WHEN :endDate IS NOT NULL THEN o.createdAt <= :endDate ELSE 1 END) " +
                "LIMIT :limit OFFSET :offset"
    )
    abstract suspend fun getPagedOrdersList(
        limit: Int,
        offset: Int,
        searchTerm: String,
        orderStatusList: List<Int>,
        orderStatusListSize: Int,
        paymentStatusList: List<Int>,
        paymentStatusListSize: Int,
        startDate: String?,
        endDate: String?,
    ): List<OrderEntity>

    @Query("SELECT * FROM OrderEntity WHERE orderId = :orderId LIMIT 1")
    abstract suspend fun getOrderOrderId(orderId: String): OrderEntity?

    @Query("SELECT (SELECT COUNT(*) FROM OrderEntity) == 0")
    abstract suspend fun isTableEmpty(): Boolean
}