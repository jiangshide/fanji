package com.fanji.android.find.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentPersonalBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @author: jiangshide
 * @date: 2023/10/2
 * @email: 18311271399@163.com
 * @description:
 */
class PersonalFragment(val type: Int) : BaseFragment<FragmentPersonalBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentPersonalBinding.inflate(layoutInflater),
        title = "我的关注",
        isRefresh = true,
        isMore = true
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var personalAdapter: KAdapter<User>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        personalAdapter = binding.personalRecyclerView.create(
            ArrayList(),
            R.layout.fragment_personal_item,
            {
                val icon = findViewById<FJCircleImg>(R.id.personalIcon)
                val name = findViewById<TextView>(R.id.personalName)
                val identity = findViewById<TextView>(R.id.personalIdentity)
                val fans = findViewById<TextView>(R.id.personalFans)
                name.text = it.name
                fans.text = "${it.fansNum}粉丝"
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