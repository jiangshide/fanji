package com.fanji.android.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class FeedFragment(id: Int) : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = initView(
        CommonRecyclerviewBinding.inflate(layoutInflater)
    )

    private var feedAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedAdapter =
            binding.recyclerView.create(R.layout.fragment_feed_item, {
                val icon = findViewById<FJCircleImg>(R.id.icon)
                val name = findViewById<TextView>(R.id.name)
                val top = findViewById<TextView>(R.id.top)
                val fans = findViewById<TextView>(R.id.fans)
                val time = findViewById<TextView>(R.id.time)
                val follow = findViewById<TextView>(R.id.follow)
                val title = findViewById<TextView>(R.id.title)
                val contents = findViewById<TextView>(R.id.contents)
                val collect = findViewById<TextView>(R.id.collect)
                val comment = findViewById<TextView>(R.id.comment)
                val like = findViewById<TextView>(R.id.like)
                name.text = it.name
            }, {})
        val feeds = ArrayList<Feed>()
        for (i in 1..40) {
            val feed = Feed()
            feed.name = "梵山科技$i"
            feeds.add(feed)
        }
        feedAdapter?.add(feeds)
    }
}