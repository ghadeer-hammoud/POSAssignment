package com.ghadeer.posassignment.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
)