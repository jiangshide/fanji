package com.fanji.android.resource.vm.channel

import androidx.lifecycle.MutableLiveData
import com.fanji.android.net.Net
import com.fanji.android.net.exception.NetException
import com.fanji.android.net.observer.BaseObserver
import com.fanji.android.net.transformer.CommonTransformer
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.channel.data.Channel
import com.fanji.android.resource.vm.channel.data.ChannelBlog
import com.fanji.android.resource.vm.channel.data.ChannelNature
import com.fanji.android.resource.vm.channel.data.ChannelType
import com.fanji.android.resource.vm.channel.data.Word
import com.fanji.android.resource.vm.channel.remote.ChannelRemote
import com.fanji.android.resource.vm.feed.OnWordListener
import com.fanji.android.ui.vm.FJVM
import com.fanji.android.util.ScreenUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import kotlin.random.Random

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class ChannelVM : FJVM() {
    private val iChannel: ChannelRemote = Net.createService(ChannelRemote::class.java)

    var followAdd: MutableLiveData<LiveResult<Channel>> = MutableLiveData()
    var channelFollow: MutableLiveData<LiveResult<MutableList<Channel>>> = MutableLiveData()
    var channelRecommend: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    var channelTypes: MutableLiveData<LiveResult<ArrayList<ChannelType>>> = MutableLiveData()
    var channelNature: MutableLiveData<LiveResult<MutableList<ChannelNature>>> = MutableLiveData()
    var createChannel: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var channel: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    val channelUser: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    val channelOfficial: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    val channelType: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    var search: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()
    var wordAdd: MutableLiveData<LiveResult<Int>> = MutableLiveData()
    var channelId: MutableLiveData<LiveResult<Channel>> = MutableLiveData()
    var follow: MutableLiveData<LiveResult<MutableList<ChannelBlog>>> = MutableLiveData()

    fun follow(
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iChannel.follow(page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    follow.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    follow.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun channelId(
        id: Long?,
        uid: Long?,
        blogId: Long
    ) {
        iChannel.channelId(id, uid, blogId)
            .compose(
                CommonTransformer<Response<RespData<Channel>>, Channel>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Channel>() {
                override fun onNext(t: Channel) {
                    super.onNext(t)
                    channelId.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelId.postValue(LiveResult.error(e))
                }
            })
    }

    fun word(
        uid: Long? = Resource.uid,
        source: Int = 0,
        listener: OnWordListener
    ) {
        iChannel.word(uid, source)
            .compose(
                CommonTransformer<Response<RespData<MutableList<Word>>>, MutableList<Word>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Word>>() {
                override fun onNext(t: MutableList<Word>) {
                    super.onNext(t)
                    listener?.onWords(data = t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    listener?.onWords(err = e)
                }
            })
    }

    fun search(
        word: String,
        uid: Long? = Resource.uid,
        source: Int? = 2,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iChannel.search(word, uid, source, page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    search.postValue(LiveResult.success(t, page = page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    search.postValue(LiveResult.error(e, page = page))
                }
            })
    }

    fun channel(
        status: Int = 2,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iChannel.channel(status, page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    channel.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channel.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun channelOfficial(
        official: Int? = 1,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iChannel.channelOfficial(official, page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    channelOfficial.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelOfficial.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

//  fun channelId(
//    status: Int = 2,
//    id: Long? = 1,
//    isRefresh: Boolean = true
//  ) {
//    refresh(isRefresh)
//    iChannel.channelId(status, id, page = page, pageSize = this.size)
//        .compose(
//            CommonTransformer<Response<RespData<MutableList<Channel>>>, MutableList<Channel>>()
//        )
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(object : BaseObserver<MutableList<Channel>>() {
//          override fun onNext(t: MutableList<Channel>) {
//            super.onNext(t)
//            if (!isRefresh && (t == null || t == null || t.size == 0)) {
//              page--
//            }
//            channelId.postValue(LiveResult.success(t))
//          }
//
//          override fun onFail(e: HttpException) {
//            super.onFail(e)
//            if (!isRefresh && page > 1) {
//              page--
//            }
//            channelId.postValue(LiveResult.error(e))
//          }
//        })
//  }

    fun channelType(
        status: Int = 2,
        id: Int? = 1,
        name: String = "",
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20,
        listener: VMListener<MutableList<ChannelBlog>>? = null
    ): ChannelVM {
        iChannel.channelType(status, id, name = name, page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    t?.forEach {
                        if (it.blog != null) {
                            it.blog!!.urls?.forEach {
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
        return this
    }

    fun channelUser(
        status: Int = 2,
        uid: Long? = Resource.uid,
        fromUid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iChannel.channelUser(status, uid, fromUid, page = page, pageSize = pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    channelUser.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelUser.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun createChannel(
        id: Long? = 0,
        uid: Long? = Resource.uid,
        channelNatureId: Int,
        name: String,
        cover: String,
        des: String
    ) {
        iChannel.createChannel(id, uid, channelNatureId, name, cover, des)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    createChannel.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    createChannel.postValue(LiveResult.error(e))
                }
            })
    }

    fun channelRecommend(
        status: Int = 1,
        uid: Long? = Resource.uid,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iChannel.channelRecommend(status, uid, page, pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelBlog>>>, MutableList<ChannelBlog>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelBlog>>() {
                override fun onNext(t: MutableList<ChannelBlog>) {
                    super.onNext(t)
                    channelRecommend.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelRecommend.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun channelTypes(uid: Long, isRefresh: Boolean = true): ChannelVM {
        iChannel.channelType(uid)
            .compose(
                CommonTransformer<Response<RespData<ArrayList<ChannelType>>>, ArrayList<ChannelType>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<ArrayList<ChannelType>>() {
                override fun onNext(t: ArrayList<ChannelType>) {
                    super.onNext(t)
                    channelTypes.postValue(LiveResult.success(t, isRefresh))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelTypes.postValue(LiveResult.error(e, isRefresh))
                }
            })
        return this
    }

    fun channelNature() {
        iChannel.channelNature(Resource.uid)
            .compose(
                CommonTransformer<Response<RespData<MutableList<ChannelNature>>>, MutableList<ChannelNature>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<ChannelNature>>() {
                override fun onNext(t: MutableList<ChannelNature>) {
                    super.onNext(t)
                    channelNature.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    channelNature.postValue(LiveResult.error(e))
                }
            })
    }
}