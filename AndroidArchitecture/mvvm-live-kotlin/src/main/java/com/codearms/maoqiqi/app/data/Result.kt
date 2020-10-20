package com.codearms.maoqiqi.app.data

import java.lang.Exception

/**
 * A generic class that holds a value with its loading status.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2020-09-27 15:54
 */
sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Loading(val time: Long) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading[time=$time]"
        }
    }
}