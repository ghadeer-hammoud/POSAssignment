package com.ghadeer.posassignment.util

object StringUtils {

    fun getRandomString(length: Int = 14): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}