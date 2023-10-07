package com.fanji.android.find.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.FragmentFollowBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.HORIZONTAL
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FollowFragment : BaseFragment<FragmentFollowBinding>() {

    private var feedVM: FeedVM? = create(FeedVM::class.java)

    private var personalAdapter: KAdapter<User>? = null
    private var followAdapter: KAdapter<Feed>? = null

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentFollowBinding.inflate(layoutInflater), isRefresh = true, isMore = true, isTopPadding = false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        personalAdapter =
            binding.personalTopRecyclerView.create(ArrayList(), R.layout.find_follow_personal, {
                val personalIcon = findViewById<FJCircleImg>(R.id.personalIcon)
                val personalName = findViewById<TextView>(R.id.personalName)
//                personalIcon.load(it.icon)
                personalName.text = it.name
            }, {}, binding.personalTopRecyclerView.HORIZONTAL())
//
        binding.personalAllBtn.setOnClickListener {
            push(PersonalManagerFragment())
        }
        followAdapter =
            binding.followRecycleView.create(ArrayList(), R.layout.find_follow_item, {
                val icon = findViewById<FJCircleImg>(R.id.icon)
                val name = findViewById<TextView>(R.id.name)
                val fans = findViewById<TextView>(R.id.fans)
                val time = findViewById<TextView>(R.id.time)
                val title = findViewById<TextView>(R.id.title)
                val contents = findViewById<TextView>(R.id.contents)
                val collect = findViewById<TextView>(R.id.collect)
                val comment = findViewById<TextView>(R.id.comment)
                val like = findViewById<TextView>(R.id.like)
                name.text = it.name
            }, {})


        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)

//            if (it.msg != null) {
//                tips()
//            }
            test()
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    private fun test() {
        val users = ArrayList<User>()
        for (i in 1..30) {
            val user = User()
            user.name = "梵山科技$i"
            users.add(user)
        }
        personalAdapter?.add(users)

        val feeds = ArrayList<Feed>()
        for (i in 1..40) {
            val feed = Feed()
            feed.name = "梵山科技$i"
            feeds.add(feed)
        }
        followAdapter?.add(feeds)
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