package com.ghadeer.posassignment.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Category(
    val id: Int = 0,
    val name: String = "",
): Parcelable