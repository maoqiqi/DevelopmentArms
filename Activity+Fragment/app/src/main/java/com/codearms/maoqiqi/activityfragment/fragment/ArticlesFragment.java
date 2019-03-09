package com.codearms.maoqiqi.activityfragment.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codearms.maoqiqi.activityfragment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 显示文章列表
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/15 11:00
 */
public class ArticlesFragment extends BaseFragment {

    private OnArticleSelectedListener articleSelectedListener;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_articles, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = getView().findViewById(R.id.list_view);

        final List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("我是文章列表" + i);
        }

        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_activated_1, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (articleSelectedListener != null)
                    articleSelectedListener.onArticleSelected(position);
            }
        });
    }

    // 设置单选模式
    public void setChoiceModeSingle() {
        if (listView != null) listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    // 设置某一项被选中
    public void setItemChecked(int position) {
        if (listView != null) listView.setItemChecked(position, true);
    }

    public interface OnArticleSelectedListener {

        void onArticleSelected(int position);
    }

    public void setArticleSelectedListener(OnArticleSelectedListener articleSelectedListener) {
        this.articleSelectedListener = articleSelectedListener;
    }
}