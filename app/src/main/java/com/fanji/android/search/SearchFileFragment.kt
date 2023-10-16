package com.fanji.android.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSearchFileBinding
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.ui.FJEditText.OnKeyboardListener
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.refresh.api.RefreshLayout
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/10/16
 * @email: 18311271399@163.com
 * @description:
 */
class SearchFileFragment : BaseFragment<FragmentSearchFileBinding>(), OnKeyboardListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentSearchFileBinding.inflate(layoutInflater),
        isTopPadding = true,
        isRefresh = true,
        isMore = true
    )

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var fileAdapter: KAdapter<FileData>? = null
    private var historyAdapter: KAdapter<FileData>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener { pop() }
        binding.searchEdit.setListener { s, input ->
            val fileData = FileData()
            fileData.name = input
            historyAdapter?.add(fileData)
        }
        binding.searchEdit.setKeyBoardListener(requireActivity(), this)
        binding.search.setOnClickListener {
            binding.searchEdit.hide()
        }
        fileAdapter = binding.searchRecyclerView.create(R.layout.fragment_search_file_item, {
            val icon = findViewById<FJImageView>(R.id.icon)
            val name = findViewById<TextView>(R.id.name)
            val size = findViewById<TextView>(R.id.size)
            val path = findViewById<TextView>(R.id.path)
            val checkBox = findViewById<TextView>(R.id.checkBox)
            checkBox.isSelected = it.selected
            name.text = it.name
            size.text = "${it.size}MB"
            path.text = it.cover
        }, {
            selected = !selected
            fileAdapter?.notifyDataSetChanged()
        })
        binding.clearRecord.setOnClickListener {
            historyAdapter?.clear()
        }
        historyAdapter =
            binding.historyRecyclerView.create(R.layout.common_recyclerview_item, {
                val name = findViewById<TextView>(R.id.name)
                name.text = it.name
            }, {
                binding.searchEdit.setText(name)
                showHistory(false)
            }, manager = GridLayoutManager(requireContext(), 3))
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
            fileData.name = "梵记$i.jpg"
            fileData.size = 10
            fileData.cover = "位置：$i xxxx"
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

    override fun show(height: Int) {
        showHistory()
    }

    override fun hide(height: Int) {
        showHistory(false)
    }

    private fun showHistory(isShow: Boolean = true) {
        if (isShow) {
            binding.historyRL.visibility = View.VISIBLE
            return
        }
        binding.historyRL.visibility = View.GONE
    }
}