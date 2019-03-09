package com.codearms.maoqiqi.activityfragment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.fragment.ArticlesFragment;
import com.codearms.maoqiqi.activityfragment.fragment.DetailsFragment;

/**
 * 文章列表适配宽屏
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/15 10:20
 */
public class MainActivity extends BaseActivity implements ArticlesFragment.OnArticleSelectedListener {

    private boolean dualPane;
    private int currentPosition;

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ArticlesFragment) {
            ArticlesFragment articlesFragment = (ArticlesFragment) fragment;
            articlesFragment.setArticleSelectedListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            // 恢复选中的position
            currentPosition = savedInstanceState.getInt("currentPosition", 0);
            Log.e("info", "onCreate()--currentPosition:" + currentPosition);
        }

        // 检查是否有一个details视图在我们的UI中
        FrameLayout detailsFrame = findViewById(R.id.details);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dualPane) onArticleSelected(currentPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存选中的position
        outState.putInt("currentPosition", currentPosition);
        Log.e("info", "onSaveInstanceState()--currentPosition:" + currentPosition);
    }

    // 显示所选文章详情,PORTRAIT布局启动一个新的页面显示文章详情,LANDSCAPE在当前UI中显示一个显示文章详情片段
    @Override
    public void onArticleSelected(int position) {
        currentPosition = position;
        Log.e("info", "currentPosition:" + currentPosition);

        if (dualPane) {
            ArticlesFragment articles = (ArticlesFragment) getSupportFragmentManager().findFragmentById(R.id.articles);
            if (articles != null) {
                articles.setChoiceModeSingle();
                articles.setItemChecked(currentPosition);
            }

            DetailsFragment details = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownPosition() != position) {
                details = DetailsFragment.newInstance(position);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.details, details)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
            Log.e("info", "details.getShownPosition():" + details.getShownPosition());
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }
}