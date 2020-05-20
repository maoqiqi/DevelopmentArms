package com.codearms.maoqiqi.activityfragment.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codearms.maoqiqi.activityfragment.R;

/**
 * 显示文章详情
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/15 11:47
 */
public class DetailsFragment extends BaseFragment {

    public static DetailsFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tvDetails = getView().findViewById(R.id.tv_details);

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            str.append("我是文章列表").append(getShownPosition()).append("\n");
        }

        tvDetails.setText(str.toString());
    }

    public int getShownPosition() {
        return getArguments().getInt("position", 0);
    }
}