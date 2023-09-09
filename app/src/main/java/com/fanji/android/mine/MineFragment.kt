package com.fanji.android.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMineBinding
import com.fanji.android.mine.fragment.SetFragment
import com.fanji.android.mine.fragment.UserFragment
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.fanji.android.util.FJEvent

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MineFragment : BaseFragment<FragmentMineBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMineBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mineViewPager.adapter = binding.mineViewPager.create(childFragmentManager)
            .setFragment(
                UserFragment(),
                SetFragment()
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setPersistent(false)
            .initTabs(activity, binding.mineViewPager, true)
        FJEvent.get().with(MENU, Int::class.java).observes(this, {
            binding.mineViewPager?.currentItem = it
        })

    }
}

const val MENU = "menu"