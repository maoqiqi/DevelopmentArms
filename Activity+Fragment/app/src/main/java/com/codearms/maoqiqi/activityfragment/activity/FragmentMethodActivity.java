package com.codearms.maoqiqi.activityfragment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codearms.maoqiqi.activityfragment.R;
import com.codearms.maoqiqi.activityfragment.fragment.DynamicFragment1;
import com.codearms.maoqiqi.activityfragment.fragment.DynamicFragment2;

/**
 * 执行片段事务生命周期变化
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/10 11:45
 */
public class FragmentMethodActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvMessage;
    private String str;

    private static final String TAG_1 = "TAG_1";
    private static final String TAG_2 = "TAG_2";

    private DynamicFragment1 fragment1;
    private DynamicFragment2 fragment2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_method);

        fragment1 = (DynamicFragment1) getSupportFragmentManager().findFragmentByTag(TAG_1);
        if (fragment1 == null) {
            Log.e("info", "创建DynamicFragment1");
            fragment1 = new DynamicFragment1();
        } else {
            Log.e("info", "复用DynamicFragment1");
        }

        fragment2 = (DynamicFragment2) getSupportFragmentManager().findFragmentByTag(TAG_2);
        if (fragment2 == null) {
            Log.e("info", "创建DynamicFragment2");
            fragment2 = new DynamicFragment2();
        } else {
            Log.e("info", "复用DynamicFragment2");
        }

        findViewById(R.id.btnAdd).setOnClickListener(this);
        findViewById(R.id.btnRemove).setOnClickListener(this);
        findViewById(R.id.btnAttach).setOnClickListener(this);
        findViewById(R.id.btnDetach).setOnClickListener(this);
        findViewById(R.id.btnShow).setOnClickListener(this);
        findViewById(R.id.btnHide).setOnClickListener(this);
        findViewById(R.id.btnReplace).setOnClickListener(this);

        tvMessage = findViewById(R.id.tvMessage);
        if (savedInstanceState != null) {
            str = savedInstanceState.getString("str");
            tvMessage.setText(str);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                str = tvMessage.getText() + "-->Add";
                tvMessage.setText(str);
                Log.d("info", "fragment1.isAdded() = " + fragment1.isAdded());
                if (!fragment1.isAdded())
                    getSupportFragmentManager().beginTransaction().add(R.id.fl_method_1, fragment1, TAG_1).commit();
                Log.d("info", "fragment2.isAdded() = " + fragment2.isAdded());
                if (!fragment2.isAdded())
                    getSupportFragmentManager().beginTransaction().add(R.id.fl_method_2, fragment2, TAG_2).commit();
                break;
            case R.id.btnRemove:
                str = tvMessage.getText() + "-->Remove";
                tvMessage.setText(str);
                getSupportFragmentManager().beginTransaction().remove(fragment1).commit();
                getSupportFragmentManager().beginTransaction().remove(fragment2).commit();
                break;
            case R.id.btnAttach:
                str = tvMessage.getText() + "-->Attach";
                tvMessage.setText(str);
                getSupportFragmentManager().beginTransaction().attach(fragment1).commit();
                getSupportFragmentManager().beginTransaction().attach(fragment2).commit();
                break;
            case R.id.btnDetach:
                str = tvMessage.getText() + "-->Detach";
                tvMessage.setText(str);
                getSupportFragmentManager().beginTransaction().detach(fragment1).commit();
                getSupportFragmentManager().beginTransaction().detach(fragment2).commit();
                break;
            case R.id.btnShow:
                str = tvMessage.getText() + "-->Show";
                tvMessage.setText(str);
                getSupportFragmentManager().beginTransaction().show(fragment1).commit();
                getSupportFragmentManager().beginTransaction().show(fragment2).commit();
                break;
            case R.id.btnHide:
                str = tvMessage.getText() + "-->Hide";
                tvMessage.setText(str);
                getSupportFragmentManager().beginTransaction().hide(fragment1).commit();
                getSupportFragmentManager().beginTransaction().hide(fragment2).commit();
                break;
            case R.id.btnReplace:
                str = tvMessage.getText() + "-->Replace";
                tvMessage.setText(str);
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_method_1, new DynamicFragment1(), TAG_1).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_method_2, new DynamicFragment2(), TAG_2).commit();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("str", str);
        super.onSaveInstanceState(outState);
    }
}