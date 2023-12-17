package com.ghadeer.posassignment.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.ghadeer.posassignment.database.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao : BaseDao<CategoryEntity>("CategoryEntity") {

    @Query("SELECT * FROM CategoryEntity WHERE name LIKE '%' || :filter || '%'")
    abstract fun getCategories(filter: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM CategoryEntity WHERE id = :categoryId LIMIT 1")
    abstract suspend fun getCategoryById(categoryId: Int): CategoryEntity?

    @Query("SELECT (SELECT COUNT(*) FROM CategoryEntity) == 0")
    abstract suspend fun isTableEmpty(): Boolean
}