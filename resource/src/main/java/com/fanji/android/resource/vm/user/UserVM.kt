package com.fanji.android.resource.vm.user

import androidx.lifecycle.MutableLiveData
import com.fanji.android.net.Net
import com.fanji.android.net.exception.NetException
import com.fanji.android.net.observer.BaseObserver
import com.fanji.android.net.transformer.CommonTransformer
import com.fanji.android.net.vm.LiveResult
import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.ACTIVE_SPLASH
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.user.data.Active
import com.fanji.android.resource.vm.user.data.Ads
import com.fanji.android.resource.vm.user.data.App
import com.fanji.android.resource.vm.user.data.Certification
import com.fanji.android.resource.vm.user.data.Course
import com.fanji.android.resource.vm.user.data.Friend
import com.fanji.android.resource.vm.user.data.Order
import com.fanji.android.resource.vm.user.data.Profile
import com.fanji.android.resource.vm.user.data.User
import com.fanji.android.resource.vm.user.remote.UserRemote
import com.fanji.android.ui.vm.FJVM
import com.fanji.android.util.AppUtil
import com.fanji.android.util.LogUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
class UserVM : FJVM() {

    private val iUser: UserRemote = Net.createService(UserRemote::class.java)
    var forgetPsw: MutableLiveData<LiveResult<Boolean>> = MutableLiveData()
    var reg: MutableLiveData<LiveResult<User>> = MutableLiveData()
    var codeLogin: MutableLiveData<LiveResult<User>> = MutableLiveData()
    var login: MutableLiveData<LiveResult<User>> = MutableLiveData()
    var bind: MutableLiveData<LiveResult<User>> = MutableLiveData()
    var validate: MutableLiveData<LiveResult<String>> = MutableLiveData()
    var userExit: MutableLiveData<LiveResult<Boolean>> = MutableLiveData()
    var profile: MutableLiveData<LiveResult<Profile>> = MutableLiveData()
    var remarks: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var profileUid: MutableLiveData<LiveResult<User>> = MutableLiveData()
    var oneLine: MutableLiveData<LiveResult<Boolean>> = MutableLiveData()
    var onlineList: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var users: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var appAds: MutableLiveData<LiveResult<Ads>> = MutableLiveData()
    var appUpdate: MutableLiveData<LiveResult<App>> = MutableLiveData()
    var praise: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var course: MutableLiveData<LiveResult<MutableList<Course>>> = MutableLiveData()
    var friend: MutableLiveData<LiveResult<MutableList<Friend>>> = MutableLiveData()
    var friendAdd: MutableLiveData<LiveResult<Long>> = MutableLiveData()
    var follow: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var fans: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var view: MutableLiveData<LiveResult<MutableList<User>>> = MutableLiveData()
    var certification: MutableLiveData<LiveResult<Certification>> = MutableLiveData()
    var certificationUpdate: MutableLiveData<LiveResult<Certification>> = MutableLiveData()
    var certificationList: MutableLiveData<LiveResult<MutableList<Certification>>> =
        MutableLiveData()
    var pay: MutableLiveData<LiveResult<Order>> = MutableLiveData()
    var activeAdd: MutableLiveData<LiveResult<Int>> = MutableLiveData()
    var active: MutableLiveData<LiveResult<MutableList<Active>>> =
        MutableLiveData()

