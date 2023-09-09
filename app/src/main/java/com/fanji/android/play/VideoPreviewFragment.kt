package com.fanji.android.play

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.databinding.FragmentVideoPreviewBinding
import com.fanji.android.play.controller.StandardVideoController
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.util.data.FileData

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class VideoPreviewFragment(private val fileData: FileData? = null) :
    BaseFragment<FragmentVideoPreviewBinding>() {

    private var path: String = ""

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVideoPreviewBinding.inflate(layoutInflater)


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