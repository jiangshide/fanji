package com.fanji.android.resource.vm.feed

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.fanji.android.net.Net
import com.fanji.android.net.exception.NetException
import com.fanji.android.net.observer.BaseObserver
import com.fanji.android.net.transformer.CommonTransformer
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.BLOG_FOLLOW_FROM_FIND
import com.fanji.android.resource.Resource
import com.fanji.android.resource.base.BaseVM
import com.fanji.android.resource.vm.channel.data.Word
import com.fanji.android.resource.vm.feed.data.Comment
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.resource.vm.feed.data.Format
import com.fanji.android.resource.vm.feed.data.LrcLInfo
import com.fanji.android.resource.vm.feed.data.Total
import com.fanji.android.resource.vm.feed.remote.FeedRemote
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.util.LogUtil
import com.fanji.android.util.ScreenUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import kotlin.random.Random

/**
 * created by jiangshide on 4/7/21.
 * email:18311271399@163.com
 */
class FeedVM : BaseVM() {
    private val iBlog: FeedRemote = Net.createService(FeedRemote::class.java)
    var collection: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var top: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var followAdd: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var findBlogFollow: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var shareAdd: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var share: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var viewAdd: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var format: MutableLiveData<LiveResult<MutableList<Format>>> = MutableLiveData()
    var collectionBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var cityBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var followBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var recommendBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var findBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var praiseBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var browseBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var channelBlog: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var update: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var remove: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var commentAdd: MutableLiveData<LiveResult<Total>> = MutableLiveData()
    var comment: MutableLiveData<LiveResult<MutableList<Comment>>> = MutableLiveData()
    var praiseCommentAdd: MutableLiveData<LiveResult<Comment>> = MutableLiveData()
    var blogFormat: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()
    var blogId: MutableLiveData<LiveResult<Feed>> = MutableLiveData()
    var searchLrc: MutableLiveData<LiveResult<LrcLInfo>> = MutableLiveData()
    var lrcUpdate: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var historyAdd: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var historyList: MutableLiveData<LiveResult<MutableList<Feed>>> = MutableLiveData()

