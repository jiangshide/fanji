package com.fanji.android.files.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fanji.android.files.FJFiles
import com.fanji.android.files.FileListener
import com.fanji.android.files.databinding.FragmentFileBinding
import com.fanji.android.files.viewmodels.VMDocPicker
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/9/23
 * @email: 18311271399@163.com
 * @description:
 */
class FileFragment(
    private val type: Int,
    private val title: String,
    private val res: Int,
    private val listener: FileListener
) : BaseFragment<FragmentFileBinding>(), FileListener {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFileBinding.inflate(layoutInflater)


    private var adapter: KAdapter<FileData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        LogUtil.e("----jsd---", "---type:", type, " | title:", title)
        if (type == DOC) {
            LogUtil.e("----jsd---", "---type1:", type, " | title:", title)
//            viewDoc.getDocs(PickerManager.getFileTypes(), PickerManager.sortingType.comparator)
            FJFiles.fileListSync("pdf", this)
        } else {
        }
    }

    override fun onFiles(files: List<FileData>) {
        LogUtil.e("----jsd---", "------files.size:", files.size)
        adapter!!.add(files)
    }
}