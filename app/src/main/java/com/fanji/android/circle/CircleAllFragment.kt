package com.fanji.android.circle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentCircleAllBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class CircleAllFragment : BaseFragment<FragmentCircleAllBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCircleAllBinding.inflate(layoutInflater), title = "全部圈子")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.circleSearch.setOnClickListener {

        }
        binding.circleCreate.setOnClickListener {

        }
        binding.circleAllViewPager.adapter =
            binding.circleAllViewPager.create(childFragmentManager)
                .setTitles(
                    "全部", "我创建的", "我加入的"
                )
                .setFragment(
                    CircleListFragment(0),
                    CircleListFragment(1),
                    CircleListFragment(2)
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .initTabs(activity, binding.circleAllTab, binding.circleAllViewPager)
    }
}