package com.fanji.android.cloud.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentTransferManagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class TransferManagerFragment : BaseFragment<FragmentTransferManagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentTransferManagerBinding.inflate(layoutInflater))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.transferManagerViewPager.adapter =
            binding.transferManagerViewPager.create(childFragmentManager)
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
                    binding.transferManagerTab,
                    binding.transferManagerViewPager,
                    true
                )
    }
}