package com.fanji.android.cloud.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData
import kotlin.random.Random

/**
 * @author: jiangshide
 * @date: 2023/10/16
 * @email: 18311271399@163.com
 * @description:
 */
class MediaFragment(val title: String) : BaseFragment<CommonRecyclerviewBinding>() {
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
            R.layout.fragment_media,
            {
                val date = findViewById<TextView>(R.id.date)
                date.text = it.name
                val mediaRecyclerView = findViewById<RecyclerView>(R.id.mediaRecyclerView)
                val mediaAdapter: KAdapter<FileData> =
                    mediaRecyclerView.create(
                        com.fanji.android.files.R.layout.fragment_media_item,
                        {
                            val fileItemIcon = findViewById<FJImageView>(R.id.fileItemIcon)
//                            fileItemIcon.load("")
                        },
                        {}, getIcons(), manager = GridLayoutManager(requireContext(), 4)
                    )
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

    fun getIcons(): ArrayList<FileData> {
        val list = ArrayList<FileData>()
        for (i in 1..20) {
            val fileData = FileData()
            fileData.path = ""
            list.add(fileData)
        }
        return list
    }

    fun test(isRefresh: Boolean) {
        val users = ArrayList<User>()
        val random = Random.nextInt(11)
        LogUtil.e("----jsd---", "-----random:", random)
        for (i in 1..random) {
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