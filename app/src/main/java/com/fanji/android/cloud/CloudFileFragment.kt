package com.fanji.android.cloud

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class CloudFileFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        CommonRecyclerviewBinding.inflate(layoutInflater), isRefresh = true,
        isMore = true, isTopPadding = false
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var fileAdapter: KAdapter<FileData>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fileAdapter = binding.recyclerView.create(
            R.layout.fragment_cloud_file_item,
            {
                val fileItemIcon = findViewById<FJCircleImg>(R.id.fileItemIcon)
                val fileItemName = findViewById<TextView>(R.id.fileItemName)
                val fileItemTime = findViewById<TextView>(R.id.fileItemTime)
                val fileItemSize = findViewById<TextView>(R.id.fileItemSize)
                val fileItemChecked = findViewById<CheckBox>(R.id.fileItemChecked)
                fileItemName.text = it.name
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

    private fun test(isRefresh: Boolean) {
        val list = ArrayList<FileData>()
        for (i in 1..50) {
            val fileData = FileData()
            fileData.name = "梵记$i"
            list.add(fileData)
        }
        fileAdapter?.add(list, isRefresh)
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