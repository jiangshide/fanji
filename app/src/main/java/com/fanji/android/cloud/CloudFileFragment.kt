package com.fanji.android.cloud

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.ui.FJCircleImg
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/10/3
 * @email: 18311271399@163.com
 * @description:
 */
class CloudFileFragment : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater))

    private var fileAdapter: KAdapter<FileData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fileAdapter = binding.recyclerView.create(
            R.layout.fragment_cloud_file_item,
            {
                val fileItemIcon = findViewById<FJCircleImg>(R.id.fileItemIcon)
                val fileItemName = findViewById<TextView>(R.id.fileItemName)
                val fileItemTime = findViewById<TextView>(R.id.fileItemTime)
                val fileItemSize = findViewById<TextView>(R.id.fileItemSize)
                val fileItemChecked = findViewById<CheckBox>(R.id.fileItemChecked)
                fileItemName.text = it.name
            },
            {})
        val list = ArrayList<FileData>()
        for (i in 1..50) {
            val fileData = FileData()
            fileData.name = "梵记$i"
            list.add(fileData)
        }
        fileAdapter?.add(list)
    }
}