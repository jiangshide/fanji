package com.fanji.android.resource.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fanji.android.net.HTTP_OK
import com.fanji.android.resource.R
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.FJTabLayout
import com.fanji.android.ui.FJViewPager
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * created by jiangshide on 5/5/21.
 * email:18311271399@163.com
 */
class SearchChannelManagerFragment(private val listener: OnChannelListener? = null) :
    BaseFragment() {

    var channel: ChannelVM? = null
    private lateinit var publishSearchChannelListEdit: FJEditText
    private lateinit var publishSearchChannelListCancel: FJButton
    private lateinit var publishChannelListViewPager: FJViewPager
    private lateinit var tabsPublishChannelList: FJTabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        channel = ViewModelProvider.NewInstanceFactory.instance.create(ChannelVM::class.java)
        return view(R.layout.search_channel_manager_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publishSearchChannelListEdit = view.findViewById(R.id.publishSearchChannelListEdit)
        publishSearchChannelListCancel = view.findViewById(R.id.publishSearchChannelListCancel)
        publishChannelListViewPager = view.findViewById(R.id.publishChannelListViewPager)
        tabsPublishChannelList = view.findViewById(R.id.tabsPublishChannelList)

        publishSearchChannelListEdit.setListener { s, input ->

        }
        publishSearchChannelListCancel.setOnClickListener {
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
        publishChannelListViewPager.adapter =
            publishChannelListViewPager.create(childFragmentManager)
                .setTitles(
                    list
                )
                .setFragment(
                    fragmens
                )
                .initTabs(requireActivity(), tabsPublishChannelList, publishChannelListViewPager)
                .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
                .setLinePagerIndicator(color(com.fanji.android.ui.R.color.blue))
    }
}

interface OnChannelListener {
    fun onChannel(channelBlog: ChannelBlog)
}