package com.codearms.maoqiqi.app.utils

import java.util.concurrent.Executor

/**
 * Allow instant execution of tasks.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/13 14:15
 */
class SingleExecutors : AppExecutors(instant, instant, instant) {
    companion object {
        private val instant = Executor { command -> command.run() }
    }
}