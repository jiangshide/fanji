package com.fanji.android.resource.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fanji.android.play.controller.StandardVideoController
import com.fanji.android.play.dplay.player.AbstractPlayer
import com.fanji.android.play.dplay.player.VideoView
import com.fanji.android.resource.R
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.util.data.FileData

/**
 * created by jiangshide on 5/7/21.
 * email:18311271399@163.com
 */
class VideoPreviewFragment(private val fileData: FileData? = null) : BaseFragment() {

    private var path: String = ""

    private lateinit var videoPreview: VideoView<AbstractPlayer>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setTitleView(R.layout.video_preview_fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoPreview = view.findViewById(R.id.videoPreview)

        if (fileData == null) pop()
        setLeftListener {
            pop()
        }
        if (!TextUtils.isEmpty(fileData?.outPath)) {
            path = fileData?.outPath!!
        } else {
            path = fileData?.path!!
        }
        videoPreview?.setUrl(path)
        val standardVideoController = StandardVideoController(requireActivity())
        standardVideoController?.addDefaultControlComponent(fileData?.name, false)
        videoPreview?.setVideoController(standardVideoController)
        videoPreview?.setLooping(true)
        videoPreview?.start()
    }

    override fun onPause() {
        super.onPause()
        videoPreview?.release()
    }

    override fun onStop() {
        super.onStop()
        videoPreview?.release()
    }
}