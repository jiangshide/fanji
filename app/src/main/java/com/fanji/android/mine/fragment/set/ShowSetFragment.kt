package com.fanji.android.mine.fragment.set

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentShowSetBinding
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.util.FJEvent
import com.fanji.android.util.SPUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class ShowSetFragment : BaseFragment<FragmentShowSetBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentShowSetBinding.inflate(layoutInflater), title = "显示设置")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isScroll = SPUtil.getBoolean(TAB_BOTTOM_SCROLL)
        binding.switchBottomMove.isChecked = isScroll
        binding.switchBottomMove.setOnCheckedChangeListener { view, isChecked ->
            FJEvent.get().with(TAB_BOTTOM_SCROLL).post(isChecked)
            SPUtil.putBoolean(TAB_BOTTOM_SCROLL, isChecked)
        }
    }
}

const val TAB_BOTTOM_SCROLL = "tabBottomScroll"