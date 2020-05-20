package com.codearms.maoqiqi.radiogroupviewpagerfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 主页
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/2/15 16:00
 */
public class MainActivity extends BaseActivity {

    private int[] rbIds = {R.id.rb_1, R.id.rb_2, R.id.rb_3, R.id.rb_4};

    private ViewPager viewPager;
    private RadioGroup radioGroup;

    // 选中Fragment对应的位置
    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().setElevation(0);

        viewPager = findViewById(R.id.view_pager);
        radioGroup = findViewById(R.id.radio_group);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", 0);
        }

        // 设置ViewPager
        // viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setAdapter(new MyFragmentStatePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(rbIds.length - 1);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        // 设置默认选中常用框架
        radioGroup.check(rbIds[position]);
        // 设置RadioGroup的监听
        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
    }

    // 得到对应的Fragment
    private Fragment getFragment(int position) {
        switch (position) {
            case 0:
                return Fragment1.newInstance();
            case 1:
                return Fragment2.newInstance();
            case 2:
                return Fragment3.newInstance();
            case 3:
                return Fragment4.newInstance();
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前选中索引
        outState.putInt("position", position);
    }

    private final class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // FragmentPagerAdapter内部已经做了缓存
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.d("Fragment" + (position + 1), "getItem(int position), position = " + position);
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return rbIds.length;
        }

        // 不用重写,这里是需要打印日志。
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object o = super.instantiateItem(container, position);
            Log.d("Fragment" + (position + 1), "instantiateItem()->Fragment->hashCode():" + o.hashCode());
            return o;
        }
    }

    // 演示FragmentPagerAdapter和FragmentStatePagerAdapter的区别
    private final class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        MyFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.d("Fragment" + (position + 1), "getItem(int position), position = " + position);
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return rbIds.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object o = super.instantiateItem(container, position);
            Log.d("Fragment" + (position + 1), "instantiateItem()->Fragment->hashCode():" + o.hashCode());
            return o;
        }
    }

    private final class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d("info", "onPageSelected(),position=" + position);
            // RadioGroup的OnCheckedChangeListener里的onCheckedChanged(RadioGroup group, int checkedId)会被调用三次
            // radioGroup.check(rbIds[position]);
            // 只调用一次onCheckedChanged(RadioGroup group, int checkedId)
            ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private final class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_1:
                    position = 0;
                    break;
                case R.id.rb_2:
                    position = 1;
                    break;
                case R.id.rb_3:
                    position = 2;
                    break;
                case R.id.rb_4:
                    position = 3;
                    break;
            }
            Log.d("info", "onCheckedChanged(),checkedId=" + checkedId + ",position=" + position);
            viewPager.setCurrentItem(position);
        }
    }
}