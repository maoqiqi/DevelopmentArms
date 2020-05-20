package com.codearms.maoqiqi.app.statistics;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.app.CallBack;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.base.BaseFragment;
import com.codearms.maoqiqi.app.databinding.FragmentStatisticsBinding;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.SnackbarUtils;

/**
 * Main UI for the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
public class StatisticsFragment extends BaseFragment implements CallBack {

    private StatisticsViewModel statisticsViewModel;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    public void setViewModel(StatisticsViewModel statisticsViewModel) {
        this.statisticsViewModel = statisticsViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentStatisticsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);
        binding.setStatisticsViewModel(statisticsViewModel);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        statisticsViewModel.start();
    }

    @Override
    public void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }
}