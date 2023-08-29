package com.fanji.android.mine.fragment.set

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentAboutBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAboutBinding.inflate(layoutInflater)
}