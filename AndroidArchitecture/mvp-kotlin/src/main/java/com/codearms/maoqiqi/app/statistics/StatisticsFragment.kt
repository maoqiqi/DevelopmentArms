package com.codearms.maoqiqi.app.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class StatisticsFragment : BaseFragment(), StatisticsContract.View {

    override lateinit var presenter: StatisticsContract.Presenter
    override val isActive: Boolean
        get() = isAdded

    private lateinit var tvStatistics: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_statistics, container, false)
        tvStatistics = root.findViewById(R.id.tvStatistics)
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun showStatistics(numberOfActiveTasks: Int, numberOfCompletedTasks: Int) {
        tvStatistics.text = getString(R.string.statistics_info, numberOfActiveTasks, numberOfCompletedTasks)
    }

    override fun showMessage(message: String) {
        view?.show(MessageMap.get(message))
    }

    companion object {
        fun newInstance(): StatisticsFragment = StatisticsFragment()
    }
}