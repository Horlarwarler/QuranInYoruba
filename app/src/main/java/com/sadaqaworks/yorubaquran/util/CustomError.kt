package com.sadaqaworks.yorubaquran.util

class CustomError(val error:String, val errorCause: Throwable? = null)  : Exception(
        error, errorCause
)