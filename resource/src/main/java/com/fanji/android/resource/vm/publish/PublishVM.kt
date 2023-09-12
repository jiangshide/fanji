package com.fanji.android.resource.vm.publish

import androidx.lifecycle.MutableLiveData
import com.fanji.android.net.Net
import com.fanji.android.net.exception.NetException
import com.fanji.android.net.observer.BaseObserver
import com.fanji.android.net.transformer.CommonTransformer
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.resource.vm.feed.data.OnSyncListener
import com.fanji.android.resource.vm.feed.data.OnSyncStatusListener
import com.fanji.android.resource.vm.publish.data.Publish
import com.fanji.android.resource.vm.publish.remote.PublishRemote
import com.fanji.android.ui.vm.FJVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class PublishVM : FJVM() {
    private val iPublish: PublishRemote = Net.createService(PublishRemote::class.java)

    var publishBlog: MutableLiveData<LiveResult<Publish>> = MutableLiveData()

    fun syncStatus(
        name: String? = "",
        sufix: String? = "",
        url: String? = "",
        listener: OnSyncStatusListener? = null
    ) {
        iPublish.syncStatus(name = name, sufix = sufix)
            .compose(CommonTransformer<Response<RespData<Feed>>, Feed>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Feed>() {
                override fun onNext(t: Feed) {
                    super.onNext(t)
                    listener?.syncResult(1, t)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    listener?.syncResult(-1)
                }
            })
    }

    fun syncBlog(
        blog: Feed,
        listener: OnSyncListener
    ) {
        iPublish.syncBlog(
            name = blog.name,
            sufix = blog.sufix,
            width = blog.width,
            height = blog.height,
            duration = blog.duration,
            bitrate = blog.bitrate,
            size = blog.size,
            rotate = blog.rotate,
            url = blog.url!!,
            cover = blog.cover,
            channelId = blog.channelId,
            title = blog.title,
            des = blog.des,
            city = blog.city,
            position = blog.position,
            address = blog.address,
            format = blog.format,
            uid = blog.uid,
            latitude = blog.latitude,
            longitude = blog.longitude,
            locationType = blog.locationType
        )
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    listener?.syncResult(1, blog?.url!!)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    listener?.syncResult(-1, blog?.url!!)
                }
            })
    }

    fun publish(
        publish: Publish
    ) {
        iPublish.publish(
            publish.channelId,
            publish?.content,
            publish.title!!,
            publish.des,
            publish.city,
            publish.position,
            publish.address,
            publish.netInfo,
            publish.device,
            publish.atsJson,
            publish.filesJson,
            publish.format!!,
            publish.styleJson
        )
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    publishBlog.postValue(LiveResult.success(publish))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    publishBlog.postValue(LiveResult.error(e))
                }
            })
    }
}