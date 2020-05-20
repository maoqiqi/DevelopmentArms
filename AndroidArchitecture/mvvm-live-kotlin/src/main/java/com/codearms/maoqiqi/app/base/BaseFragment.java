package com.codearms.maoqiqi.app.base;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * base fragment
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/6 17:54
 */
public class BaseFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "-->setUserVisibleHint(boolean isVisibleToUser) = " + isVisibleToUser);
    }

    // 该方法只在我们直接用标签在布局中定义的时候才会被调用
    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(TAG, "-->onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState)");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "-->onAttach(Context context)");
    }

    // 系统会在创建片段时调用此方法，只会调用一次。您应该在此初始化您想在片段暂停或停止后恢复时需要的数据。
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "-->onCreate(@Nullable Bundle savedInstanceState)");
    }

    // 每次创建，绘制改Fragment的View组件时回调，会将显示的View返回
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "-->onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "-->onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)");
    }

    // 当Fragment所在的Activity启动完成后回调
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "-->onActivityCreated(@Nullable Bundle savedInstanceState)");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "-->onViewStateRestored(@Nullable Bundle savedInstanceState)");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "-->onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "-->onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "-->onPause()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "-->onSaveInstanceState(@NonNull Bundle outState)");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "-->onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "-->onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "-->onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "-->onDetach()");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "-->onConfigurationChanged(Configuration newConfig)");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "-->onHiddenChanged(boolean hidden) = " + hidden);
    }
}