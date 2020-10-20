package com.codearms.maoqiqi.app.statistics

import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.codearms.maoqiqi.app.R
import com.codearms.maoqiqi.app.base.BaseFragment
import com.codearms.maoqiqi.app.databinding.FragmentStatisticsBinding
import com.codearms.maoqiqi.app.utils.MessageMap
import com.codearms.maoqiqi.app.utils.getViewModelFactory
import com.codearms.maoqiqi.app.utils.show

/**
 * Main UI for the statistics screen.
 * Author: fengqi.mao.march@gmail.com
 * Date: 2019/3/12 13:32
 */
class StatisticsFragment : BaseFragment() {

    private val statisticsViewModel: StatisticsViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = DataBindingUtil.inflate<FragmentStatisticsBinding>(inflater, R.layout.fragment_statistics, container, false)
        binding.statisticsViewModel = statisticsViewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statisticsViewModel.message.observe(viewLifecycleOwner, Observer { stringEvent ->
            val message = stringEvent!!.getContentIfNotHandled()
            if (message != null) showMessage(message)
        })
    }

    override fun onResume() {
        super.onResume()
        statisticsViewModel.start()
    }

    private fun showMessage(message: String?) {
        view?.show(MessageMap.get(message))
    }
}