package com.codearms.maoqiqi.radiogroupfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Fragment1
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/18 11:20
 */
public class Fragment1 extends BaseFragment {

    private View rootView;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment1.
     */
    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString("key", "value");
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment1() {
        super();
        Log.d("Fragment1", "-->创建Fragment1");
    }

    // TODO: 19/2/25 分析log1.txt需要将下面方法注释掉，另外三个Fragment一样
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) rootView = Utils.createView(getActivity(), R.string.index_1);
        return rootView;
    }
}