package com.ghadeer.posassignment.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val barcode: String = "",
    val categoryId: Int = 0,
)