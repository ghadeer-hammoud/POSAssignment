package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import com.ghadeer.posassignment.util.getDate
import kotlinx.parcelize.Parcelize

@Parcelize
data class Customer(
    val id: Int = 0,
    val name: String = "",
): Parcelable
