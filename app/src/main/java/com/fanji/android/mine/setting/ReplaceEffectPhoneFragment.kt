package com.fanji.android.mine.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentReplaceEffectPhoneBinding
import com.fanji.android.ui.FJToast
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/15
 * @email: 18311271399@163.com
 * @description:
 */
class ReplaceEffectPhoneFragment : BaseFragment<FragmentReplaceEffectPhoneBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentReplaceEffectPhoneBinding.inflate(layoutInflater), title = "更换手机号")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.next.setOnClickListener {
            FJToast.fixTxt(requireContext(), "换绑完成！")
            pop(2)
        }
    }
}