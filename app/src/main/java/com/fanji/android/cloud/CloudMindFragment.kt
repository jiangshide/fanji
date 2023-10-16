package com.fanji.android.cloud

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.fanji.android.R
import com.fanji.android.cloud.file.DocManagerFragment
import com.fanji.android.cloud.file.MediaFragment
import com.fanji.android.databinding.FragmentCloudMindBinding
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
class CloudMindFragment : BaseFragment<FragmentCloudMindBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentCloudMindBinding.inflate(layoutInflater), isRefresh = true,
        isMore = true, isTopPadding = false
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var mindAdapter: KAdapter<FileData>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mindPic.setOnClickListener {
            push(MediaFragment("图片"))
        }
        binding.mindVideo.setOnClickListener {
            push(MediaFragment("视频"))
        }
        binding.mindAudio.setOnClickListener {
            push(MediaFragment("音频"))
        }
        binding.mindDoc.setOnClickListener {
            push(DocManagerFragment())
        }
        mindAdapter =
            binding.mindRecyclerView.create(R.layout.fragment_cloud_mind_item, {
                val mindItemTime = findViewById<TextView>(R.id.mindItemTime)
                val mindItemStatus = findViewById<TextView>(R.id.mindItemStatus)
                val mintItemIcon = findViewById<FJCircleImg>(R.id.mintItemIcon)
                val mindItemFileName = findViewById<TextView>(R.id.mindItemFileName)
                val mindItemPosition = findViewById<TextView>(R.id.mindItemPosition)
                mindItemFileName.text = it.name
            }, {})

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
        mindAdapter?.add(list, isRefresh)
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