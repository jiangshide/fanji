package com.fanji.android.resource.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fanji.android.img.FJImg
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.resource.R
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.base.BaseVM
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.FJRecycleView
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.widget.adapter.KAdapter
import com.fanji.widget.adapter.create

/**
 * created by jiangshide on 5/5/21.
 * email:18311271399@163.com
 */
class SearchChannelFragment(
    private val fromId: Int = 0,
    private val listener: OnChannelListener? = null
) : BaseFragment(), BaseVM.VMListener<MutableList<ChannelBlog>> {

    var channel: ChannelVM? = null

    private var adapter: KAdapter<ChannelBlog>? = null
    private lateinit var publishChannelListRecycleView: FJRecycleView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        channel = ViewModelProvider.NewInstanceFactory.instance.create(ChannelVM::class.java)
        return view(
            R.layout.search_channel_fragment,
            isRefresh = true,
            isMore = true,
            isTips = true
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publishChannelListRecycleView = view.findViewById(R.id.publishChannelListRecycleView)

        channel!!.channelUser.observe(viewLifecycleOwner, Observer {
            refreshFinish(it.isRefresh)
            if (it.code == HTTP_OK) {
                hiddenTips()
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
            publishChannelListRecycleView.create(data,
                R.layout.search_channel_fragment_item,
                {
                    val channelListItemIcon: FJImageView =
                        this.findViewById(R.id.channelListItemIcon)
                    val channelListItemName: TextView = this.findViewById(R.id.channelListItemName)
                    val channelListItemDes: TextView = this.findViewById(R.id.channelListItemDes)
                    FJImg.loadImageRound(it.cover, channelListItemIcon, 5, R.mipmap.splash)
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
        if (res.code == HTTP_OK) {
            showView(res.data!!)
            hiddenTips()
        } else if (adapter == null || adapter?.count() == 0) {
            tips()
        }
        refreshFinish(res.isRefresh)
    }
}