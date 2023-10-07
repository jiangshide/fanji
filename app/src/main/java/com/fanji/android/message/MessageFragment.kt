package com.fanji.android.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentMessageBinding
import com.fanji.android.resource.vm.feed.data.Feed
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
class MessageFragment(val type: Int) : BaseFragment<FragmentMessageBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentMessageBinding.inflate(layoutInflater))

    private var messageAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageAdapter =
            binding.messageRecyclerView.create(ArrayList(), R.layout.fragment_message_item, {
                val messageDot = findViewById<ImageView>(R.id.messageDot)
                val messageIcon = findViewById<FJCircleImg>(R.id.messageIcon)
                val messageName = findViewById<TextView>(R.id.messageName)
                val messageTime = findViewById<TextView>(R.id.messageTime)
                val messageTitle = findViewById<TextView>(R.id.messageTitle)
                val messageContent = findViewById<TextView>(R.id.messageContent)
            }, {})

        val list = ArrayList<Feed>()
        for (i in 1..50) {
            val feed = Feed()
            feed.name = "梵记$i"
            list.add(feed)
        }
        messageAdapter?.add(list)
    }
}