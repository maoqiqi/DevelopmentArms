package com.codearms.maoqiqi.app.utils;

import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An simple counter implementation of {@link IdlingResource} that determines idleness by maintaining an internal counter.
 * When the counter is 0 - it is considered to be idle, when it is  non-zero it is not idle.
 * This is very similar to the way a {@link java.util.concurrent.Semaphore} behaves.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/28 15:18
 */
public final class SimpleCountingIdlingResource implements IdlingResource {

    private final String resourceName;

    private final AtomicInteger counter = new AtomicInteger(0);

    // Written from main thread, read from any thread.
    private volatile IdlingResource.ResourceCallback resourceCallback;

    SimpleCountingIdlingResource(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getName() {
        return resourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    void increment() {
        counter.getAndIncrement();
    }

    /**
     * Decrements the count of in-flight transactions to the resource being monitored.
     */
    void decrement() {
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0) {
            // We've gone from non-zero to zero. That means we're idle now! Tell espresso.
            if (resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0) {
            throw new IllegalArgumentException("Counter has been corrupted!");
        }
    }
}