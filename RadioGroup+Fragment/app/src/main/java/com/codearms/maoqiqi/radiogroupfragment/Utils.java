package com.codearms.maoqiqi.radiogroupfragment;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * 创建TextView
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 11:11
 */
class Utils {

    /**
     * 创建TextView
     *
     * @param activity 上下文
     * @param resId    资源Id
     * @return TextView
     */
    static View createView(Activity activity, int resId) {
        Random random = new Random();
        int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        TextView textView = new TextView(activity);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(color);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setText(resId);
        return textView;
    }
}