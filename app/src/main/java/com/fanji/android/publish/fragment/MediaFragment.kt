package com.fanji.android.publish.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentPublishMediaBinding
import com.fanji.android.img.FJImg
import com.fanji.android.publish.AUDIO_RECORD_RECORDING
import com.fanji.android.publish.AUDIO_RECORD_STATE
import com.fanji.android.publish.AUDIO_RECORD_STOP
import com.fanji.android.resource.base.BaseFragment
import com.fanji.android.ui.FJImageView
import com.fanji.android.ui.FJToast
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.anim.Anim
import com.fanji.android.files.utils.ImageCaptureManager
import com.fanji.android.files.viewmodels.VMMediaPicker
import com.fanji.android.util.AppUtil
import com.fanji.android.util.DateUtil
import com.fanji.android.util.FJEvent
import com.fanji.android.util.FileUtil
import com.fanji.android.util.LogUtil
import com.fanji.android.util.Uri2PathUtil
import com.fanji.android.util.data.AUDIO
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.IMG
import com.fanji.android.util.data.VIDEO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * @author: jiangshide
 * @date: 2023/9/10
 * @email: 18311271399@163.com
 * @description:
 */
class MediaFragment(
    private val type: Int,
    private val title: String,
    private val res: Int,
    private val listener: OnMediaListener
) : BaseFragment<FragmentPublishMediaBinding>() {

    private var adapter: KAdapter<FileData>? = null
    private val data = ArrayList<FileData>()

    private var imageCaptureManager: ImageCaptureManager? = null

    private var mUri: Uri? = null
    lateinit var viewModel: VMMediaPicker
    private var MAX_SIZE = 50 * 1024 * 1024

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPublishMediaBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
        ).get(VMMediaPicker::class.java)
        imageCaptureManager = ImageCaptureManager(requireContext())
        viewModel.lvMediaData.observe(requireActivity(), Observer { data ->
            finishData(true, true, true)
            adapter?.add(data)
            adapter?.add(0, FileData())
        })

        adapter = binding.mediaRecycleView.create(
            ArrayList(),
            R.layout.publish_fragment_list_item, {
                val mediaItemCheck = findViewById<ImageView>(R.id.mediaItemCheck)
                val mediaItemAddL = findViewById<LinearLayout>(R.id.mediaItemAddL)
                val mediaItemTime = findViewById<TextView>(R.id.mediaItemTime)
                val mediaItemAddTxt = findViewById<TextView>(R.id.mediaItemAddTxt)
                val mediaItemAddIcon = findViewById<ImageView>(R.id.mediaItemAddIcon)
                val mediaItemImg = findViewById<FJImageView>(R.id.mediaItemImg)

                val media: FileData = it
                if (it.uri == null) {
                    mediaItemCheck.visibility = View.GONE
                    mediaItemAddL?.visibility = View.VISIBLE
                    mediaItemTime?.text = ""

                    mediaItemAddTxt?.text = title
                    if (type == IMG) {
                        when (media.recordState) {
                            AUDIO_RECORD_RECORDING -> {
                                mediaItemAddIcon?.setImageResource(com.fanji.android.resource.R.mipmap.recording)
                                Anim.anim(mediaItemAddIcon, com.fanji.android.resource.R.anim.heat)
                                LogUtil.e("----------this:", media)

                            }

                            AUDIO_RECORD_STOP -> {
                                mediaItemAddIcon?.setImageResource(res)
                            }
                        }
                    } else {
                        mediaItemAddIcon?.setImageResource(res)
                    }
                } else {
                    mediaItemCheck.visibility = View.VISIBLE
                    mediaItemAddL?.visibility = View.GONE
                    media.path =
                        Uri2PathUtil.getRealPathFromUri(AppUtil.getApplicationContext(), media?.uri)
                    when (type) {
                        IMG -> {
                            FJImg.loadImage(
                                media?.path,
                                mediaItemImg,
                                com.fanji.android.resource.R.mipmap.splash
                            )
                            mediaItemTime?.text = "${FileUtil.getFileSize(media.path)}"
                        }

                        AUDIO -> {
                            media?.showAudioImg(
                                mediaItemImg,
                                com.fanji.android.resource.R.mipmap.splash
                            )
                            mediaItemTime?.text =
                                "${DateUtil.formatSeconds(media.duration / 1000)}/${
                                    FileUtil.getFileSize(
                                        media.path
                                    )
                                }"
                        }

                        VIDEO -> {
                            FJImg.loadImage(
                                media?.path,
                                mediaItemImg,
                                com.fanji.android.resource.R.mipmap.splash
                            )
                            mediaItemTime?.text =
                                "${DateUtil.formatSeconds(media.duration / 1000)}/${
                                    FileUtil.getFileSize(
                                        media.path
                                    )
                                }"
                        }
                    }
                }
                if (mUri != null && media.uri == mUri) {
                    media.selected = true
                    data.add(media)
                    listener?.onMedia(data, type)
                }
                mediaItemCheck.isSelected = media.selected
            },
            {
                if (uri == null) {
                    when (type) {
                        IMG -> {
                            openCamera()
                        }

                        AUDIO -> {
                            FJEvent.get().with("clearSelected").post(type)
                            recordState = AUDIO_RECORD_RECORDING
                            FJEvent.get().with(AUDIO_RECORD_STATE).post(AUDIO_RECORD_RECORDING)
                            adapter?.notifyDataSetChanged()
                        }

                        VIDEO -> {
                            openVideo()
                        }
                    }
                    return@create
                }

                selected = !selected
                if (selected && !data.contains(this)) {
                    if (type == AUDIO || type == VIDEO) {
                        data.clear()
                        if (mUri == uri) {
                            mUri = null
                        }
                        adapter?.datas()?.forEach {
                            it.selected = false
                        }
                        adapter?.notifyDataSetChanged()
                        selected = true
                    } else if (data.size > 8) {
                        selected = false
                        adapter?.notifyDataSetChanged()
                        FJToast.txt("最多只能选择9张!")
                        return@create
                    }
                    data.add(this)
//                    mediaItemAddIcon?.clearAnimation()
                } else if (!selected && data.contains(this)) {
                    if (uri == mUri) {
                        mUri = null
                    }
                    data.remove(this)
                }
                adapter?.notifyDataSetChanged()
                listener?.onMedia(data, type)
                FJEvent.get().with("clearSelected").post(type)
            },
            GridLayoutManager(activity, 4)
        )
        FJEvent.get().with("clearSelected", Int::class.java).observes(this, Observer {
            if (type != it) {
                data?.clear()
                adapter?.datas()?.forEach { it ->
                    it.selected = false
                    it.recordState = AUDIO_RECORD_STOP
                }
                adapter?.notifyDataSetChanged()
            }
        })

        FJEvent.get().with(REMOVE_SELECTED, FileData::class.java).observes(this, Observer {
            if (type == IMG) {
                adapter?.datas()?.forEachIndexed { index, fileData ->
                    if (it == fileData) {
                        fileData.selected = false
                        if (data.contains(it)) {
                            data?.remove(it)
                        }
                        adapter?.notifyItemChanged(index)
                    }
                }
            }
        })
        viewModel.getMedia(mediaType = type)
        loading()
    }

    private fun openCamera() {
        try {
            uiScope.launch {
                val intent =
                    withContext(Dispatchers.IO) { imageCaptureManager?.dispatchTakePictureIntent() }
                if (intent != null) {
                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO)
                } else {
                    Toast.makeText(requireContext(), R.string.no_camera_exists, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun openVideo() {
        try {
            uiScope.launch {
                val intent =
                    withContext(Dispatchers.IO) { imageCaptureManager?.dispatchTakeVideoIntent() }
                if (intent != null) {
                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO)
                } else {
                    Toast.makeText(requireContext(), R.string.no_camera_exists, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ImageCaptureManager.REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                mUri = imageCaptureManager?.currentMediaPath
                viewModel?.getMedia(mediaType = type)
            } else {
                uiScope.launch(Dispatchers.IO) {
                    imageCaptureManager?.deleteContentUri(imageCaptureManager?.currentMediaPath)
                }
            }
        }
    }
}

const val REMOVE_SELECTED = "removeSelected"

interface OnMediaListener {
    fun onMedia(data: MutableList<FileData>, type: Int)
}