package com.fanji.android.find.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJButton
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
class FollowedFragment(val type: Int, val title: String = "我的关注") :
    BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        CommonRecyclerviewBinding.inflate(layoutInflater),
        title = title,
        isRefresh = true,
        isMore = true
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var personalAdapter: KAdapter<User>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personalAdapter = binding.recyclerView.create(
            R.layout.fragment_followed_item,
            {
                val icon = findViewById<FJCircleImg>(R.id.personalIcon)
                icon.load(Resource.getUrl())
                val name = findViewById<TextView>(R.id.personalName)
                val vip = findViewById<ImageView>(R.id.vip)
                val fans = findViewById<TextView>(R.id.personalFans)
                val followed = findViewById<FJButton>(R.id.personalFollowed)
                followed.setNormalColor(
                    com.fanji.android.ui.R.color.neutralLight,
                    com.fanji.android.ui.R.color.neutralLight
                )
                if (it.vip == 1) {
                    followed.text = "已关注"
                    followed.setNormalColor(
                        com.fanji.android.ui.R.color.neutralLight,
                        com.fanji.android.ui.R.color.neutralLight
                    )
                }
                if (it.vip == 2) {
                    followed.text = "+关注"
                    followed.setNormalColor(
                        com.fanji.android.ui.R.color.themeBlueStartColor,
                        com.fanji.android.ui.R.color.themeBlueEndColor
                    )
                }
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
            if (i % 2 == 0) {
                user.vip = 1
            } else {
                user.vip = 2
            }
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