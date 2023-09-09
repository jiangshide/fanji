package com.fanji.android.mine.fragment.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMyChannelBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MyChannelFragment : BaseFragment<FragmentMyChannelBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMyChannelBinding.inflate(layoutInflater)
}