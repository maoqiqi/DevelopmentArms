package com.codearms.maoqiqi.app

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 17:18
 */
class Event<T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun getContent(): T = content
}