package com.codearms.maoqiqi.activityfragment.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.Utils;

import java.util.Random;

/**
 * 测试动态创建Fragment生命周期
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 11:00
 */
public class DynamicFragment extends BaseFragment {

    private String str;

    public DynamicFragment() {
        super();
        Log.d("DynamicFragment", "-->动态创建");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str = getString(R.string.dynamic_fragment) + "--" + new Random().nextInt();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return Utils.createView(getActivity(), str);
    }
}