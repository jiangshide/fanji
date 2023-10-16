package com.fanji.android.cloud.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.R
import com.fanji.android.databinding.CommonViewpagerBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/16
 * @email: 18311271399@163.com
 * @description:
 */
class DocManagerFragment : BaseFragment<CommonViewpagerBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = initView(
        CommonViewpagerBinding.inflate(layoutInflater), title = "文档"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arr = getResArr(R.array.docs)
        val list = ArrayList<DocFragment>()
        arr.forEach {
            list.add(DocFragment(it))
        }
        binding.viewPager.adapter = binding.viewPager.create(childFragmentManager)
            .setTitles(arr)
            .setFragment(list)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(activity, binding.tab, binding.viewPager)
    }
}