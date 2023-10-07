package com.fanji.android.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMessageManagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MessageManagerFragment : BaseFragment<FragmentMessageManagerBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentMessageManagerBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageViewPager.adapter =
            binding.messageViewPager.create(childFragmentManager)
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
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(activity, binding.messageTab, binding.messageViewPager, true)
    }
}