    fun historyList(
        uid: Long? = Resource.uid,
        source: Int? = 0,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.historyList(uid = uid, source = source, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    historyList.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    historyList.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun historyAdd(
        contentId: Long = 0,
        num: Int? = 0,
        source: Int? = 0
    ) {
        iBlog.historyAdd(contentId = contentId, num = num, source = source)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    historyAdd.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    historyAdd.postValue(LiveResult.error(e))
                }
            })
    }

    fun lrcUpdate(
        id: Long = 0,
        name: String? = "",
        lrcZh: String? = "",
        lrcEs: String? = ""
    ) {
        iBlog.lrcUpdate(id, name, lrcZh = lrcZh, lrcEs = lrcEs)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    lrcUpdate.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    lrcUpdate.postValue(LiveResult.error(e))
                }
            })
    }

    fun searchLrc(
        uid: Long = 0,
        name: String,
        author: String? = "",
        language: Int = 0
    ) {
        iBlog.searchLrc(uid, name = name, author = author, language = language)
            .compose(CommonTransformer<Response<RespData<LrcLInfo>>, LrcLInfo>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<LrcLInfo>() {
                override fun onNext(t: LrcLInfo) {
                    super.onNext(t)
                    searchLrc.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    searchLrc.postValue(LiveResult.error(e))
                }
            })
    }

    fun blogId(id: Long, listener: OnBlogListener? = null) {
        iBlog.blogId(id)
            .compose(CommonTransformer<Response<RespData<Feed>>, Feed>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Feed>() {
                override fun onNext(t: Feed) {
                    super.onNext(t)
                    if (listener != null) {
                        listener.onBlog(t, null)
                    } else {
                        blogId.postValue(LiveResult.success(t))
                    }
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (listener != null) {
                        listener.onBlog(null, e)
                    } else {
                        blogId.postValue(LiveResult.error(e))
                    }
                }
            })
    }

    fun blogFormat(
        format: Int = 0,
        title: String = "",
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.blogFormat(format = format, title = title, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    blogFormat.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    blogFormat.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun commentAdd(
        comment: Comment,
        listener: OnCommentUidAddListener? = null
    ) {
        iBlog.commentAdd(
            contentId = comment.contentId,
            content = comment.content,
            at = comment?.at,
            topic = comment?.content,
            urls = comment.urls
        )
            .compose(CommonTransformer<Response<RespData<Total>>, Total>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Total>() {
                override fun onNext(t: Total) {
                    super.onNext(t)
//                    commentAdd.postValue(LiveResult.success(t))
                    listener?.onCommentAdd(t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
//                    commentAdd.postValue(LiveResult.error(e))
                    listener?.onCommentAdd(err = e)
                }
            })
    }

    fun comment(
        blogId: Long,
        listener: OnCommentsListener? = null,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.comment(contentId = blogId, page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<Comment>>>, MutableList<Comment>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Comment>>() {
                override fun onNext(t: MutableList<Comment>) {
                    super.onNext(t)
                    listener?.onComments(data = t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    listener?.onComments(err = e)
                }
            })
    }

    fun praiseCommentAdd(
        comment: Comment,
        listener: OnCommentPraiseListener
    ) {
        if (comment.praises == 1) {
            comment.praises = 0
            comment.praiseNum -= 1
        } else {
            comment.praises = 1
            comment.praiseNum += 1
        }
        iBlog.praiseCommentAdd(id = comment.id, commentId = comment.id, status = comment.praises)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
//            praiseCommentAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (comment.praises == 1) {
                        comment.praises = 0
                        comment.praiseNum -= 1
                    } else {
                        comment.praises = 1
                        comment.praiseNum += 1
                    }
//            praiseCommentAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }
            })
    }

    fun commentUidAdd(
        comment: Comment,
        listener: OnCommentUidAddListener
    ) {
        iBlog.commentUidAdd(
            commentId = comment.id, fromUid = comment.fromUid, contentId = comment.contentId,
            content = comment.content, at = comment?.at,
            topic = comment?.content,
            urls = comment.urls, reply = comment.reply
        )
            .compose(CommonTransformer<Response<RespData<Total>>, Total>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Total>() {
                override fun onNext(t: Total) {
                    super.onNext(t)
//            commentUidAdd.postValue(LiveResult.success(comment))
                    listener?.onCommentAdd(total = t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
//            commentUidAdd.postValue(LiveResult.error(e))
                    listener?.onCommentAdd(err = e)
                }
            })
    }

    fun commentUid(
        commentId: Long,
        fromUid: Long,
        contentId: Long,
        listener: OnCommentsListener,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.commentUid(
            commentId = commentId, fromUid = fromUid, contentId = contentId, page = page,
            pageSize = pageSize
        )
            .compose(
                CommonTransformer<Response<RespData<MutableList<Comment>>>, MutableList<Comment>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Comment>>() {
                override fun onNext(t: MutableList<Comment>) {
                    super.onNext(t)
                    listener?.onComments(data = t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    listener?.onComments(err = e)
                }
            })
    }

    fun praiseCommentUidAdd(
        comment: Comment,
        listener: OnCommentPraiseListener
    ) {
        if (comment.praises == 1) {
            comment.praises = 0
            comment.praiseNum -= 1
        } else {
            comment.praises = 1
            comment.praiseNum += 1
        }
        iBlog.praiseCommentUidAdd(
            id = comment.id,
            commentUidId = comment.id,
            status = comment.praises
        )
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
//            praiseCommentUidAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (comment.praises == 1) {
                        comment.praises = 0
                        comment.praiseNum -= 1
                    } else {
                        comment.praises = 1
                        comment.praiseNum += 1
                    }
//            praiseCommentUidAdd.postValue(LiveResult.success(comment))
                    listener?.onPraise(comment)
                }
            })
    }

    fun topAdd(
        blog: Feed
    ) {
        iBlog.topAdd(blog.id, blog.tops)
            .compose(CommonTransformer<Response<RespData<Any>>, Any>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Any>() {
                override fun onNext(t: Any) {
                    super.onNext(t)
                    top.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    top.postValue(LiveResult.error(e))
                }
            })
    }

    fun collectionAdd(
        blog: Feed,
        onBlogListener: OnBlogListener? = null
    ) {
        iBlog.collectionAdd(blog.id, blog.collections)
            .compose(CommonTransformer<Response<RespData<Any>>, Any>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Any>() {
                override fun onNext(t: Any) {
                    super.onNext(t)
                    if (onBlogListener != null) {
                        onBlogListener?.onBlog(blog)
                    } else {
                        collection.postValue(LiveResult.success(blog))
                    }
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (onBlogListener != null) {
                        onBlogListener?.onBlog(e = e)
                    } else {
                        collection.postValue(LiveResult.error(e))
                    }
                }
            })
    }

    fun followAdd(
        blog: Feed,
        from: Int = 0
    ) {
        iBlog.followAdd(uid = blog.uid, status = blog.follows)
            .compose(CommonTransformer<Response<RespData<Any>>, Any>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Any>() {
                override fun onNext(t: Any) {
                    super.onNext(t)
                    when (from) {
                        BLOG_FOLLOW_FROM_FIND -> {
                            findBlogFollow.postValue(LiveResult.success(blog))
                        }

                        else -> {
                            followAdd.postValue(LiveResult.success(blog))
                        }
                    }
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    when (from) {
                        BLOG_FOLLOW_FROM_FIND -> {
                            findBlogFollow.postValue(LiveResult.error(e))
                        }

                        else -> {
                            followAdd.postValue(LiveResult.error(e))
                        }
                    }
                }
            })
    }

    fun share(
        blogId: Long,
        status: Int? = 1,
        station: Int? = 0,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.share(blogId = blogId, status = status, station = station)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    share.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    share.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun shareAdd(
        blog: Feed,
        fromIds: ArrayList<Long>? = null,
        station: Int = 0
    ) {
        blog.shareNum += 1
        LogUtil.e("blog:", blog, " | fromIds:", fromIds, " | station:", station)
        iBlog.shareAdd(
            blogId = blog.id,
            status = 1,
            fromIds = fromIds.toString(),
            station = station
        )
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    shareAdd.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    blog.shareNum -= 1
                    shareAdd.postValue(LiveResult.success(blog))
                }
            })
    }

    fun viewAdd(
        blog: Feed,
        index: Int = 0
    ) {
        blog.viewNum += 1
        LogUtil.e("blog:", blog)
        iBlog.viewAdd(fromId = blog.uid, blogId = blog.id, num = index)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    viewAdd.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    blog.viewNum -= 1
                    viewAdd.postValue(LiveResult.success(blog))
                }
            })
    }

    fun praiseAdd(
        blog: Feed,
        listener: OnPraiseListener? = null
    ) {
        iBlog.praiseAdd(fromId = blog.uid, blogId = blog.id, status = blog.praises)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    blog.praiseNum = t
                    listener?.onPraise(blog)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e(e)
                    if (blog.praises == 1) {
                        blog.praises = 0
                        blog.praiseNum -= 1
                    } else {
                        blog.praises = 1
                        blog.praiseNum += 1
                    }
                    listener?.onPraise(blog)
                }
            })
    }

    fun format() {
        iBlog.format()
            .compose(CommonTransformer<Response<RespData<MutableList<Format>>>, MutableList<Format>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Format>>() {
                override fun onNext(t: MutableList<Format>) {
                    super.onNext(t)
                    format.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    format.postValue(LiveResult.error(e))
                }
            })
    }

    fun banner(
        name: String = "",
        listener: OnWordListener
    ) {
        iBlog.banner(name)
            .compose(CommonTransformer<Response<RespData<MutableList<Word>>>, MutableList<Word>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Word>>() {
                override fun onNext(t: MutableList<Word>) {
                    super.onNext(t)
//            banner.postValue(LiveResult.success(t))
                    listener?.onWords(data = t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
//            banner.postValue(LiveResult.error(e))
                    listener?.onWords(err = e)
                }
            })
    }

    fun delWord(name: String = "", listener: OnDelWordListener) {
        iBlog.delWord(name)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    listener?.onDelResult(t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    listener?.onDelResult(err = e)
                }
            })
    }

    fun collectionBlog(
        status: Int = 1,
        uid: Long? = Resource.uid,
        blogStatus: Int = 2,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.collectionBlog(status, uid, blogStatus, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    collectionBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    collectionBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun userBlog(
        status: Int = 2,
        uid: Long? = Resource.uid,
        fromUid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20,
        listener: VMListener<MutableList<Feed>>? = null
    ) {
        iBlog.userBlog(status, uid, fromUid = fromUid, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    listener?.onRes(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    listener?.onRes(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun cityBlog(
        status: Int = 2,
        mode: Int = 0,
        city: String? = Resource.user?.city,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.cityBlog(status, mode, city, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    cityBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    cityBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun followBlog(
        status: Int = 1,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.followBlog(status, uid, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    followBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    followBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun recommendBlog(
        format: Int? = -1,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ): FeedVM {
        iBlog.recommendBlog(format, uid, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    finishLoading()
                    recommendBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    finishLoading()
                    recommendBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
        return this
    }

    fun findBlog(
        status: Int = 2,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        context: Context? = null,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.findBlog(status, uid, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    findBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    findBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun praiseBlog(
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        context: Context? = null,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.praiseBlog(uid, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    praiseBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    praiseBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun browseBlog(
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        context: Context? = null,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.browseBlog(uid, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    if (context != null) {
                        t?.forEach {
                            it.urls?.forEach {
                                val height = ScreenUtil.getScreenHeight(context) / 3
                                it.randomH = height + Random.nextInt(100)
                                it.height = height
                            }
                        }
                    }
                    browseBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    browseBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun channelBlog(
        channelId: Long,
        sort: Int? = 1,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iBlog.channelBlog(channelId, sort, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Feed>>>, MutableList<Feed>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Feed>>() {
                override fun onNext(t: MutableList<Feed>) {
                    super.onNext(t)
                    channelBlog.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelBlog.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun update(
        blog: Feed
    ) {
        iBlog.update(blog.id, blog.status, blog.reason)
            .compose(
                CommonTransformer<Response<RespData<Int>>, Int>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    update.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    update.postValue(LiveResult.error(e))
                }
            })
    }

    fun remove(blog: Feed) {
        iBlog.remove(blog.id)
            .compose(
                CommonTransformer<Response<RespData<Int>>, Int>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    remove.postValue(LiveResult.success(blog))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    remove.postValue(LiveResult.error(e))
                }
            })
    }
}

interface OnBlogListener {
    fun onBlog(blog: Feed? = null, e: Exception? = null)
}

interface OnPraiseListener {
    fun onPraise(blog: Feed)
}

interface OnCommentPraiseListener {
    fun onPraise(comment: Comment)
}

interface OnWordListener {
    fun onWords(
        data: MutableList<Word>? = null,
        err: Exception? = null
    )
}

interface OnDelWordListener {
    fun onDelResult(code: Int = 0, err: Exception? = null)
}

interface OnCommentsListener {
    fun onComments(
        data: MutableList<Comment>? = null,
        err: Exception? = null
    )
}

interface OnCommentUidAddListener {
    fun onCommentAdd(
        total: Total? = null,
        err: Exception? = null,
        child: Boolean? = false
    )
}