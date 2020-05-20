package com.codearms.maoqiqi.activityfragment.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

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