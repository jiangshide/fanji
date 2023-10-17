package com.fanji.android

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentEmptyBinding
import com.fanji.android.ui.base.BaseFragment

/**
 * @author: jiangshide
 * @date: 2023/10/17
 * @email: 18311271399@163.com
 * @description:
 */
class EmptyFragment : BaseFragment<FragmentEmptyBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEmptyBinding.inflate(layoutInflater)
}