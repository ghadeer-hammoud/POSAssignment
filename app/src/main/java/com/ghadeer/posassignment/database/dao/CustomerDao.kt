package com.ghadeer.posassignment.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ghadeer.posassignment.database.entities.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CustomerDao : BaseDao<CustomerEntity>("CustomerEntity") {

    @Query("SELECT * FROM CustomerEntity WHERE name LIKE '%' || :filter || '%'")
    abstract fun getCustomersListFlow(filter: String): Flow<List<CustomerEntity>>

    @Query(
        "SELECT DISTINCT pr.* FROM CustomerEntity pr " +
                "WHERE (pr.name LIKE '%'||:searchTerm||'%' )" +
                "LIMIT :limit OFFSET :offset"
    )
    abstract suspend fun getPagedCustomerList(
        limit: Int,
        offset: Int,
        searchTerm: String,
    ): List<CustomerEntity>


    @Query("SELECT * FROM CustomerEntity WHERE id = :customerId")
    abstract suspend fun getCustomerById(customerId: Int): CustomerEntity?

    @Query("UPDATE CustomerEntity SET name = :newName WHERE id = :customerId")
    abstract suspend fun updateCustomerName(customerId: Int, newName: String): Int

    @Query("DELETE FROM CustomerEntity WHERE id = :customerId")
    abstract suspend fun deleteCustomer(customerId: Int): Int

    @Query("SELECT (SELECT COUNT(*) FROM CategoryEntity) == 0")
    abstract suspend fun isTableEmpty(): Boolean
}