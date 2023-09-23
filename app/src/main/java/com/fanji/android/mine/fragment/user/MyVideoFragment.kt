package com.fanji.android.mine.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentMyVideoBinding
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil
import java.io.File

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class MyVideoFragment : BaseFragment<FragmentMyVideoBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMyVideoBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val maxMemory = Runtime.getRuntime().maxMemory()
        val totalMemory = Runtime.getRuntime().totalMemory()
        val freeMemory = Runtime.getRuntime().freeMemory()
        LogUtil.w("---jas---","----maxMemory:",maxMemory," | totalMemory:",totalMemory," | freeMemory:",freeMemory)
        File("/proc/self/status").forEachLine {
            LogUtil.w("------jsd----","----status~line:",it)
        }
//        Debug.dumpHprofData("")
    }
}