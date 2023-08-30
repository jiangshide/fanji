package com.fanji.android.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentFollowBinding
import com.fanji.android.doc.PdfFragment
import com.fanji.android.resource.base.BaseFragment

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class FollowFragment : BaseFragment<FragmentFollowBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFollowBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.docBtn.setOnClickListener {
            push(PdfFragment())
        }
    }
}