package com.codearms.maoqiqi.app.utils

import androidx.test.espresso.IdlingResource

import java.util.concurrent.atomic.AtomicInteger

/**
 * An simple counter implementation of [IdlingResource] that determines idleness by maintaining an internal counter.
 * When the counter is 0 - it is considered to be idle, when it is  non-zero it is not idle.
 * This is very similar to the way a [java.util.concurrent.Semaphore] behaves.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/28 15:18
 */
class SimpleCountingIdlingResource internal constructor(private val resourceName: String) : IdlingResource {

    private val counter = AtomicInteger(0)

    // Written from main thread, read from any thread.
    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = resourceName

    override fun isIdleNow(): Boolean = counter.get() == 0

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.resourceCallback = callback
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    internal fun increment() {
        counter.getAndIncrement()
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     */
    internal fun decrement() {
        val counterVal = counter.decrementAndGet()
        if (counterVal == 0) {
            // We've gone from non-zero to zero. That means we're idle now! Tell espresso.
            resourceCallback?.onTransitionToIdle()
        }

        if (counterVal < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }
}