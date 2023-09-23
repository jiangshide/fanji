package com.fanji.android.publish

import android.media.audiofx.Visualizer
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.fanji.android.R
import com.fanji.android.databinding.FragmentPublishBinding
import com.fanji.android.img.FJImg
import com.fanji.android.location.LocationFragment
import com.fanji.android.location.OnLocationListener
import com.fanji.android.location.data.PoiData
import com.fanji.android.location.data.PositionData
import com.fanji.android.play.VideoPreviewFragment
import com.fanji.android.play.audio.AudioRecorder
import com.fanji.android.publish.fragment.MediaFragment
import com.fanji.android.publish.fragment.OnMediaListener
import com.fanji.android.publish.fragment.REMOVE_SELECTED
import com.fanji.android.resource.Resource
import com.fanji.android.resource.audio.AudioPlay
import com.fanji.android.resource.data.DeviceData
import com.fanji.android.resource.data.Music
import com.fanji.android.resource.task.PUBLISH_UPLOAD
import com.fanji.android.resource.view.MusicPlayerView
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.resource.vm.publish.data.Publish
import com.fanji.android.search.OnChannelListener
import com.fanji.android.search.SearchChannelManagerFragment
import com.fanji.android.search.SearchUserFragment
import com.fanji.android.ui.FJEditText
import com.fanji.android.ui.adapter.KAdapter
import com.fanji.android.ui.adapter.create
import com.fanji.android.ui.base.BaseFragment
import com.fanji.android.ui.tablayout.indicators.LinePagerIndicator
import com.fanji.android.util.CountDown
import com.fanji.android.util.DateUtil
import com.fanji.android.util.FJEvent
import com.fanji.android.util.data.AUDIO
import com.fanji.android.util.data.DOC
import com.fanji.android.util.data.FileData
import com.fanji.android.util.data.IMG
import com.fanji.android.util.data.VIDEO

/**
 * @Author:jiangshide
 * @Date:8/29/23
 * @Email:18311271399@163.com
 * @Description:
 */
