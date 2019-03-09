package com.codearms.maoqiqi.activityfragment.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.codearms.maoqiqi.activityfragment.R;

/**
 * Activity生命周期
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/1/6 18:45
 */
public class LifecycleActivity extends BaseActivity {

    private static final String NUMBER = "com.codearms.maoqiqi.activityfragment.NUMBER";

    private int number;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        number = getIntent().getIntExtra(NUMBER, 1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.lifecycle_label);

        TextView tvPagerNumber = findViewById(R.id.tv_pager_number);
        tvPagerNumber.setText(getString(R.string.lifecycle_page, number));
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_lifecycle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.new_activity:
                Intent intent = new Intent(this, LifecycleActivity.class);
                intent.putExtra(NUMBER, number + 1);
                startActivity(intent);
                break;
            case R.id.dialog_activity:
                startActivity(new Intent(this, DialogActivity.class));
                break;
            case R.id.new_dialog:
                showDialog();
                break;
        }
        return true;
    }

    private void showDialog() {
        dialog = new Dialog(this, R.style.DialogTheme);
        dialog.setContentView(R.layout.activity_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }
}