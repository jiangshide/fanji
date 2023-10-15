package com.fanji.android.mine.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentReplacePhoneBinding
import com.fanji.android.mine.feedback.FeedbackFragment
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class ReplacePhoneFragment : BaseFragment<FragmentReplacePhoneBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentReplacePhoneBinding.inflate(layoutInflater), title = "更换手机号")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.validateCode.setOnClickListener {

        }
        binding.next.setOnClickListener {
            push(ReplaceEffectPhoneFragment())
        }
        binding.feedback.setOnClickListener {
            push(FeedbackFragment())
        }
    }
}