package com.codearms.maoqiqi.activityfragment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.codearms.maoqiqi.activityfragment.R;

/**
 * 重写onCreateView创建Dialog
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 14:22
 */
public class DialogFragment1 extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_dialog, container, false);
    }
}