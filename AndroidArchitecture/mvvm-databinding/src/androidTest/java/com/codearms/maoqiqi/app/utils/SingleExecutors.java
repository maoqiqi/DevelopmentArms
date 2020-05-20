package com.codearms.maoqiqi.app.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Allow instant execution of tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 14:15
 */
public class SingleExecutors extends AppExecutors {

    private static Executor instant = new Executor() {
        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    };

    public SingleExecutors() {
        super(instant, instant, instant);
    }
}