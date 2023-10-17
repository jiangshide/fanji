package com.fanji.android.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPublishManagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.fanji.android.util.LogUtil

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class PublishManagerFragment : BaseFragment<FragmentPublishManagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentPublishManagerBinding.inflate(layoutInflater), title = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.publishManagerExit.setOnClickListener {
            LogUtil.e("---jsd0---", "----it:", it)
            pop()
        }
        binding.publishManagerDraft.setOnClickListener {
            LogUtil.e("---jsd1---", "----it:", it)
        }
        binding.publishManagerViewPager.adapter =
            binding.publishManagerViewPager.create(childFragmentManager)
                .setTitles(
                    "创作", "提问"
                )
                .setFragment(
                    ProductFragment(),
                    QuizFragment()
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(activity, binding.publishManagerTab, binding.publishManagerViewPager)

    }
}