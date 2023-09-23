package com.fanji.android.channel.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentChannelTypeBinding
import com.fanji.android.img.FJImg
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
 * @Date:8/30/23
 * @Email:18311271399@163.com
 * @Description:
 */
class ChannelTypeFragment(private val typeId: Int, var channel: ChannelVM? = null) :
    BaseFragment<FragmentChannelTypeBinding>(), FJVM.VMListener<MutableList<ChannelBlog>> {

    private var adapter: KAdapter<ChannelBlog>? = null

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentChannelTypeBinding.inflate(layoutInflater),
        isRefresh = true,
        isMore = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        adapter = binding.channelRecycleView.create(
            ArrayList(),
            R.layout.channel_fragment_type_item,
            {
                val channelItemImg: FJImageView = this.findViewById(R.id.channelItemImg)
                val channelItemFormat: FJImageView = this.findViewById(R.id.channelItemFormat)
                val channelItemName: TextView = this.findViewById(R.id.channelItemName)
                FJImg.loadImage(it.cover, channelItemImg)
                it?.blog?.setImg(
                    activity = activity,
                    formatImg = channelItemFormat,
                    coverImg = channelItemImg,
                    channelCover = it.cover
                )
                channelItemName.text = it.des
            },
            {},
            layoutManager
        )
        channel!!.channelType(id = typeId, listener = this).loading(tipsView)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        channel!!.channelType(id = typeId, listener = this).loading(tipsView)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        channel?.channelType(id = typeId, listener = this)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        channel?.channelType(id = typeId, isRefresh = false, page = page, listener = this)
    }

    override fun onRes(res: LiveResult<MutableList<ChannelBlog>>) {
        finishData(true, true, true)
        page = res.page
        if (res.msg != null) {
            tips(res.code)
            return
        }
        adapter?.add(res.data?.toMutableList(), res.isRefresh)
    }
}