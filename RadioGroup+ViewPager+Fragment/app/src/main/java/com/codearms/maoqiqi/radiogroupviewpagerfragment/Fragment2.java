package com.codearms.maoqiqi.radiogroupviewpagerfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.lazyload.LazyLoadFragment;

/**
 * Fragment2
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/2/15 16:16
 */
public class Fragment2 extends LazyLoadFragment {

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment2.
     */
    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString("key", "value");
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment2() {
        super();
        Log.d("Fragment2", "-->创建Fragment2");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    protected View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return Utils.createView(getActivity(), R.string.index_2);
    }
}