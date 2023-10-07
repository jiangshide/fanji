package com.fanji.android.find.circle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentCircleManagerBinding
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class CircleManagerFragment : BaseFragment<FragmentCircleManagerBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCircleManagerBinding.inflate(layoutInflater), isTopPadding = false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.circleOrder.setOnClickListener {
            push(CircleOrderFragment())
        }
        binding.circleAll.setOnClickListener {
            push(CircleAllFragment())
        }

        val list = ArrayList<ChannelType>()
        for (i in 1..10) {
            val channelType = ChannelType(i, "梵记$i", "梵山科技$i", 1, 1, "")
            list.add(channelType)
        }
        showView(list)
    }

    private fun showView(data: ArrayList<ChannelType>?) {
        var list = ArrayList<String>()
        var fragmens = ArrayList<BaseFragment<*>>()
        data?.forEach {
            list.add(it.name)
            fragmens.add(CircleFragment(it.id))
        }
        binding.circleManagerViewPager.adapter =
            binding.circleManagerViewPager.create(childFragmentManager)
                .setTitles(
                    list
                )
                .setFragment(
                    fragmens
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
                .initTabs(activity, binding.circleManagerTab, binding.circleManagerViewPager)
    }
}