package com.codearms.maoqiqi.radiogroupfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment4
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/18 11:20
 */
public class Fragment4 extends BaseFragment {

    private View rootView;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) rootView = Utils.createView(getActivity(), R.string.index_4);
        return rootView;
    }
}