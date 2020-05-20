package com.codearms.maoqiqi.activityfragment.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.fragment.DynamicFragment;
import com.codearms.maoqiqi.activityfragment.fragment.DynamicFragment1;
import com.codearms.maoqiqi.activityfragment.fragment.DynamicFragment2;

/**
 * Activity+Fragment生命周期
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/9 18:58
 */
public class FragmentLifecycleActivity extends BaseActivity {

    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static final String FRAGMENT_TAG_1 = "FRAGMENT_TAG_1";
    private static final String FRAGMENT_TAG_2 = "FRAGMENT_TAG_2";

    private DynamicFragment dynamicFragment;
    private DynamicFragment1 dynamicFragment1;
    private DynamicFragment2 dynamicFragment2;

    // 测试create2打开注释,注释掉上面重复的申明
    // private DynamicFragment dynamicFragment = new DynamicFragment();
    // private DynamicFragment1 dynamicFragment1 = new DynamicFragment1();
    // private DynamicFragment2 dynamicFragment2 = new DynamicFragment2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_lifecycle);

        // create1();
        // create2();
        create3();
    }

    // 以下写法存在严重的问题,当Activity被意外终止时,恢复时会创建多份,可以仔细查看日志
    private void create1() {
        // 1.创建Fragment对象
        dynamicFragment = new DynamicFragment();
        // 2.得到FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 3.得到fragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // 4.添加Fragment对象
        fragmentTransaction.add(R.id.fl_container, dynamicFragment, FRAGMENT_TAG);
        // 5.添加到回退栈
        fragmentTransaction.addToBackStack(null);
        // 6.提交
        fragmentTransaction.commit();

        // 每次都创建一个Fragment
        dynamicFragment1 = new DynamicFragment1();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container_1, dynamicFragment1, FRAGMENT_TAG_1).addToBackStack(null).commit();
        dynamicFragment2 = new DynamicFragment2();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container_2, dynamicFragment2, FRAGMENT_TAG_2).addToBackStack(null).commit();
    }

    // 问题同上
    private void create2() {
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, dynamicFragment, FRAGMENT_TAG).addToBackStack(null).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container_1, dynamicFragment1, FRAGMENT_TAG_1).addToBackStack(null).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container_2, dynamicFragment2, FRAGMENT_TAG_2).addToBackStack(null).commit();
    }

    // 正确使用方式
    private void create3() {
        dynamicFragment = (DynamicFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (dynamicFragment == null) {
            Log.e("info", "创建DynamicFragment");
            dynamicFragment = new DynamicFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container, dynamicFragment, FRAGMENT_TAG).addToBackStack(null).commit();
        } else {
            Log.e("info", "复用DynamicFragment");
        }

        dynamicFragment1 = (DynamicFragment1) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_1);
        if (dynamicFragment1 == null) {
            Log.e("info", "创建DynamicFragment1");
            dynamicFragment1 = new DynamicFragment1();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container_1, dynamicFragment1, FRAGMENT_TAG_1).addToBackStack(null).commit();
        } else {
            Log.e("info", "复用DynamicFragment1");
        }

        dynamicFragment2 = (DynamicFragment2) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_2);
        if (dynamicFragment2 == null) {
            Log.e("info", "创建DynamicFragment2");
            dynamicFragment2 = new DynamicFragment2();
            getSupportFragmentManager().beginTransaction().add(R.id.fl_container_2, dynamicFragment2, FRAGMENT_TAG_2).addToBackStack(null).commit();
        } else {
            Log.e("info", "复用DynamicFragment2");
        }
    }
}