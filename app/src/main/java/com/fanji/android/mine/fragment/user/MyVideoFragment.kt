package com.fanji.android.mine.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentMyVideoBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.adapter.HORIZONTAL
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil
import java.io.File

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MyVideoFragment : BaseFragment<FragmentMyVideoBinding>() {

    private var adapter: KAdapter<User>? = null
    private var adapter1: KAdapter<User>? = null
    private var feedVM: FeedVM? = create(FeedVM::class.java)

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentMyVideoBinding.inflate(layoutInflater),
        isTopPadding = false,
        isRefresh = true,
        isMore = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val maxMemory = Runtime.getRuntime().maxMemory()
        val totalMemory = Runtime.getRuntime().totalMemory()
        val freeMemory = Runtime.getRuntime().freeMemory()
        LogUtil.w(
            "---jas---",
            "----maxMemory:",
            maxMemory,
            " | totalMemory:",
            totalMemory,
            " | freeMemory:",
            freeMemory
        )
        File("/proc/self/status").forEachLine {
            LogUtil.w("------jsd----", "----status~line:", it)
        }
//        Debug.dumpHprofData("")


        adapter =
            binding.videoRecyclerView.create(R.layout.fragment_cloud_file_item, {
                val fileItemName = findViewById<TextView>(R.id.fileItemName)
//                fileItemName.text = it.name + "---jsd"
            }, {
                LogUtil.e("---jsd---", "---1--this:", this)
            })

        val headView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_my_video, null)
        val videoRecyclerView =
            headView.findViewById<RecyclerView>(R.id.videoRecyclerView)

        adapter1 = videoRecyclerView.create(
            R.layout.fragment_cloud_file_item,
            {
                val fileItemName = findViewById<TextView>(R.id.fileItemName)
                fileItemName.text = it.name
            },
            {
                LogUtil.e("---jsd---", "--2---this:", this)
            },
            manager = videoRecyclerView.HORIZONTAL()
        )

//        adapter?.addHeaderView(headView)
//        val footView =
//            LayoutInflater.from(requireActivity()).inflate(R.layout.find_follow_item, null)
//        adapter?.addFooterView(footView)

        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
//            if (it.msg != null) {
//                tips()
//            }

            addData(it.isRefresh)
        })
        feedVM!!.recommendBlog().loading(tipsView)
        addData(true)
    }

    fun addData(isRefresh: Boolean) {
        val list = ArrayList<User>()
        for (i in 1..10) {
            val user = User()
            user.name = "梵山科技$i"
            list.add(user)
        }
        if (isRefresh) {
            adapter1?.update(list)
            adapter?.update(list)
        } else {
            adapter?.add(list)
        }

        LogUtil.e(
            "---------jsd------",
            "----adaper.size:",
            adapter?.count(),
            " | adapter1.size:",
            adapter1?.count(), " | isRefresh:", isRefresh, " | thread:", Thread.currentThread().name
        )
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        super.onRefresh(refreshLayout)
        feedVM!!.recommendBlog()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        super.onLoadMore(refreshLayout)
        feedVM!!.recommendBlog(isRefresh = false)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        feedVM!!.recommendBlog().loading(tipsView)
    }
}