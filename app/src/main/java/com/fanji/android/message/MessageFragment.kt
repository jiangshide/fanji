package com.fanji.android.message

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMessageBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MessageFragment : BaseFragment<FragmentMessageBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMessageBinding.inflate(layoutInflater)
}