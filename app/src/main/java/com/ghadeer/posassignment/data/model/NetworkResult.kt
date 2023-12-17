package com.ghadeer.posassignment.data.model

sealed class NetworkResult<out R> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error<out E>(val error: E) : NetworkResult<E>()
    data class Failure(val message: String) : NetworkResult<Nothing>()


    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[Error=$error]"
            is Failure -> "Error[Message=$message]"
        }
    }
}