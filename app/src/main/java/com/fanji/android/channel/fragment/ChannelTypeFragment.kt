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
import com.fanji.android.net.HTTP_OK
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.base.BaseVM
import com.fanji.android.resource.vm.channel.ChannelVM
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @Author:jiangshide
 * @Date:8/30/23
 * @Email:18311271399@163.com
 * @Description:
 */
class ChannelTypeFragment(private val typeId: Int, var channel: ChannelVM? = null) :
    BaseFragment<FragmentChannelTypeBinding>(), BaseVM.VMListener<MutableList<ChannelBlog>> {

    private var adapter: KAdapter<ChannelBlog>? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChannelTypeBinding.inflate(layoutInflater)


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

        channel?.channelType(id = typeId, listener = this)
        loading()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        channel?.channelType(id = typeId, listener = this)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        channel?.channelType(id = typeId, isRefresh = false, page = page, listener = this)
    }

    override fun onRes(res: LiveResult<MutableList<ChannelBlog>>) {
        page = res.page
        if (res.code == HTTP_OK) {
            adapter?.add(res.data?.toMutableList(), res.isRefresh)
            hiddenTips()
        } else if (adapter == null || adapter?.count() == 0) {
            tips()
        }
        refreshFinish(res.isRefresh)
    }
}