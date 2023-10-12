package com.fanji.android.find.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentFollowContentBinding
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/11
 * @email: 18311271399@163.com
 * @description:
 */
class FollowContentFragment(val feed: Feed) : BaseFragment<FragmentFollowContentBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentFollowContentBinding.inflate(layoutInflater),
        title = feed.name,
        rightBtn = "分享"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRightListener {

        }
    }
}