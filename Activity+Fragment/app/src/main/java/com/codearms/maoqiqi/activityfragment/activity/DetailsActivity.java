package com.codearms.maoqiqi.activityfragment.activity;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.codearms.maoqiqi.activityfragment.fragment.DetailsFragment;

/**
 * 显示文章详情
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/15 11:58
 */
public class DetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            DetailsFragment details = DetailsFragment.newInstance(getIntent().getIntExtra("position", 0));
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}