package com.fanji.android.find.answers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentJoinDetailBinding
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.FJDialog
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @author: jiangshide
 * @date: 2023/10/21
 * @email: 18311271399@163.com
 * @description:
 */
class JoinDetailFragment : BaseFragment<FragmentJoinDetailBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentJoinDetailBinding.inflate(layoutInflater),
        title = "参与详情",
        isRefresh = true,
        isMore = true
    )

    private val feedVM: FeedVM = create(FeedVM::class.java)
    private var adapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = binding.recyclerView.create(R.layout.fragment_join_detail_item, {
            val icon = findViewById<FJCircleImg>(R.id.icon)
            icon.load(Resource.getUrl())
            val name = findViewById<TextView>(R.id.name)
            val time = findViewById<TextView>(R.id.time)
            val bidding = findViewById<ImageView>(R.id.bidding)
            val checkDetail = findViewById<FJButton>(R.id.checkDetail)
            if (it.online == 1) {
                bidding.visibility = View.GONE
            } else {
                bidding.visibility = View.VISIBLE
            }
            checkDetail.setOnClickListener {
                FJDialog.create(requireContext()).setContent("支付1梵币进行查看").setLeftTxt("确定")
                    .setRightTxt("取消").setListener { isCancel, editMessage ->
                        checkDetail.text = "查看"
                    }.show()
            }
            if (checkDetail.text == "查看") {
//                push()
            }
            name.text = it.name
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
            if (i % 2 == 0) {
                feed.online = 1
            } else {
                feed.online = 2
            }
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