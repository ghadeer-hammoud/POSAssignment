package com.ghadeer.posassignment.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ghadeer.posassignment.database.dao.*
import com.ghadeer.posassignment.database.entities.*

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CustomerEntity::class,
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    companion object {

        private const val TAG = "AppDatabase"
        private const val DB_NAME = "app_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { buildDatabase(context) }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).build()

            return INSTANCE!!
        }
    }

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun customerDao(): CustomerDao
}