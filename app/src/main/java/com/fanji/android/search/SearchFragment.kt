package com.fanji.android.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentSearchBinding
import com.fanji.android.mine.personal.OpusFragment
import com.fanji.android.mine.personal.ReplyFragment
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.google.android.material.appbar.AppBarLayout

/**
 * @author: jiangshide
 * @date: 2023/10/11
 * @email: 18311271399@163.com
 * @description:
 */
class SearchFragment : BaseFragment<FragmentSearchBinding>(), AppBarLayout.OnOffsetChangedListener,
    FJEditText.OnKeyboardListener {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentSearchBinding.inflate(layoutInflater), isTips = true)

    private val feedVM: FeedVM = create(FeedVM::class.java)
    private var adapter: KAdapter<Feed>? = null
    private var vagueAdapter: KAdapter<Feed>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener {
            pop()
        }
        binding.search.setOnClickListener {
            val input = binding.searchEdit.editableText.toString()
            val feed = Feed()
            feed.name = input
            adapter?.add(feed)
            showHistory(false)

        }
        binding.searchEdit.setListener { s, input ->
            val feed = Feed()
            feed.name = input
            adapter?.add(feed)
            vagueAdapter?.add(feed)
        }
        binding.searchEdit.setKeyBoardListener(requireActivity(), this)
        binding.searchHistoryRoot.searchClearRecord.setOnClickListener {
            adapter?.clear()
        }
        adapter = binding.searchHistoryRoot.searchHistoryRecyclerView.create(
            R.layout.common_recyclerview_item,
            {
                val itemName = findViewById<TextView>(R.id.name)
                itemName.text = it.name
            },
            {
                binding.searchEdit.setText(name)
                showHistory(false)
            },
            manager = GridLayoutManager(requireContext(), 3)
        )
        vagueAdapter =
            binding.searchHistoryRoot.searchVagueRecyclerView.create(R.layout.fragment_search_vague,
                {
                    val vagusName = findViewById<TextView>(R.id.vagusName)
                    vagusName.text = it.name
                },
                {
                    binding.searchEdit.setText(name)
                    showHistory(false)
                })
        binding.searchViewPager.adapter = binding.searchViewPager.create(childFragmentManager)
            .setTitles(
                "综合", "内容", "用户", "圈子", "问答"
            )
            .setFragment(
                OpusFragment(),
                ReplyFragment(),
                ReplyFragment(),
                ReplyFragment(),
                ReplyFragment()
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .initTabs(activity, binding.searchTab, binding.searchViewPager, true)
        binding.appBarLayout.addOnOffsetChangedListener(this)

        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
//            if (it.msg != null) {
//                tips(code = it.code)
//            }
//            tips(it.code)
            test(it.isRefresh)
        })
        feedVM!!.recommendBlog().loading(tipsView)
    }

    private fun test(isRefresh: Boolean) {
        val feeds = ArrayList<Feed>()
        for (i in 1..10) {
            val feed = Feed()
            feed.name = "梵山科技$i"
            feeds.add(feed)
        }
        adapter?.add(feeds, isRefresh)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
//        FJFiles.openFile(requireContext(), IMG, this)
        feedVM!!.recommendBlog().loading(tipsView)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {

    }

    override fun show(height: Int) {
        showHistory()
    }

    override fun hide(height: Int) {
        showHistory(false)
    }

    private fun showHistory(isShow: Boolean = true) {
        if (isShow) {
            binding.searchHistoryRoot.root.visibility = View.VISIBLE
            binding.searchViewPager.visibility = View.GONE
            binding.searchTab.visibility = View.GONE
            binding.searchHistoryRoot.searchVagueRecyclerView.visibility = View.VISIBLE
            return
        }
        binding.searchHistoryRoot.root.visibility = View.GONE
        binding.searchViewPager.visibility = View.VISIBLE
        binding.searchTab.visibility = View.VISIBLE
        binding.searchEdit.hide()
        binding.searchHistoryRoot.searchVagueRecyclerView.visibility = View.GONE
    }
}