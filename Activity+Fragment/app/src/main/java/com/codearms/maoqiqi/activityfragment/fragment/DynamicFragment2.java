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
 * 测试动态创建DynamicFragment2生命周期
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 11:00
 */
public class DynamicFragment2 extends BaseFragment {

    private String str;

    public DynamicFragment2() {
        super();
        Log.d("DynamicFragment2", "-->动态创建2222");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置下面为true后,Fragment意外销毁会保留实例
        setRetainInstance(true);
        // 意外销毁时,onDestroy()和onCreate()回调方法不会调用,所有str数据不会清空,还是原来的。
        str = getString(R.string.dynamic_fragment_2) + "--" + new Random().nextInt();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return Utils.createView(getActivity(), str);
    }
}