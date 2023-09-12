package com.fanji.android.doc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fanji.android.R
import com.fanji.android.databinding.FragmentPdfBinding
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.files.viewmodels.VMDocPicker
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.FileData

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class PdfFragment : BaseFragment<FragmentPdfBinding>(), FileListener {

    private var adapter: KAdapter<FileData>? = null
    lateinit var viewModel: VMDocPicker
    private var isRead = false

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentPdfBinding.inflate(layoutInflater),
        isTitle = true,
        isTips = true,
        isRefresh = true,
        isTopPadding = true
    )

    var start = 0L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("文件")!!.setLeftListener {
            if (isRead) {
                isRead = false
                read()
                return@setLeftListener
            }
            pop()
        }
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(VMDocPicker::class.java)
        start = System.currentTimeMillis()
        viewModel.lvDocData.observe(requireActivity(), Observer { data ->
            LogUtil.e("---jsd---", "---data:", data)
            finishData(true, true, true)
//            adapter.add(data.get(FileType.))
            val duration = System.currentTimeMillis() - start
            LogUtil.e("-------jsd----", "---duration:", duration)
//            adapter?.add(0, FileData())
        })

        adapter = binding.pdfRecycleView.create(ArrayList(), R.layout.doc_fragent_pdf_item, {
            val docTitle = findViewById<TextView>(R.id.docTitle)
            val docPath = findViewById<TextView>(R.id.docPath)
            docTitle.text = it.name
            docPath.text = it.path
        }, {
            LogUtil.e("------jssds---", "----fileData:", this)
            isRead = true
            read()
            binding.pdfView.fromFile(path).enableDoubletap(true).swipeVertical(true)
                .enableSwipe(true).onPageChange { page, pageCount ->
                    LogUtil.e("--------jsd----", "----page:", page, " | pageCount:", pageCount)
                }.load()
        }
        )
        FJFiles.fileListSync(DOC, this)
    }

    private fun read() {
        if (isRead) {
            binding.pdfView.visibility = VISIBLE
            binding.pdfRecycleView.visibility = GONE
        } else {
            binding.pdfRecycleView.visibility = VISIBLE
            binding.pdfView.visibility = GONE
        }
    }

    override fun onFiles(files: List<FileData>) {
        adapter!!.add(files)
        val duration = System.currentTimeMillis() - start
        LogUtil.e("-------hsdsd---", "---size:", files.size, " | duration:", duration)
    }
}