package com.fanji.android.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.channel.fragment.ChannelTypeFragment
import com.fanji.android.databinding.FragmentChannelBinding
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class ChannelFragment : BaseFragment<FragmentChannelBinding>() {

    private var channel: ChannelVM? = create(ChannelVM::class.java)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChannelBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channel?.channelTypes?.observe(viewLifecycleOwner) {
            if (it.code == HTTP_OK) {
                showView(it.data)
                hiddenTips()
            } else {
                tips()
            }
            refreshFinish(it.isRefresh)
        }
        channel?.channelTypes(-1)
        loading()
    }

    private fun showView(data: ArrayList<ChannelType>?) {
        var list = ArrayList<String>()
        var fragmens = ArrayList<BaseFragment<*>>()
        data?.forEach {
            list.add(it.name)
            fragmens.add(ChannelTypeFragment(it.id, channel))
        }
        binding.channelViewPager.adapter = binding.channelViewPager.create(childFragmentManager)
            .setTitles(
                list
            )
            .setFragment(
                fragmens
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(activity, binding.channelTab, binding.channelViewPager)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        channel?.channelTypes(-1)
        loading()
    }
}