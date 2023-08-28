package com.fanji.android.resource.vm.feed.data

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.fanji.android.img.FJImg
import com.fanji.android.resource.MEN
import com.fanji.android.resource.ONLINE
import com.fanji.android.resource.ONLINE_AUDIO
import com.fanji.android.resource.*
import com.fanji.android.resource.R
import com.fanji.android.resource.WOMEN
import com.fanji.android.resource.audio.AudioPlay
import com.fanji.android.resource.audio.AudioService
import com.fanji.android.resource.vm.feed.FeedVM
import com.fanji.android.resource.vm.feed.OnBlogListener
import com.fanji.android.resource.vm.publish.data.File
import com.fanji.android.util.*
import com.fanji.android.util.data.*
import com.fanji.android.ui.FJCircleImg

/**
 * created by jiangshide on 4/7/21.
 * email:18311271399@163.com
 */
data class Feed(
    var id: Long = 0,
    var uid: Long = 0,
    var online: Int = 0,//在线状态:1~在线,2~音乐,3~电影,4~游戏,5~学习,6~工作,-1~离线
    var onlineId: Long = 0,//在线状态资源ID
    var onlineName: String? = "",//在线状态资源名称
    var channelId: Long = 0,//频道ID
    var positionId: Long = 0,//当前位置ID
    var title: String? = "",//动态名称
    var des: String? = "",//动态描述
    var latitude: Double = 0.0,//精度
    var longitude: Double = 0.0,//纬度
    var locationType: String? = "",//定位类型
    var country: String? = "",//国家
    var city: String? = "",//城市
    var position: String? = "",//位置
    var address: String? = "",//详细位置
    var cityCode: String? = "",//城市编码
    var timeCone: String? = "",//时区
    var tag: String? = "",//标签
    var status: Int = 0,//状态:0~未审核,1~审核中,2~审核通过,-1~移到回忆箱,-2~审核拒绝,-3～禁言，-4~关闭/折叠,-5~被投诉
    var reason: String? = "",//原由
    var official: Int = 0,//官方推荐:-1~取消推荐,0~未推荐,1~推荐,2~特别推荐
    var url: String? = "",//文件Url
    var cover: String? = "",//封面
    var name: String? = "",//文件名称
    var sufix: String? = "",//文件名后缀
    var format: Int = 0,//内容格式:0:图片,1:音频,2:视频,3:文档,4:web,5:VR
    var duration: Long = 0,//内容时长
    var width: Int = 0,//内容宽
    var height: Int = 0,//内容高
    var size: Long = 0,//内容尺寸
    var rotate: Int = 0,//角度旋转
    var bitrate: Int = 0,//采用率
    var sampleRate: Int = 0,//频率
    var level: Int = 0,//质量:0~标准
    var mode: Int = 0,//模式
    var wave: String? = "",//频谱
    var lrcZh: String? = "",//字幕~中文
    var lrcEn: String? = "",//字母~英文
    var source: Int = 0,//创作类型:0~原创,1~其它
    var date: String? = "",//创建时间

    var icon: String? = "",//用户头像
    var nick: String? = "",//用户昵称
    var sex: Int = 0,//用户性别
    var age: Int = 0,//用户年龄
    var zodiac: String? = "",//用户生肖
    var ucity: String? = "",//用户出生城市

    var remark: String? = "",//备注名称

    var channel: String? = "",//频道名称

    var praiseNum: Int = 0,//点赞次数
    var viewNum: Int = 0,//预览次数
    var shareNum: Int = 0,//共享次数
    var commentNum: Int = 0,//评论次数

    //点赞
    var praises: Int = 0,//点赞状态
    //举报
    var reportr: String? = "",//举报原因
    //关注
    var follows: Int = 0,//关注状态
    //收藏
    var collections: Int = 0,//收藏状态
    //置顶
    var tops: Int = 0,//置顶状态
    //推荐
    var recommends: Int = 0,//推荐状态

    var urls: MutableList<File>? = null,
    var comments: MutableList<Comment>? = null,
    val ats: MutableList<At>? = null,

    var playStatus: Int = RELEASE,
    var index: Int = 0,
    var refresh: String? = "",
    var isSync: Boolean = true,
    var content: String? = "",
    var style: Style? = null,
    var path: String? = null,
    var collectionNum: Int = 0,//收藏次数
    var channelCover: String = "",
    var dynamicCover: String? = ""//动态封面
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(File),
        parcel.createTypedArrayList(Comment),
        parcel.createTypedArrayList(At),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int
    ) {
        parcel.writeLong(id)
        parcel.writeLong(uid)
        parcel.writeInt(online)
        parcel.writeLong(onlineId)
        parcel.writeString(onlineName)
        parcel.writeLong(channelId)
        parcel.writeLong(positionId)
        parcel.writeString(title)
        parcel.writeString(des)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(locationType)
        parcel.writeString(country)
        parcel.writeString(city)
        parcel.writeString(position)
        parcel.writeString(address)
        parcel.writeString(cityCode)
        parcel.writeString(timeCone)
        parcel.writeString(tag)
        parcel.writeInt(status)
        parcel.writeString(reason)
        parcel.writeInt(official)
        parcel.writeString(url)
        parcel.writeString(cover)
        parcel.writeString(name)
        parcel.writeString(sufix)
        parcel.writeInt(format)
        parcel.writeLong(duration)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeLong(size)
        parcel.writeInt(rotate)
        parcel.writeInt(bitrate)
        parcel.writeInt(sampleRate)
        parcel.writeInt(level)
        parcel.writeInt(mode)
        parcel.writeString(wave)
        parcel.writeString(lrcZh)
        parcel.writeString(lrcEn)
        parcel.writeInt(source)
        parcel.writeString(date)
        parcel.writeString(icon)
        parcel.writeString(nick)
        parcel.writeInt(sex)
        parcel.writeInt(age)
        parcel.writeString(zodiac)
        parcel.writeString(ucity)
        parcel.writeString(remark)
        parcel.writeString(channel)
        parcel.writeInt(praiseNum)
        parcel.writeInt(viewNum)
        parcel.writeInt(shareNum)
        parcel.writeInt(commentNum)
        parcel.writeInt(praises)
        parcel.writeString(reportr)
        parcel.writeInt(follows)
        parcel.writeInt(collections)
        parcel.writeInt(tops)
        parcel.writeInt(recommends)
        parcel.writeInt(playStatus)
        parcel.writeInt(index)
        parcel.writeString(refresh)
        parcel.writeByte(if (isSync) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Blog(id=$id, uid=$uid, channelId=$channelId, positionId=$positionId, title=$title, des=$des, latitude=$latitude, longitude=$longitude, locationType=$locationType, country=$country, city=$city, position=$position, address=$address, cityCode=$cityCode, timeCone=$timeCone, tag=$tag, status=$status, reason=$reason, official=$official, url=$url, cover=$cover, name=$name, sufix=$sufix, format=$format, duration=$duration, width=$width, height=$height, size=$size, rotate=$rotate, bitrate=$bitrate, sampleRate=$sampleRate, level=$level, mode=$mode, wave=$wave, lrcZh=$lrcZh, lrcEn=$lrcEn, source=$source, date=$date, icon=$icon, nick=$nick, sex=$sex, age=$age, zodiac=$zodiac, ucity=$ucity, remark=$remark, channel=$channel, praiseNum=$praiseNum, viewNum=$viewNum, shareNum=$shareNum, commentNum=$commentNum, praises=$praises, reportr=$reportr, follows=$follows, collections=$collections, tops=$tops, recommends=$recommends, urls=$urls, comments=$comments, playStatus=$playStatus, index=$index, refresh=$refresh, isSync=$isSync)"
    }

    companion object CREATOR : Parcelable.Creator<Feed> {
        override fun createFromParcel(parcel: Parcel): Feed {
            return Feed(parcel)
        }

        override fun newArray(size: Int): Array<Feed?> {
            return arrayOfNulls(size)
        }
    }

    fun setSex(text: TextView) {
        val context = AppUtil.getApplicationContext().applicationContext
        when (sex) {
            MEN -> {
//                text.setDrawableLeft(R.mipmap.sex_man)
            }
            WOMEN -> {
//                text.setDrawableLeft(R.mipmap.sex_women)
            }
        }
        var str = ""
        if (age > 0) {
            str = context.getString(R.string.dot) + " $age"
        }
        if (!TextUtils.isEmpty(city)) {
            str += context.getString(R.string.dot) + " $city"
        }
        text.text = str
        StringUtil.setDot(text, context.getString(R.string.dot))
    }

    fun setImg(
        activity: Activity?,
        formatImg: ImageView,
        sizeTxt: TextView? = null,
        coverImg: ImageView? = null,
        channelCover: String? = "",
        isRandom: Boolean=true
    ) {
        var url = url
        var height = ScreenUtil.getRtScreenHeight(activity) / 3
        when (format) {
            com.fanji.android.util.data.IMG -> {
                if (TextUtils.isEmpty(url) && urls != null && urls!!.size > 0) {
                    height = if (isRandom) urls!![0].randomH else urls!![0].height
                    url = urls!![0].url
                    if (sizeTxt != null) {
                        if (urls!!.size > 1) {
                            sizeTxt.text = "1/${urls!!.size}"
                        }
                    }
                }
                formatImg.setImageResource(com.fanji.android.ui.R.mipmap.img)
            }
            AUDIO -> {
                url = getCoverUrl(channelCover)
                formatImg.setImageResource(com.fanji.android.ui.R.mipmap.audio)
            }
            VIDEO -> {
                if (!TextUtils.isEmpty(cover)) {
                    url = cover
                }
                if (TextUtils.isEmpty(url) && urls != null && urls!!.size > 0) {
                    url = urls!![0].url
                }
                formatImg.setImageResource(com.fanji.android.ui.R.mipmap.video)
            }
            DOC -> {

            }
            WEB -> {

            }
            VR -> {

            }
        }
        if (TextUtils.isEmpty(url)) {
            url = cover
        }
        if (coverImg != null) {
            val layoutParams =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    height
                )
            coverImg?.layoutParams = layoutParams
            FJImg.loadImageRound(url, coverImg, 10,R.mipmap.splash)
        }
    }

    fun getCoverUrl(cv: String? = ""): String? {
        var urlStr = ""
        if (urls != null && urls!!.size > 0) {
            urls?.forEach {
                if (!TextUtils.isEmpty(it.url) && FileUtil.isImg(it.url)) {
                    urlStr = it.url!!
                } else if (!TextUtils.isEmpty(it.cover)) {
                    urlStr = it.cover!!
                }
            }
        }
        if (TextUtils.isEmpty(urlStr) && !TextUtils.isEmpty(url) && FileUtil.isImg(url)) {
            urlStr = url!!
        }
        if (TextUtils.isEmpty(urlStr) && !TextUtils.isEmpty(cover)) {
            urlStr = cover!!
        }

        if (TextUtils.isEmpty(urlStr)) {
            urlStr = channelCover
        }

        if (TextUtils.isEmpty(urlStr) && !TextUtils.isEmpty(cv)) {
            urlStr = cv!!
        }
        if (TextUtils.isEmpty(urlStr)) {
            urlStr = icon!!
        }
        return urlStr
    }

    fun showNum(
        txtView: TextView,
        num: Int,
        praises: Int = -2
    ) {
        if (praises != -2) {
            txtView.isSelected = praises == 1
        }
        when {
            num >= 10000 -> {
                txtView?.text = "${num / 10000.0}万"
            }
            num >= 1000 -> {
                txtView?.text = "${num / 1000.0}千"
            }
            num > 0 -> {
                txtView?.text = "$num"
            }
            else -> {
//        txtView?.text = ""
            }
        }
    }

    fun getWave(wave: String): List<Int> {
        if (TextUtils.isEmpty(wave)) return emptyList()
        val byteArray = EncryptUtil.base64Decode(wave)
        val list = List(byteArray.size) { index ->
            val n = byteArray[index].toInt()
            if (n < 0) {
                return@List n + 256
            }
            return@List n
        }
        return list
    }

    fun onLine(
        blogVM: FeedVM,
        onlineIcon: FJCircleImg,
        onlineStateIcon: ImageView?,
        onlineDes: TextView?
    ) {
        onlineIcon?.loadCircle(icon, R.mipmap.default_user)
        when (online) {
            ONLINE -> {
                onlineDes?.visibility = View.GONE
                onlineStateIcon?.visibility = View.GONE
                onlineIcon?.borderColor = onlineIcon?.resources!!.getColor(com.fanji.android.ui.R.color.blue)
            }
            ONLINE_AUDIO -> {
                onlineDes?.text = onlineName
                onlineDes?.setOnClickListener {
                    blogVM?.blogId(onlineId, object : OnBlogListener {
                        override fun onBlog(blog: Feed?, e: Exception?) {
                            if (blog == null || e != null) return
                            var datas = AudioPlay.getInstance().mData
                            if (datas == null || datas.size == 0) {
                                datas = ArrayList()
                                datas.add(blog)
                                AudioService.startAudioCommand(AudioService.CMD_INIT, data = datas)
                                AudioService.startAudioCommand(AudioService.CMD_PLAY, 0)
                            } else {
                                AudioPlay.getInstance().data = blog
                                AudioService.startAudioCommand(AudioService.CMD_PLAY)
                            }
                        }
                    })
                }
                onlineDes?.visibility = View.VISIBLE
                onlineStateIcon?.visibility = View.VISIBLE
                onlineIcon?.borderColor = onlineIcon?.resources!!.getColor(com.fanji.android.ui.R.color.blueLight)
            }
            else -> {
                onlineDes?.visibility = View.GONE
                onlineStateIcon?.visibility = View.GONE
                onlineIcon?.borderColor =
                    onlineIcon?.resources!!.getColor(com.fanji.android.ui.R.color.translucent3)
            }
        }
    }

    fun setDate(text: TextView) {
        text.text = DateUtil.showTimeAhead(DateUtil.stringToLong(date))
    }

    fun getFiles(): List<File> {
        val data = ArrayList<File>()
        data.add(getFile())
        if (urls != null) {
            data.addAll(urls!!)
        }
        return data
    }

    fun getFile(): File {
        return File(
            url = url,
            cover = cover,
            name = name,
            sufix = sufix,
            format = format,
            duration = duration,
            width = width,
            height = height,
            size = size,
            rotate = rotate,
            bitrate = bitrate,
            sampleRate = sampleRate,
            level = level,
            mode = mode,
            wave = wave,
            lrcEs = lrcEn,
            lrcZh = lrcZh,
            source = source
        )
    }
}

interface OnSyncListener {
    fun syncResult(
        status: Int,
        url: String
    )
}

interface OnSyncStatusListener {
    fun syncResult(
        status: Int,
        blog: Feed? = null
    )
}

const val RELEASE = 0
const val PLAYING = 1
const val PAUSE = 2
const val RESUME = 3