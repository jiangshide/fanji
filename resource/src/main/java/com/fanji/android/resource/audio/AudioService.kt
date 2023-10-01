package com.fanji.android.resource.audio

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.audiofx.Visualizer
import android.media.audiofx.Visualizer.OnDataCaptureListener
import android.os.Binder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.fanji.android.img.FJImg
import com.fanji.android.img.listener.IBitmapListener
import com.fanji.android.net.exception.NetException
import com.fanji.android.util.FJEvent
import com.fanji.android.resource.FOLLOW
import com.fanji.android.resource.R
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.OnPraiseListener
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.resource.vm.user.OnFollowListener
import com.fanji.android.resource.vm.user.UserVM
import com.fanji.android.util.*
import com.fanji.android.ui.FJNotification
import com.fanji.android.ui.FJToast
import java.util.*

/**
 * created by jiangshide on 2020/4/26.
 * email:18311271399@163.com
 */
class AudioService : Service(), AudioPlay.PlayStateListener, OnDataCaptureListener,
    CountDown.OnCountDownListener,AudioManager.OnAudioFocusChangeListener {
    private val NOTIFICATION_ID = 0x2000000
    private var remoteViews: RemoteViews? = null
    private var remoteViewsBig: RemoteViews? = null
    private var remoteViewsHeadUp: RemoteViews? = null
    private val mBinder: IBinder = AudioServiceBinder()
    private var blogVM: FeedVM? = null
    private var userVM: UserVM? = null
    private var audioManager:AudioManager?=null

    private var notificationManager: NotificationManager? = null

    private var countDown = CountDown()

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
//        audioManager?.abandonAudioFocus(this)
        onStop()
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        blogVM = FeedVM()
        userVM = UserVM()
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager?.listen(object :PhoneStateListener(){
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when(state){
                    TelephonyManager.CALL_STATE_RINGING->{
                        onStop()
                    }
                    TelephonyManager.CALL_STATE_IDLE->{
                        onStart()
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK->{
                        onStop()
                    }
                }
                super.onCallStateChanged(state, phoneNumber)
            }
        },PhoneStateListener.LISTEN_CALL_STATE)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
//        audioManager?.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent != null) {
            val action = intent.action
            val command = intent.getStringExtra(CMD_NAME)
            val position = intent.getIntExtra("position", 0)
            if (ACTION_CMD == action && !TextUtils.isEmpty(command)) {
                when (command) {
                    CMD_INIT -> {
                        countDown.setInterval(50)
                            .setListener(this)
                        notification()
                    }
                    CMD_PLAY -> {
                        AudioPlay.getInstance()
                            .setPlayStateListener(this)
                            .setDataCaptureListener(this)
                        AudioPlay.getInstance()
                            .play(position)
                    }
                    CMD_MODE -> {
                        AudioPlay.getInstance()
                            .setMode()
                        notification()
                    }
                    CMD_PREV -> AudioPlay.getInstance()
                        .prev()
                    CMD_START -> {
                        onStart()
                    }
                    CMD_NEXT -> AudioPlay.getInstance()
                        .next()
                    CMD_STOP -> {
                        onStop()
                    }
                    CMD_CLOSE -> {
                        AudioPlay.getInstance().stop()
                        AudioPlay.getInstance().release()
                        notification()
                        FJNotification.getInstance()
                            .clear(NOTIFICATION_ID)
                    }
                    CMD_PRAISE -> {
                        val blog = AudioPlay.getInstance().data
                        if (blog != null) {
                            blog?.praises = FOLLOW - blog.praises
                            if (blog?.praises == FOLLOW) {
                                blog.praiseNum += 1
                            } else {
                                blog.praiseNum -= 1
                            }
                            AudioPlay.getInstance().data.praises = blog?.praises!!
                            notification()
                            blogVM?.praiseAdd(blog, object : OnPraiseListener {
                                override fun onPraise(feed: Feed) {
                                    AudioPlay.getInstance().mData?.forEachIndexed { index, it ->
                                        if (feed.id == it.id) {
                                            AudioPlay.getInstance().mData[index].praises =
                                                feed.praises
                                            notification()
                                            return@forEachIndexed
                                        }
                                    }
                                }
                            })
                        }
                    }
                    CMD_CHANNEL -> FJEvent.get()
                        .with("channel")
                        .post("channel")
                    CMD_FOLLOW -> {
                        val blog = AudioPlay.getInstance().data
                        if (blog != null) {
                            if (blog.uid == Resource.uid) {
                                FJToast.txt("不能关注自己!")
                            } else {
                                AudioPlay.getInstance().data.follows = FOLLOW
                                notification()
                                userVM?.followAdd(blog.uid, FOLLOW, object : OnFollowListener {
                                    override fun follow(
                                        status: Int,
                                        e: NetException?
                                    ) {
                                        if (e != null) {
                                            FJToast.txt(e.message)
                                            return
                                        }
                                        AudioPlay.getInstance().mData?.forEachIndexed { index, it ->
                                            if (it.id == blog.id) {
                                                AudioPlay.getInstance().mData[index].follows =
                                                    status
                                                notification()
                                                return@forEachIndexed
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        return START_STICKY
    }

    private fun onStart(){
        AudioPlay.getInstance()
            .start()
        notification()
    }

    private fun onStop(){
        AudioPlay.getInstance()
            .stop()
        notification()
    }

    private fun notification() {
        notificationManager = FJNotification.getInstance().create()
            .setId(NOTIFICATION_ID)
            .setService(this)
//            .setTitle("音乐世界")
//            .setContent("这个世界很美好!")
            .setIsClear(true)
            .setIsRing(true)
            .setLights(true)
            .setIcon(R.mipmap.unaudio)
            .setVibrate(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
//        .setView(remoteViews)
            .setCustomContentView(customContentView())
            .setCustomBigContentView(setCustomBigContentView())
            .setCustomHeadsUpContentView(customContentView())
//            .setClass(PushReceiver::class.java)
            .build()
    }

    private fun setCustomHeadsUpContentView(): RemoteViews {
        if (remoteViewsHeadUp == null) {
            remoteViewsHeadUp =
                RemoteViews(AppUtil.getPackageName(), R.layout.audio_notification_headup)
        }
        return remoteViewsHeadUp!!
    }

    private fun setCustomBigContentView(): RemoteViews {
        if (remoteViewsBig == null) {
            remoteViewsBig = RemoteViews(AppUtil.getPackageName(), R.layout.audio_notification_big)
//            remoteViewsBig!!.setOnClickPendingIntent(
//                id.audioNotificationFollow,
//                getPendingIntent(CMD_FOLLOW)
//            )
            initView(remoteViewsBig)
        }
        updateRemoteViews(remoteViewsBig, 1)
        return remoteViewsBig!!
    }

    private fun customContentView(
    ): RemoteViews {
        if (remoteViews == null) {
            remoteViews = RemoteViews(
                AppUtil.getPackageName(), R.layout.audio_notification
            )
            initView(remoteViews)
        }
        updateRemoteViews(
            remoteViews, 0
        )
        return remoteViews!!
    }

    private fun initView(remoteView: RemoteViews?) {
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationFollow,
            getPendingIntent(CMD_FOLLOW)
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationChannel,
            getPendingIntent(CMD_CHANNEL)
        )
        remoteView!!.setOnClickPendingIntent(R.id.audioNotificationMode, getPendingIntent(CMD_MODE))
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationPrev,
            getPendingIntent(CMD_PREV)
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationNext,
            getPendingIntent(CMD_NEXT)
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationPlay,
            getPendingIntent(CMD_START)
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationFormat,
            getPendingIntent(CMD_FORMAT)
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationDuration, getPendingIntent(
                CMD_DURATION
            )
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationProgress, getPendingIntent(
                CMD_PROGRESS
            )
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationTime, getPendingIntent(
                CMD_TIME
            )
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationClose,
            getPendingIntent(CMD_CLOSE)
        )
        remoteView!!.setOnClickPendingIntent(
            R.id.audioNotificationPraise,
            getPendingIntent(CMD_PRAISE)
        )
    }

    private fun updateRemoteViews(
        remoteView: RemoteViews?,
        type: Int
    ) {
        val audioPlay = AudioPlay.getInstance()
        val data = audioPlay.data ?: return
        var cover = Resource.icon
        if (data != null) {
            var name = data.name
            if (TextUtils.isEmpty(name)) {
                name = AppUtil.getAppName()
            }
            if (!TextUtils.isEmpty(data.cover)) {
                cover = data.cover!!
            }
            val time = audioPlay.currentPosition() / 1000
            remoteView!!.setTextViewText(
                R.id.audioNotificationDuration, DateUtil.formatSeconds(data.duration / 1000)
            )
            remoteView!!.setTextViewText(
                R.id.audioNotificationTime, DateUtil.formatSeconds(time)
            )
            remoteView!!.setProgressBar(
                R.id.audioNotificationProgress, data.duration.toInt(), time, false
            )
            remoteView?.setTextViewText(R.id.audioNotificationName, name)
            remoteView?.setTextViewText(R.id.audioNotificationChannel, "# $name")
            var des = data.des
            if (TextUtils.isEmpty(des)) {
                des = data.name
            }
            remoteView?.setTextViewText(R.id.audioNotificationDes, des)
        } else {
            remoteView!!.setTextViewText(
                R.id.audioNotificationName, AppUtil.getAppName()
            )
            remoteView.setTextViewText(
                R.id.audioNotificationChannel, AppUtil.getAppName()
            )
        }
        remoteView.setImageViewResource(R.id.audioNotificationMode, MODE[audioPlay.mMode])
        LogUtil.e("-----------userVM:",userVM)
        userVM?.oneLine(if(audioPlay.isPlaying) 2 else 1,data?.id,data?.name)
        remoteView.setImageViewResource(
            R.id.audioNotificationPlay,
            if (audioPlay.isPlaying) R.mipmap.play_pause else com.fanji.android.play.R.mipmap.play
        )
        remoteView.setImageViewResource(
            R.id.audioNotificationPraise,
            if (data.praises == FOLLOW) R.mipmap.liked else R.mipmap.unlike
        )
        remoteView.setImageViewResource(
            R.id.audioNotificationFollow, if (data.follows == FOLLOW) R.mipmap.unmine else com.fanji.android.files.R.mipmap.follow
        )

//    remoteView.setImageViewUri(id.audioNotificationCover, Uri.parse("http://zd112.oss-cn-beijing.aliyuncs.com/img/IMG_20200424_183201.jpg"))
        var width = ScreenUtil.getRtScreenWidth(AppUtil.getApplicationContext())
        var height = 400
        if (type == 0) {
            width = 200
            height = 200
        }
        if (data != null && !TextUtils.isEmpty(data.getCoverUrl())) {
            FJImg.loadImage(FJImg.thumbAliOssUrl(data.getCoverUrl(), width, height), 0,
                object : IBitmapListener {
                    override fun onSuccess(bitmap: Bitmap): Boolean {
                        if (remoteView == null) return false
                        if (bitmap == null) {
                            remoteView.setImageViewResource(
                                R.id.audioNotificationCover,
                                R.mipmap.ic_launcher
                            )
                        } else {
                            remoteView.setImageViewBitmap(
                                R.id.audioNotificationCover, bitmap
                            )
                        }
                        return false
                    }

                    override fun onFailure(): Boolean {
                        if (remoteView == null) return false
                        remoteView.setImageViewResource(
                            R.id.audioNotificationCover,
                            R.mipmap.ic_launcher
                        )
                        return false
                    }
                })
        }
    }

    private fun getPendingIntent(cmd: String): PendingIntent {
        val intent = Intent(ACTION_CMD)
        intent.putExtra(CMD_NAME, cmd)
        // requestCode使用UUID处理携带的数据失效问题
        return PendingIntent.getService(
            this, UUID.randomUUID()
                .hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    inner class AudioServiceBinder : Binder() {
        val service: AudioService
            get() = this@AudioService
    }

    companion object {

        const val CMD_NAME = "CMD_NAME"
        const val ACTION_CMD = "com.android.sanskrit.AUDIO_ACTION_CMD"

        const val CMD_INIT = "cmdInit"

        const val CMD_START = "cmdStart"

        /**
         * 播放模式
         */
        const val CMD_MODE = "cmdMode"

        //  播放/暂停
        const val CMD_PLAY = "cmdPlay"

        //  播放上一首
        const val CMD_PREV = "cmdPrev"

        //  播放下一首
        const val CMD_NEXT = "cmdNext"

        //  停止服务
        const val CMD_STOP = "cmdStop"

        //  关闭
        const val CMD_CLOSE = "cmdClose" //关闭
        const val CMD_FORMAT = "cmdFormat" //内容格式
        const val CMD_DURATION = "cmdDuration"//时间长度
        const val CMD_PROGRESS = "cmdProgress"//进度
        const val CMD_TIME = "cmdTime"//进度时间
        const val CMD_CHANNEL = "cmdChannel" //频道
        const val CMD_PRAISE = "cmdPraise" //点赞
        const val CMD_FOLLOW = "cmdFollow"//关注

        val MODE = listOf(
            R.mipmap.play_loop,
            R.mipmap.play_loop_single, R.mipmap.play_sequence,
            R.mipmap.play_random, R.mipmap.play_single
        )

        //  开启服务
        fun startAudioService() {
            val context: Context = AppUtil.getApplicationContext()
            val intent = Intent(context, AudioService::class.java)
            context.startService(intent)
        }

        //  停止服务
        fun stopAudioService() {
            val context: Context = AppUtil.getApplicationContext()
            val intent = Intent(context, AudioService::class.java)
            context.stopService(intent)
        }

        //  绑定服务
        fun bindAudioService(
            context: Context,
            conn: ServiceConnection?,
            flags: Int
        ) {
            val intent = Intent()
            intent.setClass(context, AudioService::class.java)
            context.bindService(intent, conn!!, flags)
        }

        //  通过该方法发出命令
        fun startAudioCommand(
            cmd: String,
            position: Int = 0,
            data: ArrayList<Feed>? = null
        ) {
            val context: Context = AppUtil.getApplicationContext()
            val intent = Intent(context, AudioService::class.java)
            intent.action = ACTION_CMD
            if (data != null) {
                AudioPlay.getInstance()
                    .setData(data)
//        intent.putExtra("data", data)
            }
            intent.putExtra("position", position)
            intent.putExtra(CMD_NAME, cmd)
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onFinish() {
        LogUtil.e("finish")
    }

    override fun onPause(duration: Long) {
    }

    override fun onTick(duration: Long) {
//    notification(true)
        FJEvent.get()
            .with(AUDIO_PROGRESS, Int::class.java)
            .post(
                AudioPlay.getInstance()
                    .currentPosition()
            )
    }

    override fun onStateChange(state: AudioPlay.PlayState) {
        FJEvent.get().with(AUDIO_STATE)
            .post(state)
        when (state) {
            AudioPlay.PlayState.STATE_INITIALIZING -> {
                countDown.stop()
            }
            AudioPlay.PlayState.STATE_PLAYING -> {
                countDown.start()
                notification()
            }
            AudioPlay.PlayState.STATE_RESUME -> {
                countDown.start()
                notification()
            }
            AudioPlay.PlayState.STATE_PAUSE -> {
                countDown.pause()
                notification()
            }
            AudioPlay.PlayState.STATE_IDLE -> {
                countDown.stop()

            }
        }
    }

    override fun onFftDataCapture(
        visualizer: Visualizer?,
        fft: ByteArray,
        samplingRate: Int
    ) {
        FJEvent.get()
            .with(AUDIO_CAPTURE)
            .post(fft)
    }

    override fun onWaveFormDataCapture(
        visualizer: Visualizer?,
        waveform: ByteArray,
        samplingRate: Int
    ) {
        FJEvent.get()
            .with(AUDIO_CAPTURE)
            .post(waveform)
    }

    override fun onAudioFocusChange(focusChange: Int) {
    }
}

const val AUDIO_STATE = "audioState"
const val AUDIO_CAPTURE = "audioCapture"
const val AUDIO_PROGRESS = "audioProgress"