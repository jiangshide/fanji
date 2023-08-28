package com.fanji.android.resource.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.fanji.android.play.audio.AudioRecorder
import com.fanji.android.resource.R
import com.fanji.android.resource.data.Music
import java.lang.ref.WeakReference

/**
 * created by jiangshide on 2019-10-25.
 * email:18311271399@163.com
 */
class MusicPlayerView(
    context: Context,
    attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {
    class MusicPlayerHandler(musicPlayerView: MusicPlayerView) : Handler() {
        private val musicPlayerViewRef: WeakReference<MusicPlayerView> =
            WeakReference(musicPlayerView)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val view = musicPlayerViewRef.get() ?: return
            if (view.callback == null) return

            val curr = view.callback!!.getProgress()
            val progress = curr / 1000f
            val intProgress = progress.toInt()
            view.music_time.text =
                "%02d:%02d/%02d:%02d".format(
                    intProgress / 60, intProgress % 60, view.duration / 60, view.duration % 60
                )
            view.moveTo(progress / view.duration)
            view.sendMessage()
        }
    }

    interface Callback {
        fun onPlayClick()

        fun onPauseClick()

        fun getProgress(): Int

    }

    var callback: Callback? = null

    //  private var music: Music? = null
    private var handler: MusicPlayerHandler = MusicPlayerHandler(this)
    private var duration: Int = 0

    private lateinit var music_play: ImageView
    private lateinit var musicFinish: TextView
    private lateinit var music_wave: MusicWaveView
    private lateinit var music_time: TextView
    private lateinit var music_coordinate: LinearLayout

    init {
        val view = View.inflate(context, R.layout.music_player_view, this)
        music_play = view.findViewById(R.id.music_play)
        musicFinish = view.findViewById(R.id.musicFinish)
        music_wave = view.findViewById(R.id.music_wave)
        music_time = view.findViewById(R.id.music_time)
        music_coordinate = view.findViewById(R.id.music_coordinate)

//        setBackgroundResource(R.drawable.bg_corner_yellow_5)
        music_play.setOnClickListener {
            if (music_play.isSelected) {
                callback?.onPauseClick()
            } else {
                callback?.onPlayClick()
            }
        }
        musicFinish?.setOnClickListener {
            AudioRecorder.getInstance().stopRecord()
            musicFinish?.visibility = View.GONE
            enableClick(true)
        }
    }

    fun showFinish() {
        musicFinish?.visibility = View.VISIBLE
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        music_wave?.waveStart =
            ((music_coordinate.left + music_coordinate.right) / 2 - music_wave.left)
    }

    fun updateMusic(music: Music) {
//    this.music = music
        music_wave?.updateMusic(music)
        this.duration = music.duration / 1000
        music_wave?.moveTo(0f)
        music_time.text = "%02d:%02d/%02d:%02d".format(0, 0, duration / 60, duration % 60)
    }

    fun updateWave(
        waveArray: List<Int>,
        duration: Long
    ) {
        music_wave.updateWaveArray(waveArray)
        this.duration = (duration / 1000).toInt()
        val time = "%02d:%02d/%02d:%02d".format(0, 0, this.duration / 60, this.duration % 60)
        music_time?.text = time

        moveTo(1f)
    }

    fun moveTo(percent: Float) {
        music_wave?.moveTo(percent)
    }

    fun onStart() {
        music_play?.isSelected = true
        handler?.removeMessages(0)
        sendMessage()
    }

    fun onPause() {
        music_play?.isSelected = false
        handler?.removeMessages(0)
    }

    fun enableClick(clickable: Boolean) {
        music_play?.isClickable = clickable
        if (music_play.isClickable) {
            music_play?.alpha = 1f
        } else {
            music_play?.alpha = .5f
        }
    }

    private fun sendMessage() {
        handler?.sendEmptyMessageDelayed(0, 50)
    }
}