package com.fanji.android.cloud

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.cloud.transfer.TransferManagerFragment
import com.fanji.android.databinding.FragmentCloudManagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class CloudManagerFragment : BaseFragment<FragmentCloudManagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCloudManagerBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cloudManagerTransfer.setOnClickListener {
            push(TransferManagerFragment())
        }
        binding.cloudManagerSet.setOnClickListener {

        }
        binding.cloudSearch.setOnClickListener {

        }
        binding.cloudManagerViewPager.adapter =
            binding.cloudManagerViewPager.create(childFragmentManager)
                .setTitles(
                    "智能", "文件"
                )
                .setFragment(
                    CloudMindFragment(),
                    CloudFileFragment()
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(activity, binding.cloudManagerTab, binding.cloudManagerViewPager)
    }
}