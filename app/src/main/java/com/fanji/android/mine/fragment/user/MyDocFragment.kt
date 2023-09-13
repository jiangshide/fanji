package com.fanji.android.mine.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentUserDocBinding
import com.fanji.android.doc.PdfFragment
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/9/13
 * @email: 18311271399@163.com
 * @description:
 */
class MyDocFragment : BaseFragment<FragmentUserDocBinding>(), FileListener {

    private var adapter: KAdapter<FileData>? = null
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentUserDocBinding.inflate(layoutInflater), isTips = true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = binding.docRecycleView.create(ArrayList(), R.layout.doc_fragent_pdf_item, {
            val docTitle = findViewById<TextView>(R.id.docTitle)
            val docPath = findViewById<TextView>(R.id.docPath)
            docTitle.text = it.name
            docPath.text = it.path
        }, {
            push(PdfFragment(this))
        }
        )
        FJFiles.fileListSync(DOC, this)
    }

    override fun onFiles(files: List<FileData>) {
        adapter!!.add(files)
    }
}