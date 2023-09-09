package com.fanji.android.doc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPdfBinding
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.util.LogUtil

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class PdfFragment : BaseFragment<FragmentPdfBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPdfBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pdfView.fromAsset("sample.pdf").defaultPage(2).enableDoubletap(true)
            .enableSwipe(true).onPageChange { page, pageCount ->
            LogUtil.e("--------jsd----", "----page:", page, " | pageCount:", pageCount)
        }.load()
    }
}