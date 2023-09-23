package com.fanji.android.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fanji.android.databinding.FragmentFollowBinding
import com.fanji.android.files.utils.PickerManager
import com.fanji.android.files.viewmodels.VMDocPicker
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FollowFragment : BaseFragment<FragmentFollowBinding>() {

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    lateinit var viewModel: VMDocPicker
    var startTime = 0L
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentFollowBinding.inflate(layoutInflater), isRefresh = true, isMore = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(VMDocPicker::class.java)
        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
            if (it.msg != null) {
                tips()
            }
        })
        feedVM!!.recommendBlog().loading(tipsView)
        viewModel.lvDocData.observe(viewLifecycleOwner, Observer { files ->
            val duration = System.currentTimeMillis() - startTime
            LogUtil.e("----jsd---", "---files:", files.size, " | duration:", duration)
//            setDataOnFragments(files)
//            adapter!!.add(files)
        })
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.recommendBlog().loading(tipsView)
        startTime = System.currentTimeMillis()
        PickerManager.addDocTypes()
        viewModel.getDocs(PickerManager.getFileTypes(), PickerManager.sortingType.comparator)
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