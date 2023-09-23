package com.fanji.android.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.databinding.FragmentRecommendBinding
import com.fanji.android.pdf.PdfUtils
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.thread.ruler.thread
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class RecommendFragment : BaseFragment<FragmentRecommendBinding>() {

    private var feedVM: FeedVM? = create(FeedVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentRecommendBinding.inflate(layoutInflater), isRefresh = true, isMore = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
            if (it.msg != null) {
                tips(code = it.code)
            }
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.recommendBlog().loading(tipsView)
        thread {
            val list = PdfUtils.getDocumentData(requireContext())
            LogUtil.e("--------jsd------", "-----pdf~list.size:", list.size)
        }
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