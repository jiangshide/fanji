package com.fanji.android.mine.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMyFeedBinding
import com.fanji.android.resource.Resource
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.refresh.api.RefreshLayout

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MyFeedFragment : BaseFragment<FragmentMyFeedBinding>() {

    private val feedVM: FeedVM = create(FeedVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentMyFeedBinding.inflate(layoutInflater), isRefresh = true, isMore = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedVM!!.userBlog() {
            finishData(true, true, true)
            if (it.msg != null) {
                tips(it.code)
                return@userBlog
            }
            showView(it.data)
        }
        feedVM!!.userBlog(uid = Resource.uid).loading(tipsView)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.userBlog(uid = Resource.uid).loading(tipsView)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.userBlog(uid = Resource.uid)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feedVM!!.userBlog(uid = Resource.uid)
    }

    private fun showView(data: MutableList<Feed>?) {

    }
}