package com.fanji.android.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.channel.fragment.ChannelTypeFragment
import com.fanji.android.databinding.CommonViewpagerBinding
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class ChannelFragment : BaseFragment<CommonViewpagerBinding>() {

    private var channel: ChannelVM? = create(ChannelVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonViewpagerBinding.inflate(layoutInflater), isTips = true, isTopPadding = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channel?.channelTypes?.observe(viewLifecycleOwner) {
            finishData(isFinishTips = true)
            if (it.msg != null) {
                tips(it.code)
                return@observe
            }
            showView(it.data)
        }
        channel!!.channelTypes(-1).loading(tipsView)
    }

    private fun showView(data: ArrayList<ChannelType>?) {
        var list = ArrayList<String>()
        var fragmens = ArrayList<BaseFragment<*>>()
        data?.forEach {
            list.add(it.name)
            fragmens.add(ChannelTypeFragment(it.id, channel))
        }
        binding.viewPager.adapter = binding.viewPager.create(childFragmentManager)
            .setTitles(
                list
            )
            .setFragment(
                fragmens
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setTxtSelectedColor(com.fanji.android.ui.R.color.white)
            .initTabs(activity, binding.tab, binding.viewPager)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        channel!!.channelTypes(-1).loading(tipsView)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        channel!!.channelTypes(-1)
    }
}