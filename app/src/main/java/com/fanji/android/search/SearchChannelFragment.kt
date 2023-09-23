package com.fanji.android.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSearchChannelBinding
import com.fanji.android.img.FJImg
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.ui.vm.FJVM

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class SearchChannelFragment(
    private val fromId: Int = 0,
    private val listener: OnChannelListener? = null
) : BaseFragment<FragmentSearchChannelBinding>(), FJVM.VMListener<MutableList<ChannelBlog>> {

    var channel: ChannelVM? = create(ChannelVM::class.java)
    private var adapter: KAdapter<ChannelBlog>? = null
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSearchChannelBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channel!!.channelUser.observe(viewLifecycleOwner, Observer {
            finishData(true, true, true)
            if (it.code == HTTP_OK) {
                finishTips()
                showView(it.data!!)
            } else if (adapter == null || adapter?.count() == 0) {
                tips()
            }
        })

        request(isLoading = true, isRefresh = true)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        request(isLoading = true, isRefresh = true)
    }

    private fun request(
        isLoading: Boolean,
        isRefresh: Boolean = true
    ) {
        if (fromId > 0) {
            channel?.channelType(id = fromId, isRefresh = isRefresh, listener = this)
        } else {
            channel?.channelUser()
        }
        if (isLoading) {
            loading()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        request(isLoading = false, isRefresh = true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        request(isLoading = false, isRefresh = false)
    }

    private fun showView(data: MutableList<ChannelBlog>) {
        adapter =
            binding.publishChannelListRecycleView.create(data,
                R.layout.search_channel_fragment_item,
                {
                    val channelListItemIcon: FJImageView =
                        this.findViewById(R.id.channelListItemIcon)
                    val channelListItemName: TextView = this.findViewById(R.id.channelListItemName)
                    val channelListItemDes: TextView = this.findViewById(R.id.channelListItemDes)
                    FJImg.loadImageRound(
                        it.cover,
                        channelListItemIcon,
                        5,
                        com.fanji.android.resource.R.mipmap.splash
                    )
                    channelListItemName.text = it.name
////                    channelListItemType.text =
////                        if (it.natureId == CHANNEL_PRIVATE) getString(R.string.private_channel) else getString(R.string.publish_channel)
                    channelListItemDes.text = it.des
                },
                {
                    if (listener != null) {
                        listener?.onChannel(this)
                    } else {
//                        ZdEvent.get()
//                            .with(CHANNEL)
//                            .post(this)
                    }
                    pop()
                })
    }

    override fun onRes(res: LiveResult<MutableList<ChannelBlog>>) {
        page = res.page
        finishData(true, true, true)
        if (res.code == HTTP_OK) {
            showView(res.data!!)
            finishTips()
        } else if (adapter == null || adapter?.count() == 0) {
            tips()
        }
    }
}