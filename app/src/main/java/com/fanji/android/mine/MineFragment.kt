package com.fanji.android.mine

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMineBinding
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MineFragment : BaseFragment<FragmentMineBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMineBinding.inflate(layoutInflater)
}