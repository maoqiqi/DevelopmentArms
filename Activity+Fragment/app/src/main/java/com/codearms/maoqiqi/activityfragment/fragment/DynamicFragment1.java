package com.codearms.maoqiqi.activityfragment.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.Utils;

import java.util.Random;

/**
 * 测试动态创建DynamicFragment1生命周期
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 11:00
 */
public class DynamicFragment1 extends BaseFragment {

    private String str;

    public DynamicFragment1() {
        super();
        Log.d("DynamicFragment1", "-->动态创建1111");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        str = getString(R.string.dynamic_fragment_1) + "--" + new Random().nextInt();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return Utils.createView(getActivity(), str);
    }
}