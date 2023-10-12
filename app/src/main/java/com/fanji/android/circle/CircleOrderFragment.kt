package com.fanji.android.circle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentCircleOrderBinding
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.adapter.AbstractAdapter
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class CircleOrderFragment : BaseFragment<FragmentCircleOrderBinding>(),
    AbstractAdapter.OnMoveListener<ChannelType> {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCircleOrderBinding.inflate(layoutInflater), leftBtn = "", title = "编辑圈子顺序", rightBtn = "取消")

    private var circleAdapter: KAdapter<ChannelType>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        circleAdapter = binding.orderRecyclerView.create(
            R.layout.fragment_circle_order_item,
            {
                val circleName = findViewById<TextView>(R.id.circleName)
                circleName.text = it.name
            },
            {}, manager = GridLayoutManager(requireActivity(), 4)
        )
        circleAdapter?.drag(binding.orderRecyclerView, this)

        val list = ArrayList<ChannelType>()
        for (i in 1..12) {
            val channelType = ChannelType(i, "梵记$i", "", 1, 1, "")
            list.add(channelType)
        }
        circleAdapter?.add(list)
    }

    override fun move(fromPosition: Int, toPosition: Int) {
        LogUtil.e("-----jsd----", "-----fromPosition:", fromPosition, " | toPosition:", toPosition)
    }
}