package com.codearms.maoqiqi.app.statistics;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.ViewModelFactory;
import com.codearms.maoqiqi.app.base.BaseActivity;

/**
 * Displays tasks statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class StatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.statistics);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StatisticsFragment statisticsFragment = (StatisticsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (statisticsFragment == null) {
            statisticsFragment = StatisticsFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.contentFrame, statisticsFragment).commit();
        }

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        StatisticsViewModel statisticsViewModel = ViewModelProviders.of(this, factory).get(StatisticsViewModel.class);

        statisticsFragment.setViewModel(statisticsViewModel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}