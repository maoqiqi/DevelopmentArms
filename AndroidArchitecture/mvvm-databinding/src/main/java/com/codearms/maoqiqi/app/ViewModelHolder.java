package com.codearms.maoqiqi.app;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Non-UI Fragment used to retain ViewModels.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/18 11:42
 */
public class ViewModelHolder<VM> extends Fragment {

    private VM viewModel;

    public ViewModelHolder() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static <VM> ViewModelHolder createContainer(VM viewModel) {
        ViewModelHolder<VM> viewModelContainer = new ViewModelHolder<>();
        viewModelContainer.setViewModel(viewModel);
        return viewModelContainer;
    }

    public VM getViewModel() {
        return viewModel;
    }

    public void setViewModel(VM viewModel) {
        this.viewModel = viewModel;
    }
}