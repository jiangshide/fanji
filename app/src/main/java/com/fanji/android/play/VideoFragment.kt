package com.fanji.android.play

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentVideoBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class VideoFragment : BaseFragment<FragmentVideoBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVideoBinding.inflate(layoutInflater)
}