package com.codearms.maoqiqi.app.addedittask;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codearms.maoqiqi.app.CallBack;
import com.codearms.maoqiqi.app.R;
import com.codearms.maoqiqi.app.base.BaseFragment;
import com.codearms.maoqiqi.app.databinding.FragmentAddEditTaskBinding;
import com.codearms.maoqiqi.app.tasks.TasksActivity;
import com.codearms.maoqiqi.app.utils.MessageMap;
import com.codearms.maoqiqi.app.utils.SnackbarUtils;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
public class AddEditTaskFragment extends BaseFragment implements AddEditTaskListener, CallBack {

    private AddEditTaskViewModel addEditTaskViewModel;

    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    void setViewModel(AddEditTaskViewModel addEditTaskViewModel) {
        this.addEditTaskViewModel = addEditTaskViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        FragmentAddEditTaskBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_edit_task, container, false);
        binding.setAddEditTaskViewModel(addEditTaskViewModel);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        addEditTaskViewModel.start();
    }

    @Override
    public void showTasks() {
        if (getActivity() == null) return;
        getActivity().setResult(TasksActivity.RESULT_ADD_TASK);
        getActivity().finish();
    }

    @Override
    public void showMessage(String message) {
        SnackbarUtils.show(getView(), MessageMap.get(message));
    }
}