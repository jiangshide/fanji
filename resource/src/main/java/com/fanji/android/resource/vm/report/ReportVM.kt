package com.fanji.android.resource.vm.report

import androidx.lifecycle.MutableLiveData
import com.fanji.android.net.Net
import com.fanji.android.net.exception.NetException
import com.fanji.android.net.observer.BaseObserver
import com.fanji.android.net.transformer.CommonTransformer
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.vm.report.data.Report
import com.fanji.android.resource.vm.report.data.Type
import com.fanji.android.resource.vm.report.remote.ReportRemote
import com.fanji.android.ui.vm.FJVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * created by jiangshide on 2020/4/5.
 * email:18311271399@163.com
 */
class ReportVM : FJVM() {
    private val iReport: ReportRemote = Net.createService(ReportRemote::class.java)

    var reportAdd: MutableLiveData<LiveResult<Int>> = MutableLiveData()
    var types: MutableLiveData<LiveResult<MutableList<Type>>> = MutableLiveData()
    var feedback: MutableLiveData<LiveResult<Long>> = MutableLiveData()

    fun feedback(
        contentId: Long? = 0,
        content: String,
        url: String? = "",
        contact: String,
        source: Int? = 0
    ) {
        iReport.feedback(
            contentId = contentId, content = content, url = url, contact = contact, source = source
        )
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    feedback.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    feedback.postValue(LiveResult.error(e))
                }
            })
    }

    fun reportAdd(
        report: Report
    ) {
        iReport.reportAdd(
            contentId = report.contentId, type = report.type, source = report.source,
            status = report.status, reason = report.reason
        )
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    reportAdd.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    reportAdd.postValue(LiveResult.error(e))
                }
            })
    }

    fun types() {
        iReport.types()
            .compose(CommonTransformer<Response<RespData<MutableList<Type>>>, MutableList<Type>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Type>>() {
                override fun onNext(t: MutableList<Type>) {
                    super.onNext(t)
                    types.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    types.postValue(LiveResult.error(e))
                }
            })
    }
}