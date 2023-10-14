package com.fanji.android.circle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class CircleListFragment(val type: Int) : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater))

    private var circleListAdapter: KAdapter<ChannelType>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        circleListAdapter = binding.recyclerView.create(
            R.layout.fragment_circle_list_item,
            {
                val circleListIcon = findViewById<FJCircleImg>(R.id.circleListIcon)
                val circleListName = findViewById<TextView>(R.id.circleListName)
                val circleListStatus = findViewById<TextView>(R.id.circleListStatus)
                val circleListMembers = findViewById<TextView>(R.id.circleListMembers)
                val circleListContents = findViewById<TextView>(R.id.circleListContents)
                val circleListBtn = findViewById<TextView>(R.id.circleListBtn)
                circleListName.text = it.name
            },
            {})

        val list = ArrayList<ChannelType>()
        for (i in 1..50) {
            val channelType = ChannelType(i, "梵记$i", "", 1, 1, "")
            list.add(channelType)
        }
        circleListAdapter?.add(list)
    }
}