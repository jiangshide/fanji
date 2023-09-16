package com.fanji.android.doc

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentPdfBinding
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.util.data.FileData

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class PdfFragment(private val fileData: FileData) : BaseFragment<FragmentPdfBinding>() {

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentPdfBinding.inflate(layoutInflater),
        title = fileData.name
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.pdfView.fromFile(fileData.path).swipeVertical(true)
//            .defaultPage(SPUtil.getInt(fileData.name, 0))
//            .enableSwipe(true).onPageChange { page, pageCount ->
//                SPUtil.putInt(fileData.name, page)
//            }.load()
        val bundle = Bundle()
        bundle.putString("filePath",fileData.path)
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().path)
        binding.tbsPdfView.openFile(bundle)
    }
}