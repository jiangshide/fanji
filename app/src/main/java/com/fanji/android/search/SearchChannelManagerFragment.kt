package com.fanji.android.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fanji.android.databinding.FragmentSearchChannelManagerBinding
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.R
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class SearchChannelManagerFragment(private val listener: OnChannelListener? = null) :
    BaseFragment<FragmentSearchChannelManagerBinding>() {

    var channel: ChannelVM? = create(ChannelVM::class.java)
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchChannelManagerBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.publishSearchChannelListEdit.setListener { s, input ->

        }
        binding.publishSearchChannelListCancel.setOnClickListener {
            pop()
        }

        channel!!.channelTypes.observe(viewLifecycleOwner, Observer {
            refreshFinish(it.isRefresh)
            if (it.code == HTTP_OK) {
                hiddenTips()
                showView(it.data)
            } else {
                tips()
            }

        })
        channel?.channelTypes(-1)
        loading()
    }

    private fun showView(data: ArrayList<ChannelType>?) {
        var list = ArrayList<String>()
        var fragmens = ArrayList<SearchChannelFragment>()
        list.add(getString(R.string.me))
        fragmens.add(SearchChannelFragment(listener = listener))
        data?.forEach {
            list.add(it.name)
            fragmens.add(SearchChannelFragment(it.id))
        }
        binding.publishChannelListViewPager.adapter =
            binding.publishChannelListViewPager.create(childFragmentManager)
                .setTitles(
                    list
                )
                .setFragment(
                    fragmens
                )
                .initTabs(
                    requireActivity(),
                    binding.tabsPublishChannelList,
                    binding.publishChannelListViewPager
                )
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setLinePagerIndicator(color(com.fanji.android.ui.R.color.blue))
    }
}

interface OnChannelListener {
    fun onChannel(channelBlog: ChannelBlog)
}