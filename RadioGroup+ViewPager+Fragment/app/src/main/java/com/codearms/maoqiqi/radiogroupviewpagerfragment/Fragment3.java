package com.codearms.maoqiqi.radiogroupviewpagerfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codearms.maoqiqi.lazyload.LazyLoadFragment;

/**
 * Fragment3
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/2/15 16:16
 */
public class Fragment3 extends LazyLoadFragment {

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment3.
     */
    public static Fragment3 newInstance() {
        Fragment3 fragment = new Fragment3();
        Bundle args = new Bundle();
        args.putString("key", "value");
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment3() {
        super();
        Log.d("Fragment3", "-->创建Fragment3");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    protected View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return Utils.createView(getActivity(), R.string.index_3);
    }
}