package com.codearms.maoqiqi.activityfragment.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.codearms.maoqiqi.activityfragment.R;

/**
 * 重写onCreateDialog创建Dialog
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 14:48
 */
public class DialogFragment2 extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        return new AlertDialog.Builder(getActivity(), R.style.DialogTheme).setView(R.layout.activity_dialog).create();
    }
}