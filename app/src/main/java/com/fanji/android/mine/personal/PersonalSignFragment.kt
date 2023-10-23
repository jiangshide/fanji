package com.fanji.android.mine.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPersonalSignBinding
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/22
 * @email: 18311271399@163.com
 * @description:
 */
class PersonalSignFragment : BaseFragment<FragmentPersonalSignBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentPersonalSignBinding.inflate(layoutInflater), title = "个性签名")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}