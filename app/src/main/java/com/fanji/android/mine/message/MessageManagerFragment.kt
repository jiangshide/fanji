package com.fanji.android.mine.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.CommonViewpagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MessageManagerFragment : BaseFragment<CommonViewpagerBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonViewpagerBinding.inflate(layoutInflater), isTips = true, title = "消息")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter =
            binding.viewPager.create(childFragmentManager)
                .setTitles(
                    "回复", "通知", "点赞", "私信"
                )
                .setFragment(
                    MessageFragment(0),
                    MessageFragment(1),
                    MessageFragment(2),
                    MessageFragment(3)
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .initTabs(activity, binding.tab, binding.viewPager, true)
    }
}