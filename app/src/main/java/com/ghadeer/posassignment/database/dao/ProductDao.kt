package com.ghadeer.posassignment.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ghadeer.posassignment.database.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProductDao : BaseDao<ProductEntity>("ProductEntity") {

    @Query("SELECT * FROM ProductEntity WHERE name LIKE '%' || :filter || '%'")
    abstract fun getProductListFlow(filter: String): Flow<List<ProductEntity>>

    @Query(
        "SELECT DISTINCT pr.* FROM ProductEntity pr " +
                "WHERE (pr.name LIKE '%'||:searchTerm||'%' OR pr.barcode LIKE '%'||:searchTerm||'%')" +
                "AND (CASE WHEN :categoriesIdsListSize > 0 THEN pr.categoryId IN (:categoriesIdsList) ELSE 1 END) " +
                "LIMIT :limit OFFSET :offset"
    )
    abstract suspend fun getPagedProductList(
        limit: Int,
        offset: Int,
        searchTerm: String,
        categoriesIdsList: List<Int>,
        categoriesIdsListSize: Int,
    ): List<ProductEntity>


    @Query("SELECT * FROM ProductEntity WHERE id = :productId")
    abstract suspend fun getProductById(productId: Int): ProductEntity?

    @Query("SELECT name FROM ProductEntity WHERE id = :productId")
    abstract suspend fun getProductNameById(productId: Int): String?

    @Query("DELETE FROM ProductEntity WHERE id = :productId")
    abstract suspend fun deleteProductById(productId: Int): Int

    @Query("SELECT (SELECT COUNT(*) FROM ProductEntity) == 0")
    abstract suspend fun isTableEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM ProductEntity")
    abstract fun getTotalProducts(): Flow<Int>
}