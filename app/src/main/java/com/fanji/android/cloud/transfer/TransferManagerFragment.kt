package com.fanji.android.cloud.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.CommonViewpagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class TransferManagerFragment : BaseFragment<CommonViewpagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonViewpagerBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter =
            binding.viewPager.create(childFragmentManager)
                .setTitles(
                    "上传列表", "下载列表"
                )
                .setFragment(
                    TransferFragment(0),
                    TransferFragment(1)
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(
                    activity,
                    binding.tab,
                    binding.viewPager,
                    true
                )
    }
}