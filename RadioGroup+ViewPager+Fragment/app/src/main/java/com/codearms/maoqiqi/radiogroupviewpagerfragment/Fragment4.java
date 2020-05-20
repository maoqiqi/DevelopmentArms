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
 * Fragment4
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/2/15 16:16
 */
public class Fragment4 extends LazyLoadFragment {

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment4.
     */
    public static Fragment4 newInstance() {
        Fragment4 fragment = new Fragment4();
        Bundle args = new Bundle();
        args.putString("key", "value");
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment4() {
        super();
        Log.d("Fragment4", "-->创建Fragment4");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    protected View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return Utils.createView(getActivity(), R.string.index_4);
    }
}