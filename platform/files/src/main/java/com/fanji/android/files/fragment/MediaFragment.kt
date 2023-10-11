package com.fanji.android.files.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.fanji.android.files.FileListener
import com.fanji.android.files.R
import com.fanji.android.files.databinding.FragmentMediaBinding
import com.fanji.android.files.viewmodels.MediaVM
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.FJNumberSelectView
import com.fanji.android.ui.adapter.HORIZONTAL
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.IMG
import com.fanji.android.util.data.VIDEO

/**
 * @author: jiangshide
 * @date: 2023/10/1
 * @email: 18311271399@163.com
 * @description:
 */
class MediaFragment(val type: Int, val fileListener: FileListener) :
    BaseFragment<FragmentMediaBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = initView(
        FragmentMediaBinding.inflate(layoutInflater), isTopPadding = true, isTips = true,
        isTitle = true,
        title = "图片"
    )

    val mediaVM = create(MediaVM::class.java)
    private var adapter: KAdapter<FileData>? = null
    private var selectedAdapter: KAdapter<FileData>? = null
    private val hashMap = HashMap<FileData, FJNumberSelectView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRightListener {
            val list = ArrayList<FileData>()
            hashMap.forEach { (t, u) ->
                list.add(t)
            }
            fileListener.onFiles(list)
            pop()
        }
        adapter = binding.mediaRecycleView.create(ArrayList(), R.layout.fragment_media_item, {
            val fileItemIcon = findViewById<FJImageView>(R.id.fileItemIcon)
            val fileItemCheck = findViewById<FJNumberSelectView>(R.id.fileItemCheck)
            fileItemCheck.isSelected = it.selected
            alpha(fileItemIcon, it.selected, it, fileItemCheck)
            fileItemCheck.setOnStateChangeListener { check ->
                it.selected = check
                alpha(fileItemIcon, it.selected, it, fileItemCheck)
                fileItemCheck.setNum(hashMap.size, it.selected)
            }
            fileItemIcon.load(it.path)
        }, {
            preview(this)
        }, manager = GridLayoutManager(requireContext(), 4))
        selectedAdapter = binding.selectedMediaRecycleView.create(
            ArrayList(),
            R.layout.fragment_media_selected_item,
            {
                val fileSelectedItemIcon = findViewById<FJImageView>(R.id.fileSelectedItemIcon)
                val fileSelectedItemCheck = findViewById<ImageView>(R.id.fileSelectedItemCheck)
                fileSelectedItemCheck.setOnClickListener { view ->
                    changeState(it)
                }
                fileSelectedItemIcon.load(it.path)
            },
            {
                preview(this)
            },
            manager = binding.selectedMediaRecycleView.HORIZONTAL()
        )
        mediaVM.lvMediaData.observe(requireActivity(), Observer {
            finishData(isFinishTips = true)
            adapter?.add(it)
        })
        mediaVM.getMedia(requireContext(), mediaType = type).loading(tipsView)
    }

    fun preview(fileData: FileData) {
        if (type == IMG) {
            viewImg(fileData.path)
        } else if (type == VIDEO) {
            push(VideoPreviewFragment(fileData))
        }
    }

    fun alpha(view: View, isAlpha: Boolean, fileData: FileData, numSelectView: FJNumberSelectView) {
        if (isAlpha) {
            view.alpha = 0.3F
            if (!hashMap.containsKey(fileData)) {
                fileData.position = hashMap.size + 1
                hashMap[fileData] = numSelectView
                selectedAdapter!!.add(fileData)
            }
        } else {
            view.alpha = 1.0F
            if (hashMap.containsKey(fileData)) {
                changeState(fileData)
            }
        }
        val size = hashMap.size
        if (size > 0) {
            setTopTitle("已选择($size)")
            setRight("完成")
        } else {
            setTopTitle("未选择")
            setRight("")
        }
    }

    private fun changeState(fileData: FileData) {
        hashMap.forEach { (t, u) ->
            if (t.position > fileData.position) {
                t.position--
                u.setNum(t.position, true)
            }
        }
        fileData.position = 0
        hashMap.remove(fileData)
        selectedAdapter!!.remove(fileData)
    }

    override fun onRetry(view: View?) {
        super.onRetry(view)
    }
}