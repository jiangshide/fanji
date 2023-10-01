package com.fanji.android.files.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.files.databinding.FragmentVideoPreviewBinding
import com.fanji.android.play.controller.StandardVideoController
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.util.LogUtil
import com.fanji.android.util.data.FileData

/**
 * @author: jiangshide
 * @date: 2023/9/29
 * @email: 18311271399@163.com
 * @description:
 */
class VideoPreviewFragment(private val fileData: FileData? = null) : BaseFragment<FragmentVideoPreviewBinding>() {
    override fun viewBinding(inflater: LayoutInflater, container: ViewGroup?) = initView(
        FragmentVideoPreviewBinding.inflate(layoutInflater)
    )

    private var path: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fileData == null) pop()
        setLeftListener {
            pop()
        }
        if (!TextUtils.isEmpty(fileData?.outPath)) {
            path = fileData?.outPath!!
        } else {
            path = fileData?.path!!
        }
        LogUtil.e("---jsd---","-----path:",path)
        binding.videoPreview?.setUrl(path)
        val standardVideoController = StandardVideoController(requireActivity())
        standardVideoController?.addDefaultControlComponent(fileData?.name, false)
        binding.videoPreview?.setVideoController(standardVideoController)
        binding.videoPreview?.setLooping(true)
        binding.videoPreview?.start()
    }

    override fun onPause() {
        super.onPause()
        binding.videoPreview?.release()
    }

    override fun onStop() {
        super.onStop()
        binding.videoPreview?.release()
    }
}