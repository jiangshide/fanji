package com.fanji.android.cloud.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.CommonRecyclerviewBinding
import com.fanji.android.ui.FJButton
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
class TransferFragment(val type: Int) : BaseFragment<CommonRecyclerviewBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(CommonRecyclerviewBinding.inflate(layoutInflater))

    private var transferAdapter: KAdapter<FileData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transferAdapter = binding.recyclerView.create(
            R.layout.fragment_transfer_item,
            {
                val transferItemIcon = findViewById<FJCircleImg>(R.id.transferItemIcon)
                val transferItemName = findViewById<TextView>(R.id.transferItemName)
                val transferItemTime = findViewById<TextView>(R.id.transferItemTime)
                val transferItemSize = findViewById<TextView>(R.id.transferItemSize)
                val transferItemOption = findViewById<FJButton>(R.id.transferItemOption)
                val transferItemProgressBar =
                    findViewById<ProgressBar>(R.id.transferItemProgressBar)
                transferItemName.text = it.name
            },
            {})

        val list = ArrayList<FileData>()
        for (i in 1..60) {
            val fileData = FileData()
            fileData.name = "梵记$i"
            list.add(fileData)
        }
        transferAdapter?.add(list)
    }
}