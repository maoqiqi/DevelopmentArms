package com.codearms.maoqiqi.app.statistics;

import androidx.lifecycle.Observer;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.app.Event;
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
public class StatisticsFragment extends BaseFragment {

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
        binding.setLifecycleOwner(getActivity());
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        statisticsViewModel.getMessage().observe(this, new Observer<Event<String>>() {
            @Override
            public void onChanged(Event<String> stringEvent) {
                String message = stringEvent.getContentIfNotHandled();
                if (message != null) showMessage(message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        statisticsViewModel.start();
    }

    private void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }
}