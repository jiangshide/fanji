package com.fanji.android.mine.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @author: jiangshide
 * @date: 2023/10/11
 * @email: 18311271399@163.com
 * @description:
 */
class OpusFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = initView(
        CommonRecyclerviewBinding.inflate(layoutInflater),
        isRefresh = true,
        isMore = true,
        isTopPadding = false
    )

    private val feedVM: FeedVM = create(FeedVM::class.java)
    private var adapter: KAdapter<Feed>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = binding.recyclerView.create(R.layout.fragment_opus_item, {
            val icon = findViewById<FJCircleImg>(R.id.icon)
            val nick = findViewById<TextView>(R.id.nick)
            val time = findViewById<TextView>(R.id.time)
            val title = findViewById<TextView>(R.id.title)
            val content = findViewById<TextView>(R.id.content)
            val forward = findViewById<TextView>(R.id.forward)
            val comment = findViewById<TextView>(R.id.comment)
            val like = findViewById<TextView>(R.id.like)
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
//        FJFiles.openFile(requireContext(), IMG, this)
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