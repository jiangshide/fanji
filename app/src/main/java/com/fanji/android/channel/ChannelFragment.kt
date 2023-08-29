package com.fanji.android.channel

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentChannelBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class ChannelFragment : BaseFragment<FragmentChannelBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChannelBinding.inflate(layoutInflater)
}