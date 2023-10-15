package com.fanji.android.mine.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.CommonViewpagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class TradeManagerFragment : BaseFragment<CommonViewpagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonViewpagerBinding.inflate(layoutInflater), title = "交易明细")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = binding.viewPager.create(childFragmentManager)
            .setTitles(
                "获得", "消耗", "充值"
            )
            .setFragment(
                TradeDetailFragment(0),
                TradeDetailFragment(1),
                TradeDetailFragment(2)
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(activity, binding.tab, binding.viewPager, true)
    }
}