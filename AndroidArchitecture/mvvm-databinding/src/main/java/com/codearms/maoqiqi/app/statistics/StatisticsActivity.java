package com.codearms.maoqiqi.app.statistics;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.codearms.maoqiqi.app.Injection;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.ViewModelHolder;
import com.codearms.maoqiqi.app.base.BaseActivity;

/**
 * Displays tasks statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
public class StatisticsActivity extends BaseActivity {

    public static final String STATISTICS_VIEW_MODEL = "STATISTICS_VIEW_MODEL";

    private StatisticsViewModel statisticsViewModel;

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

        @SuppressWarnings("unchecked")
        ViewModelHolder<StatisticsViewModel> viewModelHolder = (ViewModelHolder<StatisticsViewModel>) getSupportFragmentManager().findFragmentByTag(STATISTICS_VIEW_MODEL);
        if (viewModelHolder != null && viewModelHolder.getViewModel() != null) {
            statisticsViewModel = viewModelHolder.getViewModel();
        } else {
            statisticsViewModel = new StatisticsViewModel(Injection.provideTasksRepository(this));
            getSupportFragmentManager().beginTransaction().add(ViewModelHolder.createContainer(statisticsViewModel), STATISTICS_VIEW_MODEL).commit();
        }
        statisticsViewModel.setCallBack(statisticsFragment);

        statisticsFragment.setViewModel(statisticsViewModel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        statisticsViewModel.destroy();
        super.onDestroy();
    }
}