    fun active(
        from: Int, isRefresh: Boolean = true, page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.active(from, page, pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<Active>>>, MutableList<Active>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Active>>() {
                override fun onNext(t: MutableList<Active>) {
                    super.onNext(t)
                    active.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    active.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun activeAdd(from: Int = ACTIVE_SPLASH, contentId: Long = 0) {
        if (Resource.user == null) return
        iUser.activeAdd(Resource.uid, contentId, from)
            .compose(
                CommonTransformer<Response<RespData<Int>>, Int>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    activeAdd.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    activeAdd.postValue(LiveResult.error(e))
                }
            })
    }

    fun pay(
        source: Int? = 1,
        uid: Long? = Resource.uid,
        body: String? = AppUtil.getPackageName() + "-账号充值",
        fee: Int? = 500,
        receipt: String? = ""
    ) {
        iUser.pay(source, uid, body, fee, receipt)
            .compose(
                CommonTransformer<Response<RespData<Order>>, Order>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Order>() {
                override fun onNext(t: Order) {
                    super.onNext(t)
                    pay.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    pay.postValue(LiveResult.error(e))
                }
            })
    }

    fun certification() {
        iUser.certification()
            .compose(
                CommonTransformer<Response<RespData<Certification>>, Certification>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Certification>() {
                override fun onNext(t: Certification) {
                    super.onNext(t)
                    certification.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    certification.postValue(LiveResult.error(e))
                }
            })
    }

    fun certificationList(
        status: Int? = 1,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.certificationList(status, page, pageSize)
            .compose(
                CommonTransformer<Response<RespData<MutableList<Certification>>>, MutableList<Certification>>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Certification>>() {
                override fun onNext(t: MutableList<Certification>) {
                    super.onNext(t)
                    certificationList.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    certificationList.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun certificationUpdate(cert: Certification) {
        iUser.certificationUpdate(cert.toJson()!!)
            .compose(CommonTransformer<Response<RespData<Int>>, Int>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(t: Int) {
                    super.onNext(t)
                    certificationUpdate.postValue(LiveResult.success(cert))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    certificationUpdate.postValue(LiveResult.error(e))
                }
            })
    }

    fun followAdd(
        uid: Long, status: Int, listener: OnFollowListener
    ) {
        iUser.followAdd(uid = uid, status = status)
            .compose(
                CommonTransformer<Response<RespData<Int>>, Int>()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Int>() {
                override fun onNext(id: Int) {
                    super.onNext(status)
                    listener?.follow(status, null)
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    listener?.follow(-1, e)
                }
            })
    }

    fun follow(
        action: Int = 0,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.follow(action, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    follow.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    follow.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun fans(
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.fans(page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    fans.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    fans.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun view(
        blogId: Long,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.view(blogId = blogId)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    view.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    view.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun friendAdd(
        uid: Long,
        fromId: Long? = Resource.uid,
        status: Int = 1,
        reason: String? = "",
        url: String? = ""
    ) {
        iUser.friendAdd(uid = uid, fromId = fromId, status = status, reason = reason, url = url)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    friendAdd.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    friendAdd.postValue(LiveResult.error(e))
                }
            })
    }

    fun friend(
        fromId: Long? = Resource.uid,
        status: Int? = 1,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.friend(fromId, status, page, pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Friend>>>, MutableList<Friend>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Friend>>() {
                override fun onNext(t: MutableList<Friend>) {
                    super.onNext(t)
                    friend.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    friend.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun praise(
        blogId: Long,
        status: Int? = 1,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.praise(blogId = blogId, status = status)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    praise.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    praise.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun course(
        uid: Long,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.course(uid, page, pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<Course>>>, MutableList<Course>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<Course>>() {
                override fun onNext(t: MutableList<Course>) {
                    super.onNext(t)
                    course.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    course.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun appAds() {
        iUser.appAds()
            .compose(CommonTransformer<Response<RespData<Ads>>, Ads>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Ads>() {
                override fun onNext(t: Ads) {
                    super.onNext(t)
//                    appAds.postValue(LiveResult.success(t))
                    Resource.ads = t
                }

                override fun onFail(e: NetException?) {
                    super.onFail(e)
//                    appAds.postValue(LiveResult.error(e))
                }
            })
    }

    fun appUpdate(
        name: String? = null,
        platform: Int?,
        version: String?,
        code: Int?
    ) {
        iUser.appUpdate(name, platform, version, code)
            .compose(CommonTransformer<Response<RespData<App>>, App>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<App>() {
                override fun onNext(t: App) {
                    super.onNext(t)
                    appUpdate.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    appUpdate.postValue(LiveResult.error(e))
                }
            })
    }

    fun users(
        status: Int = 2,
        name: String? = "",
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.users(status = status, name = name, page = page, pageSize = pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    users.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    users.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun oneLine(
        online: Int? = -1,
        id: Long? = 0,
        name: String? = ""
    ) {
        if (Resource.uid!! <= 0) return
        iUser.oneLine(online = online, id = id, name = name)
            .compose(CommonTransformer<Response<RespData<Boolean>>, Boolean>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Boolean>() {
                override fun onNext(t: Boolean) {
                    super.onNext(t)
                    LogUtil.e("--------t:", t)
//                    oneLine.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e("e:", e)
//                    oneLine.postValue(LiveResult.error(e))
                }
            })
    }

    fun onlineList(
        status: Int = 2,
        online: Int = 1,
        isRefresh: Boolean = true,
        page: Int = 0,
        pageSize: Int = 20
    ) {
        iUser.onlineList(status, online, page, pageSize)
            .compose(CommonTransformer<Response<RespData<MutableList<User>>>, MutableList<User>>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<MutableList<User>>() {
                override fun onNext(t: MutableList<User>) {
                    super.onNext(t)
                    onlineList.postValue(LiveResult.success(t, isRefresh, page))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    onlineList.postValue(LiveResult.error(e, isRefresh, page))
                }
            })
    }

    fun profile(
        fromId: Long? = Resource.uid,
        uid: Long? = Resource.uid
    ) {
        iUser.profiles(uid, fromId)
            .compose(CommonTransformer<Response<RespData<User>>, User>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<User>() {
                override fun onNext(t: User) {
                    super.onNext(t)
                    profileUid.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    profileUid.postValue(LiveResult.error(e))
                }
            })
    }

    fun profile(
        p: Profile?,
        type: Int = 0
    ) {
        iUser.profile(p?.getGson()!!, type)
            .compose(CommonTransformer<Response<RespData<Profile>>, Profile>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Profile>() {
                override fun onNext(t: Profile) {
                    super.onNext(t)
                    val user = Resource.user
                    user?.set(t)
                    Resource.user = user
                    profile.postValue(LiveResult.success(user as Profile))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    profile.postValue(LiveResult.error(e))
                }
            })
    }

    fun remarks(
        uid: Long,
        name: String,
        url: String? = ""
    ) {
        iUser.remarks(uid = uid, name = name, url = url)
            .compose(CommonTransformer<Response<RespData<Long>>, Long>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Long>() {
                override fun onNext(t: Long) {
                    super.onNext(t)
                    remarks.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    remarks.postValue(LiveResult.error(e))
                }
            })
    }

    fun userExit(userName: String) {
        iUser.userExit(userName)
            .compose(CommonTransformer<Response<RespData<Boolean>>, Boolean>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Boolean>() {
                override fun onNext(t: Boolean) {
                    super.onNext(t)
                    LogUtil.e("t:", t)
                    userExit.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e("e", e)
                    userExit.postValue(LiveResult.error(e))
                }
            })
    }

    fun forgetPsw(
        userName: String,
        password: String,
        validateCode: String
    ) {
        iUser.forgetPsw(userName, md5(password), validateCode)
            .compose(CommonTransformer<Response<RespData<Boolean>>, Boolean>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<Boolean>() {
                override fun onNext(t: Boolean) {
                    super.onNext(t)
                    forgetPsw.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    forgetPsw.postValue(LiveResult.error(e))
                }
            })
    }

    fun reg(
        userName: String,
        password: String,
        validateCode: String,
        country: String? = "",
        icon: String? = "",
        sex: Int = 0,
        netInfo: String = "",
        device: String = "",
        adCode: String = ""
    ) {
        iUser.reg(
            userName,
            md5(password),
            validateCode,
            country,
            icon,
            sex,
            netInfo,
            device
        )
            .compose(CommonTransformer<Response<RespData<User>>, User>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<User>() {
                override fun onNext(t: User) {
                    super.onNext(t)
                    reg.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    reg.postValue(LiveResult.error(e))
                }
            })
    }

    fun codeLogin(
        userName: String, validateCode: String, adCode: String, netInfo: String = "",
        device: String = ""
    ) {
        iUser.codeLogin(userName, validateCode, netInfo = device, device = device)
            .compose(CommonTransformer<Response<RespData<User>>, User>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<User>() {
                override fun onNext(t: User) {
                    super.onNext(t)
                    codeLogin.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    codeLogin.postValue(LiveResult.error(e))
                }
            })
    }

    fun login(
        userName: String,
        password: String,
        netInfo: String = "",
        device: String = ""
    ) {
        iUser.login(userName, md5(password), netInfo, device)
            .compose(CommonTransformer<Response<RespData<User>>, User>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<User>() {
                override fun onNext(t: User) {
                    super.onNext(t)
                    login.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    login.postValue(LiveResult.error(e))
                }
            })
    }

    fun bind(
        id: Long? = Resource.uid,
        name: String,
        password: String,
        validateCode: String, adCode: String,
        netInfo: String? = "",
        device: String? = ""
    ) {
        iUser.bind(id, name, md5(password), validateCode, netInfo, device)
            .compose(CommonTransformer<Response<RespData<User>>, User>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<User>() {
                override fun onNext(t: User) {
                    super.onNext(t)
                    bind.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    bind.postValue(LiveResult.error(e))
                }
            })
    }

    fun weChat(
        code: String,
        netInfo: String = "",
        device: String = ""
    ) {
        LogUtil.e("----jsd---", "code:" + code + " | netInnfo:" + netInfo + " | device:" + device)
        iUser!!.weChat(code, netInfo, device)
            .compose(CommonTransformer<Response<RespData<User>>, User>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<User>() {
                override fun onNext(t: User) {
                    LogUtil.e("--------t:", t)
                    login.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    LogUtil.e("e------:", e)
                    login.postValue(LiveResult.error(e))
                }
            })
    }

//  fun qqLogin(
//    context: Context,
//    accessToken: String,
//    openid: String
//  ) {
//    iUser!!.qqLogin(BuildConfig.QQ_ID, accessToken, openid)
//        .compose(CommonTransformer<Response<RespData<User>>, User>())
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(object : BaseObserver<User>(context) {
//          override fun onNext(t: User) {
//            login.postValue(LiveResult.success(t))
//          }
//
//          override fun onFail(e: HttpException) {
//            super.onFail(e)
//            login.postValue(LiveResult.error(e))
//          }
//        })
//  }

    fun validate(userName: String) {
        iUser.validate(userName)
            .compose(CommonTransformer<Response<RespData<String>>, String>())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BaseObserver<String>() {
                override fun onNext(t: String) {
                    super.onNext(t)
                    validate.postValue(LiveResult.success(t))
                }

                override fun onFail(e: NetException) {
                    super.onFail(e)
                    validate.postValue(LiveResult.error(e))
                }
            })
    }

}

interface OnFollowListener {
    fun follow(status: Int, e: NetException?)
}