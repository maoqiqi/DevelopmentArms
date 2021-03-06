package com.codearms.maoqiqi.app.statistics

import android.os.Bundle
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseActivity
import com.codearms.maoqiqi.app.utils.setToolbar

/**
 * Displays tasks statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:14
 */
class StatisticsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        setToolbar(R.id.toolbar) {
            setTitle(R.string.statistics)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}