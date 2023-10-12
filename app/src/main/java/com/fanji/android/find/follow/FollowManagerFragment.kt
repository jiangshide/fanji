package com.fanji.android.find.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentFollowManagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class FollowManagerFragment : BaseFragment<FragmentFollowManagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentFollowManagerBinding.inflate(layoutInflater),
        isTitle = true,
        title = "关注"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.personalViewPager.adapter = binding.personalViewPager.create(childFragmentManager)
            .setTitles(
                "已关注", "新推荐"
            )
            .setFragment(
                FollowedFragment(0),
                FollowedFragment(1)
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(activity, binding.personalTab, binding.personalViewPager, true)
    }
}