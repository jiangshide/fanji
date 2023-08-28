package com.fanji.android.resource.vm.user.remote

import com.fanji.android.net.vm.data.RespData
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
import com.fanji.android.resource.vm.user.data.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
interface UserRemote {

    @GET("api/user/active")
    fun active(
        @Query("from") from: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("uid") uid: Long = Resource?.uid!!
    ): Observable<Response<RespData<MutableList<Active>>>>

    @FormUrlEncoded
    @POST("api/user/activeAdd")
    fun activeAdd(
        @Field("uid") uid: Long?,
        @Field("contentId") contentId: Long,
        @Field("from") from: Int
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("api/pay")
    fun pay(
        @Field("source") source: Int?,
        @Field("uid") uid: Long? = Resource.uid,
        @Field("body") body: String? = "",
        @Field("fee") fee: Int? = 500,
        @Field("receipt") receipt: String? = ""
    ): Observable<Response<RespData<Order>>>

    @GET("api/user/certification")
    fun certification(
        @Query("uid") uid: Long = Resource.uid!!
    ): Observable<Response<RespData<Certification>>>

    @GET("api/user/certificationList")
    fun certificationList(
        @Query("status") status: Int?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Certification>>>>

    @FormUrlEncoded
    @POST("api/user/certificationUpdate")
    fun certificationUpdate(
        @Field(
            "certification"
        ) certification: String
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("/api/user/followAdd")
    fun followAdd(
        @Field("uid") uid: Long?,
        @Field("status") status: Int,
        @Field("fromId") followId: Long? = Resource.uid
    ): Observable<Response<RespData<Int>>>

    @GET("api/user/follow")
    fun follow(
        @Query("action") action: Int,
        @Query("uid") uid: Long? = Resource.uid,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<User>>>>

    @GET("api/user/fans")
    fun fans(
        @Query("uid") uid: Long? = Resource.uid,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<User>>>>

    @GET("api/user/view")
    fun view(
        @Query("blogId") blogId: Long?
    ): Observable<Response<RespData<MutableList<User>>>>

    @FormUrlEncoded
    @POST("api/user/friendAdd")
    fun friendAdd(
        @Field("uid") uid: Long,
        @Field("fromId") fromId: Long? = Resource.uid,
        @Field("status") status: Int? = 1,
        @Field("reason") reason: String? = "",
        @Field("url") url: String? = ""
    ): Observable<Response<RespData<Long>>>

    @GET("api/user/friend")
    fun friend(
        @Query("fromId") fromId: Long? = Resource.uid,
        @Query("status") status: Int? = 1,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Friend>>>>

    @GET("api/user/praise")
    fun praise(
        @Query("blogId") blogId: Long?,
        @Query("status") status: Int? = 1
    ): Observable<Response<RespData<MutableList<User>>>>

    @GET("api/user/course")
    fun course(
        @Query("uid") uid: Long,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Course>>>>

    @GET("api/app/update")
    fun appUpdate(
        @Query("name") name: String?,
        @Query("platform") platform: Int?,
        @Query("version") version: String?,
        @Query("code") code: Int?
    ): Observable<Response<RespData<App>>>

    @GET("api/app/ads")
    fun appAds(): Observable<Response<RespData<Ads>>>

    @GET("api/user")
    fun users(
        @Query("status") status: Int,
        @Query("name") name: String? = "",
        @Query("uid") uid: Long? = Resource.uid,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<User>>>>

    @FormUrlEncoded
    @POST("api/user/online")
    fun oneLine(
        @Field("uid") uid: Long? = Resource.uid,
        @Field("online") online: Int? = 1,
        @Field("id") id: Long? = 0,
        @Field("name") name: String? = ""
    ): Observable<Response<RespData<Boolean>>>

    @GET("api/user/onlineList")
    fun onlineList(
        @Query("status") status: Int,
        @Query("online") online: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<User>>>>

    @GET("api/user/userExit")
    fun userExit(@Query("username") username: String): Observable<Response<RespData<Boolean>>>

    @FormUrlEncoded
    @POST("api/user/forgetPsw")
    fun forgetPsw(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("validateCode") validateCode: String
    ): Observable<Response<RespData<Boolean>>>

    @FormUrlEncoded
    @POST("api/user/reg")
    fun reg(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("validateCode") validateCode: String,
        @Field("country") country: String? = "",
        @Field("icon") icon: String? = "",
        @Field("sex") sex: Int = 0,
        @Field("netInfo") netInfo: String?,
        @Field("device") device: String?,
    ): Observable<Response<RespData<User>>>

    @FormUrlEncoded
    @POST("api/user/codeLogin")
    fun codeLogin(
        @Field("username") username: String,
        @Field("validateCode") validateCode: String,
        @Field("netInfo") netInfo: String?,
        @Field("device") device: String?,
    ): Observable<Response<RespData<User>>>

    @FormUrlEncoded
    @POST("api/user/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("netInfo") netInfo: String?,
        @Field("device") device: String?,
    ): Observable<Response<RespData<User>>>

    @FormUrlEncoded
    @POST("api/user/bind")
    fun bind(
        @Field("id") id: Long?,
        @Field("name") name: String,
        @Field("password") password: String,
        @Field("validateCode") validateCode: String,
        @Field("netInfo") netInfo: String?,
        @Field("device") device: String?,
    ): Observable<Response<RespData<User>>>

    /**
     * 微信登陆
     */
    @FormUrlEncoded
    @POST("api/user/weChat")
    fun weChat(
        @Field("code") code: String,
        @Field("netInfo") netInfo: String?,
        @Field("device") device: String?,
    ): Observable<Response<RespData<User>>>

    /**
     * QQ登陆
     */
    @GET("api/user/qq")
    fun qqLogin(
        @Query("appid") appid: String,
        @Query("access_token") accessToken: String,
        @Query("openid") openid: String
    ): Observable<Response<RespData<User>>>

    @FormUrlEncoded
    @POST("api/user/validate")
    fun validate(@Field("username") username: String): Observable<Response<RespData<String>>>

    @GET("api/user/profiles")
    fun profiles(
        @Query("uid") uid: Long?,
        @Query("fromId") fromId: Long?
    ): Observable<Response<RespData<User>>>

    @FormUrlEncoded
    @POST("api/user/profile")
    fun profile(
        @Field("profile") profile: String,
        @Field("type") type: Int
    ): Observable<Response<RespData<Profile>>>

    @FormUrlEncoded
    @POST("api/user/remarks")
    fun remarks(
        @Field("uid") uid: Long,
        @Field("fromId") fromId: Long? = Resource.uid,
        @Field("name") name: String,
        @Field("url") url: String? = ""
    ): Observable<Response<RespData<Long>>>
}