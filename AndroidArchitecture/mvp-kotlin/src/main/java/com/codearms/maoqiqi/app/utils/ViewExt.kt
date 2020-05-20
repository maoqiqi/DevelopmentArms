package com.codearms.maoqiqi.app.utils

import com.google.android.material.snackbar.Snackbar
import android.view.View

/**
 * Provides a method to show a Snackbar.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 15:27
 */
fun View.show(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
}