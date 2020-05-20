package com.codearms.maoqiqi.app.utils;

import com.google.android.material.snackbar.Snackbar;
import android.view.View;

/**
 * Provides a method to show a Snackbar.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/21 15:27
 */
public class SnackbarUtils {

    public static void show(View view, String text) {
        if (view == null || text == null) return;
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
    }
}