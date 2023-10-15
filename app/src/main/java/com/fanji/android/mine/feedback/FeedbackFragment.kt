package com.fanji.android.mine.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentFeedbackBinding
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class FeedbackFragment : BaseFragment<FragmentFeedbackBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentFeedbackBinding.inflate(layoutInflater),
        title = "意见反馈",
        rightBtn = "反馈历史",
        fragment = FeedbackHistoryFragment()
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}