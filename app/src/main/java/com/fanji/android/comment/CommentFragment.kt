package com.fanji.android.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentRecyclerviewBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @author: jiangshide
 * @date: 2023/10/12
 * @email: 18311271399@163.com
 * @description:
 */
class CommentFragment : BaseFragment<FragmentRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentRecyclerviewBinding.inflate(layoutInflater),
        isTopPadding = false,
        isMore = true,
        isRefresh = true
    )

    private val feedVM: FeedVM = create(FeedVM::class.java)
    private var adapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = binding.recyclerView.create(R.layout.fragment_feed_detail_comment, {
            val icon = findViewById<FJCircleImg>(R.id.icon)
            val nick = findViewById<TextView>(R.id.nick)
            val vip = findViewById<TextView>(R.id.vip)
            val content = findViewById<TextView>(R.id.content)
            val time = findViewById<TextView>(R.id.time)
            val reply = findViewById<TextView>(R.id.reply)
            nick.text = it.name
        }, {})

        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
//            if (it.msg != null) {
//                tips(code = it.code)
//            }
//            tips(it.code)
            test(it.isRefresh)
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    private fun test(isRefresh: Boolean) {
        val feeds = ArrayList<Feed>()
        for (i in 1..10) {
            val feed = Feed()
            feed.name = "梵山科技$i"
            feeds.add(feed)
        }
        adapter?.add(feeds, isRefresh)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.recommendBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feedVM!!.recommendBlog(isRefresh = false)
    }
}