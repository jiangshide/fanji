package com.fanji.android.circle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentCircleBinding
import com.fanji.android.feed.FeedFragment
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class CircleFragment(circleId: Int) : BaseFragment<FragmentCircleBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCircleBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.circleSearch.setOnClickListener {

        }
        binding.circleJoin.setOnClickListener {

        }
        binding.circleViewPager.adapter =
            binding.circleViewPager.create(childFragmentManager)
                .setTitles(
                    "动态", "问答"
                )
                .setFragment(
                    FeedFragment(0),
                    FeedFragment(1)
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(activity, binding.circleTab, binding.circleViewPager)
    }
}