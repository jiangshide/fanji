package com.fanji.android.publish

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPublishBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class PublishFragment : BaseFragment<FragmentPublishBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPublishBinding.inflate(layoutInflater)
}