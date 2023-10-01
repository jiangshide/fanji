package com.fanji.android.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.databinding.FragmentFollowBinding
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.files.utils.PickerManager
import com.fanji.android.files.viewmodels.VMDocPicker
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.VIDEO

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FollowFragment : BaseFragment<FragmentFollowBinding>(), FileListener {

    private var feedVM: FeedVM? = create(FeedVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentFollowBinding.inflate(layoutInflater), isRefresh = true, isMore = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
            if (it.msg != null) {
                tips()
            }
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
//        feedVM!!.recommendBlog().loading(tipsView)
        FJFiles.openFile(requireContext(), DOC, this)
//        viewDoc.getDocs(PickerManager.getFileTypes(), PickerManager.sortingType.comparator)
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