package com.codearms.maoqiqi.activityfragment.activity;

import android.os.Bundle;

import com.codearms.maoqiqi.activityfragment.R;

/**
 * Dialog模式Activity
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/7 11:45
 */
public class DialogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        // setFinishOnTouchOutside(false);
    }
}