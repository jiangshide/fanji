package com.fanji.android.mine.fragment.user

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMyFeedBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MyFeedFragment : BaseFragment<FragmentMyFeedBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMyFeedBinding.inflate(layoutInflater)
}