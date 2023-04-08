package com.sadaqaworks.yorubaquran.util

sealed class Resource <T>( val data : T? = null, val errorMessage: String? = null){
    class Success<T> (data: T? ) : Resource<T>(data = data)
    class Error<T>(errorMessage: String): Resource<T>(errorMessage = errorMessage)
    class Loading<T>(val isLoading:Boolean = true) :  Resource<T>()
}
