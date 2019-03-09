package com.codearms.maoqiqi.radiogroupfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

/**
 * 主页
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/18 10:58
 */
public class MainActivity extends BaseActivity {

    private static final String TAG_1 = "TAG_1";
    private static final String TAG_2 = "TAG_2";
    private static final String TAG_3 = "TAG_3";
    private static final String TAG_4 = "TAG_4";

    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;

    private int checkedId = R.id.rb_1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 去掉toolbar阴影效果
        if (getSupportActionBar() != null) getSupportActionBar().setElevation(0);

        RadioGroup radioGroup = findViewById(R.id.radio_group);

        if (savedInstanceState != null) {
            checkedId = savedInstanceState.getInt("checkedId", R.id.rb_1);
            fragment1 = (Fragment1) getSupportFragmentManager().findFragmentByTag(TAG_1);
            fragment2 = (Fragment2) getSupportFragmentManager().findFragmentByTag(TAG_2);
            fragment3 = (Fragment3) getSupportFragmentManager().findFragmentByTag(TAG_3);
            fragment4 = (Fragment4) getSupportFragmentManager().findFragmentByTag(TAG_4);
        }

        // 设置RadioGroup的监听
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        // 设置默认选中常用框架
        radioGroup.check(checkedId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前选中项
        outState.putInt("checkedId", checkedId);
    }

    private final class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        // 上次切换的Fragment
        private Fragment previousFragment;
        private String currentTag;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 得到对应的fragment
            Fragment to = getFragment(checkedId);
            // 替换
            switchFragment(previousFragment, to);
        }

        private Fragment getFragment(int checkedId) {
            switch (checkedId) {
                case R.id.rb_1:
                    currentTag = TAG_1;
                    return fragment1 == null ? fragment1 = Fragment1.newInstance() : fragment1;
                case R.id.rb_2:
                    currentTag = TAG_2;
                    return fragment2 == null ? fragment2 = Fragment2.newInstance() : fragment2;
                case R.id.rb_3:
                    currentTag = TAG_3;
                    return fragment3 == null ? fragment3 = Fragment3.newInstance() : fragment3;
                case R.id.rb_4:
                    currentTag = TAG_4;
                    return fragment4 == null ? fragment4 = Fragment4.newInstance() : fragment4;
            }
            return null;
        }

        private void switchFragment(Fragment from, Fragment to) {
            if (to != null && from != to) {// from != to 才切换
                previousFragment = to;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                // from隐藏
                if (from != null) ft.hide(from);

                if (!to.isAdded()) {
                    // 没有被添加,添加to
                    ft.add(R.id.fl_content, to, currentTag).commit();
                } else {
                    // 已经被添加,显示to
                    ft.show(to).commit();
                }
            }
        }
    }
}