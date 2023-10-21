package com.fanji.android.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.R
import com.fanji.android.comment.CommentFragment
import com.fanji.android.databinding.FragmentFeedDetailBinding
import com.fanji.android.dialog.CurDialog
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator

/**
 * @author: jiangshide
 * @date: 2023/10/11
 * @email: 18311271399@163.com
 * @description:
 */
class FeedDetailFragment(val feed: Feed) : BaseFragment<FragmentFeedDetailBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentFeedDetailBinding.inflate(layoutInflater),
        title = feed.name,
        rightBtn = R.mipmap.share
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRightListener {
            CurDialog.share(requireContext())
        }
//        binding.feedDetailRoot.title.text=""
        binding.feedDetailRoot.icon.load(Resource.getUrl())
        binding.feedDetailRoot.nick.text = "梵山科技"
//        binding.feedDetailRoot.vip
        binding.feedDetailRoot.follow.setOnClickListener {
            CurDialog.follow(requireContext())
        }
//        binding.feedDetailRoot.contents
//        binding.feedDetailRoot.editTime
        binding.feedDetailRoot.like.setOnClickListener {

        }
//        binding.feedDetailRoot.likeNum
//        binding.feedDetailRoot.readNum
//        binding.feedDetailRoot.collect

        binding.commentViewPager.adapter = binding.commentViewPager.create(childFragmentManager)
            .setTitles(
                "全部评论"
            )
            .setFragment(
                CommentFragment()
            )
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .initTabs(activity, binding.commentTab, binding.commentViewPager, true)
    }
}