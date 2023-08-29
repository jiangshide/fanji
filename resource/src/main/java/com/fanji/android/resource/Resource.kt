package com.fanji.android.resource

//import com.fanji.android.aliyun.analytics.Analytics
import com.fanji.android.net.Net
import com.fanji.android.resource.vm.feed.data.Format
import com.fanji.android.resource.vm.publish.data.Publish
import com.fanji.android.resource.vm.user.data.Ads
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.util.LogUtil
import com.fanji.android.util.SPUtil
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX

/**
 * created by jiangshide on 4/7/21.
 * email:18311271399@163.com
 */
object Resource {

    private val IS_FIRST = "isFirst"

    var UID = "uid"
    var USER = "user"
    var USERS = "users"
    var NAME = "name"
    var ICON = "icon"
    var PUBLISH = "publish"
    var FORMAT = "format"
    var ADS = "ads"

    var CHAT_VALIDATE_TYPE = "chatValidateType"

    var TAB_INDEX = 0

//    var chatType: Int
//        get() = SPUtil.getInt(CHAT_VALIDATE_TYPE, V2TIM_FRIEND_ALLOW_ANY)
//        set(type) {
//            SPUtil.putInt(CHAT_VALIDATE_TYPE, type)
//        }

    var format: MutableList<Format>?
        get() = SPUtil.getList(FORMAT, Format::class.java)
        set(format) {
            SPUtil.put(FORMAT, format)
        }

    var uid: Long?
        get() = SPUtil.getLong(UID)
        set(uid) {
            SPUtil.putLong(UID, uid!!)
        }

    var user: User?
        get() = SPUtil.get(USER, User::class.java)
        set(user) {
            uid = user!!.id
            icon = user!!.icon!!
            name = user.name!!
            SPUtil.put(USER, user)
//            Analytics.updateUserAccount(name, "$uid")
        }

    var users: ArrayList<User>?
        get() = SPUtil.getList(USERS, User::class.java) as ArrayList<User>?
        set(users) {
            SPUtil.put(USERS, users)
        }

    var name: String
        get() = SPUtil.getString(NAME)
        set(name) {
            LogUtil.e("name:", name)
            SPUtil.putString(NAME, name)
        }

    var icon: String
        get() = SPUtil.getString(ICON)
        set(icon) {
            LogUtil.e("icon", icon)
            SPUtil.putString(ICON, icon)
        }

    var first: Boolean
        get() = SPUtil.getBoolean(IS_FIRST, true)
        set(first) {
            SPUtil.putBoolean(IS_FIRST, first)
        }

    var publish: Publish?
        get() = SPUtil.get(PUBLISH, Publish::class.java)
        set(publish) {
            SPUtil.put(PUBLISH, publish)
        }

    var ads: Ads?
        get() = SPUtil.get(ADS, Ads::class.java)
        set(ads) {
            SPUtil.put(ADS, ads)
        }

    fun clearPublish() {
        SPUtil.clear(PUBLISH)
    }

    fun clearUser() {
//        Analytics.updateUserAccount()
        SPUtil.clear(UID)
        SPUtil.clear(USER)
        SPUtil.clear(Net.TOKEN)
        clearPublish()
    }
}

const val MEN = 1
const val WOMEN = 2

const val NAME_SIZE = 8

const val BLOG_FOLLOW = 1//关注
const val BLOG_RECOMMEND = 2//推荐
const val BLOG_HOT = 3//热门
const val BLOG_NEW = 4//最新
const val BLOG_CIRCLE = 5//圈子
const val BLOG_CHANNEL = 6//频道
const val BLOG_MY = 7//我的
const val BLOG_RECYCLER = 8//记忆箱

const val REFRESH_MIN = "refreshMine"
const val REFRESH_PROFILE = "refreshProfile"
const val REFRESH_MEMORY = "refreshMemory"
const val REFRESH_MINE = "refreshMine"

const val FINISH_VALIDATE = "finishValidate"

const val FINISH_USERCODE = "finishUserCode"

//审核状态:0~未审核,1~审核中,2~审核通过,3移到回忆箱,-1~审核拒绝,-2~禁言,-3~关闭/折叠
const val AUDIT = 0
const val AUDIT_UNDER = 1
const val AUDIT_PASS = 2
const val RECYCLE = 3
const val AUDIT_REJECTED = -1
const val FORBIDDEN_WORDS = -2
const val FOLD = -3

const val CHANNEL_PRIVATE = 1//私有频道
const val CHANNEL_COMMON = 2//共有频道

const val CHANNEL_FOLLOW_CANCEL = -1
const val CHANNEL_FOLLOW_RECOMMEND = 0//推荐关注
const val CHANNEL_FOLLOW = 1 //关注

const val CONTENT_FROM_USER = 1//来自用户
const val CONTENT_FROM_CHANNEL = 2//来自频道
const val CONTENT_FROM_BLOG = 3//来自动态
const val CONTENT_FROM_COMMENT = 4//来自评论

const val BLOG_FOLLOW_FROM_FIND = 1//首页动态发现~关注

const val UNFOLLOW = 0
const val FOLLOW = 1
const val ESPECIALLY = 2

const val ON = 0
const val OFF = -1

const val VIDEO_COVER = "?x-oss-process=video.json/snapshot,t_10000,m_fast/auto-orient,1"//
const val IMG = "?x-oss-process=image/resize,m_fill,h_100,w_200"//缩略图
const val ROTATION = "?x-oss-process=image/resize,w_100/auto-orient,0"//旋转

const val SHARE_WX_FRIEND = SendMessageToWX.Req.WXSceneSession//分享到微信好友
const val SHARE_WX_TIMELINE = SendMessageToWX.Req.WXSceneTimeline//分享到微信朋友圈
const val SHARE_WX_FAVORITE = SendMessageToWX.Req.WXSceneFavorite//收藏到微信
const val SHARE_FJ_FRIEND = 10//分享到站内好友


const val OFFLINE = -1//离线
const val ONLINE = 1//在线
const val ONLINE_AUDIO = 2//在线听音乐
const val ONLINE_MOVE = 3//在线看电影
const val ONLINE_GAME = 4//在线玩游戏
const val ONLINE_STUDY = 5//在线学习
const val ONLINE_WORK = 6//在线工作

const val COUNTDOWN_TIME: Int = 60

const val USER_NOT_EXIST = -311

//the active
const val ACTIVE_SPLASH = 1
const val ACTIVE_LOGIN = 2
const val ACTIVE_INVITE = 3
const val ACTIVE_PUBLISH_VIDEO = 4
const val ACTIVE_PUBLISH_AUDIO = 5
const val ACTIVE_PUBLISH_IMG = 6
const val ACTIVE_PUBLISH_DOC = 7

const val TAB_BOTTOM_SCROLL = "tabBottomScroll"