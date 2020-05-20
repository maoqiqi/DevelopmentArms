package com.codearms.maoqiqi.activityfragment.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.fragment.DialogFragment1;
import com.codearms.maoqiqi.activityfragment.fragment.DialogFragment2;

/**
 * 使用DialogFragment创建对话框
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 14:20
 */
public class DialogFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_fragment);
    }

    public void showDialogFragment1(View view) {
        new DialogFragment1().show(getSupportFragmentManager(), "TAG_1");
    }

    public void showDialogFragment2(View view) {
        new DialogFragment2().show(getSupportFragmentManager(), "TAG_2");
    }
}