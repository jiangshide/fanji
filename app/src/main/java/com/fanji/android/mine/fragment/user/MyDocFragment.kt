package com.fanji.android.mine.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.doc.PdfFragment
import com.fanji.android.files.FileListener
import com.fanji.android.files.utils.PickerManager
import com.fanji.android.files.viewmodels.VMDocPicker
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/9/13
 * @email: 18311271399@163.com
 * @description:
 */
class MyDocFragment : BaseFragment<CommonRecyclerviewBinding>(), FileListener {

    private var feedVM: FeedVM? = create(FeedVM::class.java)
    private var adapter: KAdapter<FileData>? = null
    var startTime = 0L

    lateinit var viewModel: VMDocPicker
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        CommonRecyclerviewBinding.inflate(layoutInflater),
        isRefresh = true,
        isTips = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        )[VMDocPicker::class.java]
        adapter = binding.recyclerView.create(R.layout.doc_fragent_pdf_item, {
            val docTitle = findViewById<TextView>(R.id.docTitle)
            val docPath = findViewById<TextView>(R.id.docPath)
            docTitle.text = it.name
            docPath.text = it.path
        }, {
            push(PdfFragment(this))
        }
        )
        feedVM!!.recommendBlog.observe(requireActivity(), Observer {
            finishData(true, true, true)
            if (it.msg != null) {
                tips()
            }
        })
        feedVM!!.recommendBlog().loading(tipsView)
        viewModel.lvDocData.observe(viewLifecycleOwner, Observer { files ->
            val duration = System.currentTimeMillis() - startTime
            val list = arrayListOf<FileData>()
            for ((key, value) in files) {
                LogUtil.e("---jsd---", "-----key>----:", key, " | size:", value.size)
                adapter!!.add(value)
            }
            LogUtil.e("----jsd---", "---files:", files.size, " | duration:", duration)
//            setDataOnFragments(files)
//            adapter!!.add(files)
        })

    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
        startTime = System.currentTimeMillis()
//        FJFiles.fileListSync("pdf", this)
        viewModel.getDocs(PickerManager.getFileTypes(), PickerManager.sortingType.comparator)
    }

    override fun onFiles(files: List<FileData>) {
        val duration = System.currentTimeMillis() - startTime
        LogUtil.e("-----jsd----", "------onFiles~size:", files.size, " | duration:", duration)
        adapter!!.add(files)
    }
}