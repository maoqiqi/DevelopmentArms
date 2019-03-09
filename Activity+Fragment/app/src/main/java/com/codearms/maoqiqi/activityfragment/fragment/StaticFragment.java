package com.codearms.maoqiqi.activityfragment.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.Utils;

/**
 * 测试静态创建Fragment生命周期
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 10:00
 */
public class StaticFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return Utils.createView(getActivity(), getString(R.string.static_fragment));
    }
}