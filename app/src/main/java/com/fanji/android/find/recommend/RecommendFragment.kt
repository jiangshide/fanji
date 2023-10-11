package com.fanji.android.find.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentRecommendBinding
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.IMG

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class RecommendFragment : BaseFragment<FragmentRecommendBinding>(), FileListener {

    private var feedVM: FeedVM? = create(FeedVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentRecommendBinding.inflate(layoutInflater),
        isMore = true,
        isTopPadding = false,
        isRefresh = true
    )

    private var recommendAdapter: KAdapter<User>? = null
    private var carefullyChosenAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carefullyChosenAdapter = binding.carefullyChosenRecyclerView.create(
            ArrayList(),
            R.layout.find_follow_item,
            {
                val icon = findViewById<FJCircleImg>(R.id.icon)
                val name = findViewById<TextView>(R.id.name)
                val fans = findViewById<TextView>(R.id.fans)
                val time = findViewById<TextView>(R.id.time)
                val follow = findViewById<TextView>(R.id.follow)
                follow.visibility = View.VISIBLE
                val title = findViewById<TextView>(R.id.title)
                val contents = findViewById<TextView>(R.id.contents)
                val collect = findViewById<TextView>(R.id.collect)
                val comment = findViewById<TextView>(R.id.comment)
                val like = findViewById<TextView>(R.id.like)
                name.text = it.name
            },
            {}, headResId = R.layout.find_follow_item
        )
        val headView = carefullyChosenAdapter?.addHeaderView(
            requireContext(),
            R.layout.fragment_recommend_header
        )
        carefullyChosenAdapter?.adjustSpanSize(binding.carefullyChosenRecyclerView)
        val recommend = headView?.findViewById<TextView>(R.id.recommend)
        val recommendL = headView?.findViewById<LinearLayout>(R.id.recommendL)
        val recommendRecyclerView = headView?.findViewById<RecyclerView>(R.id.recommendRecyclerView)
        recommendL?.setOnClickListener {

        }
        recommendAdapter =
            recommendRecyclerView?.create(ArrayList(), R.layout.find_recommend_item, {
                val recommendIcon = findViewById<FJCircleImg>(R.id.recommendIcon)
                val recommendName = findViewById<TextView>(R.id.recommendName)
                val recommendSign = findViewById<TextView>(R.id.recommendSign)

            }, {}, manager = GridLayoutManager(requireContext(), 3))


        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
//            if (it.msg != null) {
//                tips(code = it.code)
//            }
            tips(it.code)
//            test(it.isRefresh)
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    private fun test(isRefresh: Boolean) {
        val users = ArrayList<User>()
        for (i in 1..10) {
            val user = User()
            user.name = "梵山科技$i"
            users.add(user)
        }
        recommendAdapter?.update(users)

        val feeds = ArrayList<Feed>()
        for (i in 1..10) {
            val feed = Feed()
            feed.name = "梵山科技$i"
            feeds.add(feed)
        }
        carefullyChosenAdapter?.add(feeds, isRefresh)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        FJFiles.openFile(requireContext(), IMG, this)
//        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.recommendBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feedVM!!.recommendBlog(isRefresh = false)
    }

    override fun onFiles(files: List<FileData>) {
        LogUtil.e("----jsd---", "----files:", files)
    }
}