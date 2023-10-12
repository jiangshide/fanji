package com.fanji.android.cloud

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fanji.android.R
import com.fanji.android.databinding.FragmentCloudMindBinding
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
class CloudMindFragment : BaseFragment<FragmentCloudMindBinding>() {
    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(FragmentCloudMindBinding.inflate(layoutInflater))

    private var mindAdapter: KAdapter<FileData>? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mindPic.setOnClickListener {

        }
        binding.mindVideo.setOnClickListener {

        }
        binding.mindAudio.setOnClickListener {

        }
        binding.mindDoc.setOnClickListener {

        }
        mindAdapter =
            binding.mindRecyclerView.create(R.layout.fragment_cloud_mind_item, {
                val mindItemTime = findViewById<TextView>(R.id.mindItemTime)
                val mindItemStatus = findViewById<TextView>(R.id.mindItemStatus)
                val mintItemIcon = findViewById<FJCircleImg>(R.id.mintItemIcon)
                val mindItemFileName = findViewById<TextView>(R.id.mindItemFileName)
                val mindItemPosition = findViewById<TextView>(R.id.mindItemPosition)
                mindItemFileName.text = it.name
            }, {})

        val list = ArrayList<FileData>()
        for (i in 1..30) {
            val fileData = FileData()
            fileData.name = "梵记$i"
            list.add(fileData)
        }
        mindAdapter?.add(list)
    }
}