class PublishFragment : BaseFragment<FragmentPublishBinding>(), OnMediaListener,
    OnLocationListener, OnChannelListener, FJEditText.OnKeyboardListener,
    Visualizer.OnDataCaptureListener, CountDown.OnCountDownListener,
    MusicPlayerView.Callback {

    private val publish = Publish()

    private var adapter: KAdapter<FileData>? = null

    private val countDown = CountDown()

    private var waveArray: MutableList<Int>? = null
    private var isPlayerPaused: Boolean = false

    override fun viewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = initView(
        FragmentPublishBinding.inflate(layoutInflater),
        isTitle = true,
        isTopPadding = true
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLeft("")?.setRight("发布")?.setRightListener {
            FJEvent.get()
                .with(PUBLISH_UPLOAD)
                .post(publish)
        }
        val positionData = PositionData()
        val deviceData = DeviceData(activity)
        publish.netInfo = positionData.gson
        publish.device = deviceData.gson
        binding.publishViewPager?.adapter = binding.publishViewPager.create(childFragmentManager)
            .setTitles(resources.getStringArray(R.array.files).toList())
            .setFragment(
                MediaFragment(IMG, "拍照", com.fanji.android.resource.R.mipmap.unimg, this),
                MediaFragment(
                    AUDIO, "录音", com.fanji.android.resource.R.mipmap.record, this
                ),
                MediaFragment(VIDEO, "摄像", com.fanji.android.ui.R.mipmap.video, this),
                MediaFragment(DOC, "文件", com.fanji.android.resource.R.mipmap.follow, this)
            )
            .initTabs(requireActivity(), binding.tabsPublish, binding.publishViewPager)
            .setPersistent(false)
            .setMode(LinePagerIndicator.MODE_WRAP_CONTENT)
            .setLinePagerIndicator(color(com.fanji.android.ui.R.color.blue))
        binding.publishContent.publishContent.setKeyBoardListener(activity, this)
        binding.publishContent.publishContent.setListener { s, input ->
            publish?.content = input
            validate()
        }
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        adapter = binding.publishImgs?.create(ArrayList(), R.layout.publish_fragment_head_item, {
            val publishSelectedImg = this.findViewById<ImageView>(R.id.publishSelectedImg)
            val publishSelectedDel = this.findViewById<ImageView>(R.id.publishSelectedDel)
            val fileData: FileData = it
            FJImg.loadImageRound(
                it.path,
                publishSelectedImg,
                5,
                com.fanji.android.resource.R.mipmap.splash
            )
            publishSelectedDel?.setOnClickListener {
                adapter?.remove(fileData)
                FJEvent.get().with(REMOVE_SELECTED).post(fileData)
            }
        }, {
            val urls = ArrayList<String>()
            var position = 0
            adapter?.datas()?.forEachIndexed { index, fileData ->
                if (this == fileData) {
                    position = index
                }
                urls.add(fileData.path!!)
            }
            viewImg(urls, position)
        }, layoutManager)

        binding.publishLocation.setOnClickListener {
            push(LocationFragment(this))
        }
        binding.publishLocationTaL.setOnClickListener {
            push(SearchUserFragment())
        }
        binding.publishSetL.setOnClickListener {
            push(SearchChannelManagerFragment(this))
        }
        binding.publishAudioViewCard.publishAudioView.setOnClickListener {
            if (AudioPlay.getInstance()
                    .isPlaying
            ) {
                AudioPlay.getInstance().pause()
                return@setOnClickListener
            }
            binding.publishAudioViewCard.publishAudioProgress.max =
                publish.files?.get(0)?.duration!!.toInt()
            AudioPlay.getInstance()
                .setPlayStateListener {
                    if (binding.publishAudioViewCard.publishAudioPlay == null) return@setPlayStateListener
                    if (it == AudioPlay.PlayState.STATE_PLAYING) {
                        binding.publishAudioViewCard.publishAudioPlay.visibility = View.GONE
                        countDown.start()
                    } else if (it == AudioPlay.PlayState.STATE_PAUSE) {
                        binding.publishAudioViewCard.publishAudioPlay.visibility = View.VISIBLE
                        binding.publishAudioViewCard.publishAudioView?.hide()
                        countDown.pause()
                    } else if (it == AudioPlay.PlayState.STATE_IDLE) {
                        binding.publishAudioViewCard.publishAudioPlay.visibility = View.VISIBLE
                        binding.publishAudioViewCard.publishAudioProgress.progress = 0
                        binding.publishAudioViewCard.publishAudioView?.hide()
                        countDown.stop()
                    }
                }
                .setDataCaptureListener(this)
                .play(publish.files?.get(0)?.path)
        }
        binding.publishAudioViewCard.publishAudioCoverAdd?.setOnClickListener {
        }
        binding.publishAudioViewCard.publishAudioCover?.setOnClickListener {
            push(
                VideoPreviewFragment(publish.files!![0]).setTitle(publish.files!![0].name)
                    ?.setTopBgIcon(R.color.black)!!
            )
        }
    }

    private fun validate() {
        val disable =
            !TextUtils.isEmpty(publish.content) || (publish.files != null && publish?.files!!.size > 0)
        setRightEnable(disable)
    }

    override fun onMedia(data: MutableList<FileData>, type: Int) {
        publish.format = type
        publish.files = data
        Resource.publish = publish
        binding.publishAudioViewCard.publishAudioViewCard.visibility = View.GONE
        binding.publishImgs.visibility = View.GONE
        AudioRecorder.getInstance().stopRecord()
        AudioPlay.getInstance().stop()
        when (type) {
            IMG -> {
                binding.publishImgs.visibility = View.VISIBLE
                adapter?.add(data, true)
            }

            AUDIO -> {
                binding.publishAudioViewCard.publishAudioViewCard.visibility = View.VISIBLE
                binding.publishAudioViewCard.publishAudioViewF?.visibility = View.VISIBLE
                binding.publishAudioViewCard.publishAudioPlay?.visibility = View.VISIBLE
                binding.publishAudioViewCard.publishAudioRetry?.visibility = View.GONE
                publish.files?.get(0)?.showAudioImg(
                    binding.publishAudioViewCard.publishAudioCover,
                    com.fanji.android.resource.R.mipmap.splash
                )
                initAudio(publish.files?.get(0)!!)
            }

            VIDEO -> {
                binding.publishAudioViewCard.publishAudioViewCard.visibility = View.VISIBLE
                binding.publishAudioViewCard.publishAudioViewF?.visibility = View.GONE
                binding.publishAudioViewCard.publishAudioPlay?.visibility = View.VISIBLE
                FJImg.loadImage(
                    publish.files?.get(0)?.path,
                    binding.publishAudioViewCard.publishAudioCover,
                    com.fanji.android.resource.R.mipmap.splash
                )
            }
        }
    }

    private fun initAudio(fileData: FileData) {
        if (!TextUtils.isEmpty(fileData?.cover)) {
            FJImg.loadImageRound(
                fileData?.cover,
                binding.publishAudioViewCard.publishAudioCover,
                15,
                com.fanji.android.resource.R.mipmap.splash
            )
        }
        binding.publishAudioViewCard.publishAudioTitle.text = fileData?.name
        countDown.setTime(fileData?.duration!!)
            .setListener(this)
        binding.publishAudioViewCard.publishAudioTime.text =
            DateUtil.formatSeconds(fileData?.duration!! / 1000)
    }

    override fun onPoiData(poiData: PoiData) {
        publish.city = poiData?.cityName
        publish.position = poiData.title
        publish.address = poiData.snippet
        binding.publishLocation.text = poiData.title
    }

    override fun onChannel(channelBlog: ChannelBlog) {
        binding.publishChannelName.text = channelBlog.name
        publish.channelId = channelBlog.id
        publish.uid = Resource.uid!!
        publish.channelCover = channelBlog.cover
        FJImg.loadImageRound(
            channelBlog.cover,
            binding.publishChannelNameCover,
            5,
            com.fanji.android.resource.R.mipmap.splash
        )
    }

    override fun show(height: Int) {

    }

    override fun hide(height: Int) {
    }

    override fun onTick(duration: Long) {
        if (binding.publishAudioViewCard.publishAudioTime == null || binding.publishAudioViewCard.publishAudioProgress == null) return
        binding.publishAudioViewCard.publishAudioTime.text =
            "${DateUtil.formatSeconds(publish.files?.get(0)?.duration!! / 1000)}/${
                DateUtil.formatSeconds(
                    duration / 1000
                )
            }"
        binding.publishAudioViewCard.publishAudioProgress.progress = AudioPlay.getInstance()
            .currentPosition()
    }

    override fun onPause(duration: Long) {
    }

    override fun onFinish() {
        if (binding.publishAudioViewCard.publishAudioProgress == null) return
        binding.publishAudioViewCard.publishAudioTime.text =
            DateUtil.formatSeconds(publish.files?.get(0)?.duration!! / 1000)
    }

    override fun onWaveFormDataCapture(
        visualizer: Visualizer?,
        waveform: ByteArray?,
        samplingRate: Int
    ) {
        if (binding.publishAudioViewCard.publishAudioView == null || waveform == null) return
        binding.publishAudioViewCard.publishAudioView?.post {
            if (AudioPlay.getInstance().isPlaying) {
                binding.publishAudioViewCard.publishAudioView?.setWaveData(waveform)
            }
        }
    }

    override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
    }

    override fun onPlayClick() {
        if (waveArray == null) return
        if (isPlayerPaused) {
            AudioPlay.getInstance()
                .start()
            binding.publishAudioViewCard.publishRecordView.onStart()
        } else {
            AudioPlay.getInstance()
                .setPlayStateListener {
                    when (it) {
                        AudioPlay.PlayState.STATE_PLAYING -> {
                            val music = Music()
                            music.waveArray = waveArray
                            music.duration = AudioPlay.getInstance()!!
                                .duration()
//                            publishRecordView?.updateWave(
//                                waveArray!!, AudioPlay.getInstance()!!
//                                    .duration()
//                                    .toLong()
//                            )
                            binding.publishAudioViewCard.publishRecordView?.updateMusic(music)
                            binding.publishAudioViewCard.publishRecordView?.onStart()
                        }

                        AudioPlay.PlayState.STATE_IDLE -> {
                            binding.publishAudioViewCard.publishRecordView?.onPause()
                            isPlayerPaused = false
                        }

                        else -> {}
                    }
                }
                .play(publish?.files?.get(0)!!.path)
        }
        isPlayerPaused = false
    }

    override fun onPauseClick() {
        if (waveArray == null) return
        isPlayerPaused = true
        AudioPlay.getInstance()
            .pause()
        binding.publishAudioViewCard.publishRecordView.onPause()
    }

    override fun getProgress(): Int {
        return AudioPlay.getInstance()
            .currentPosition()
    }
}

const val AUDIO_RECORD_TIME = "audioRecordTime"
const val AUDIO_RECORD_STATE = "audioRecordState"
const val AUDIO_RECORD_RECORDING = 1
const val AUDIO_RECORD_RESUME = 2
const val AUDIO_RECORD_PAUSE = 3
const val AUDIO_RECORD_STOP = 4