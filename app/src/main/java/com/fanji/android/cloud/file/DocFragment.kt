package com.fanji.android.cloud.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJButton
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @author: jiangshide
 * @date: 2023/10/16
 * @email: 18311271399@163.com
 * @description:
 */
class DocFragment(val type: String) : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = initView(
        CommonRecyclerviewBinding.inflate(layoutInflater),
        isTopPadding = false,
        isRefresh = true,
        isMore = true
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var personalAdapter: KAdapter<User>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalAdapter = binding.recyclerView.create(
            R.layout.fragment_doc,
            {
                val icon = findViewById<FJImageView>(R.id.icon)
                val name = findViewById<TextView>(R.id.name)
                val date = findViewById<TextView>(R.id.date)
                val jumpDir = findViewById<FJButton>(R.id.jumpDir)
                name.text = it.name
            },
            {})

        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)

//            if (it.msg != null) {
//                tips()
//            }
            test(it.isRefresh)
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    fun test(isRefresh: Boolean) {
        val users = ArrayList<User>()
        for (i in 1..10) {
            val user = User()
            user.name = "梵山科技$i"
            user.fansNum = i
            users.add(user)
        }
        personalAdapter?.add(users, isRefresh)
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