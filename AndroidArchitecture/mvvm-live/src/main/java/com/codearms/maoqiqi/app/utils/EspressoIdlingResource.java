package com.codearms.maoqiqi.app.utils;

import android.support.test.espresso.IdlingResource;

/**
 * Contains a static reference to {@link IdlingResource}, only available in the 'mock' build type.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/28 15:18
 */
public class EspressoIdlingResource {

    private static final String RESOURCE = "GLOBAL";

    private static SimpleCountingIdlingResource countingIdlingResource = new SimpleCountingIdlingResource(RESOURCE);

    public static void increment() {
        countingIdlingResource.increment();
    }

    public static void decrement() {
        countingIdlingResource.decrement();
    }

    public static IdlingResource getIdlingResource() {
        return countingIdlingResource;
    }
}