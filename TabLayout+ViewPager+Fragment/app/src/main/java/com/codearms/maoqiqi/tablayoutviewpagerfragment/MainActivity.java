package com.codearms.maoqiqi.tablayoutviewpagerfragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

/**
 * 主页
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/2/23 16:58
 */
public class MainActivity extends BaseActivity {

    static String[] titles = {"推荐", "热点", "社会", "娱乐", "情感", "故事", "小说", "星座", "科技",
            "财经", "体育", "军事", "教育", "历史", "搞笑", "奇闻", "游戏", "时尚", "养生", "美食", "旅行"};

    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().setElevation(0);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position", 0);
        }

        // 设置ViewPager
        // viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setAdapter(new MyFragmentStatePagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(position);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
    }

    private final class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // FragmentPagerAdapter内部已经做了缓存
        @Override
        public Fragment getItem(int position) {
            Log.d("MyFragment" + (position + 1), "getItem(int position), position = " + position);
            return MyFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        // 不用重写,这里是需要打印日志。
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object o = super.instantiateItem(container, position);
            Log.d("MyFragment" + (position + 1), "instantiateItem()->Fragment->hashCode():" + o.hashCode());
            return o;
        }
    }

    private final class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

        MyFragmentStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("MyFragment" + (position + 1), "getItem(int position), position = " + position);
            return MyFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object o = super.instantiateItem(container, position);
            Log.d("MyFragment" + (position + 1), "instantiateItem()->Fragment->hashCode():" + o.hashCode());
            return o;
        }
    }
}