package com.fanji.android.resource.vm.feed.remote

import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.channel.data.Word
import com.fanji.android.resource.vm.feed.data.Comment
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.resource.vm.feed.data.Format
import com.fanji.android.resource.vm.feed.data.LrcLInfo
import com.fanji.android.resource.vm.feed.data.Total
import com.fanji.android.resource.vm.feed.data.*
import com.fanji.android.resource.vm.user.data.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * created by jiangshide on 4/7/21.
 * email:18311271399@163.com
 */
interface FeedRemote {

    @GET("api/blog/history")
    fun historyList(
        @Query("uid") uid: Long? = Resource.uid,
        @Query("source") source: Int? = 0,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @FormUrlEncoded
    @POST("api/blog/history")
    fun historyAdd(
        @Field("uid") uid: Long? = Resource.uid,
        @Field("contentId") contentId: Long,
        @Field("num") num: Int? = 0,
        @Field("source") source: Int? = 0
    ): Observable<Response<RespData<Long>>>

    @GET("api/blog/lrcUpdate")
    fun lrcUpdate(
        @Query("id") id: Long,
        @Query("name") name: String? = "",
        @Query("lrcZh") lrcZh: String? = "",
        @Query("lrcEs") lrcEs: String? = ""
    ): Observable<Response<RespData<Long>>>

    @GET("api/blog/lrc")
    fun searchLrc(
        @Query("uid") uid: Long,
        @Query("name") name: String,
        @Query("author") author: String? = "",
        @Query("language") language: Int = 0
    ): Observable<Response<RespData<LrcLInfo>>>

    @GET("api/blog/id")
    fun blogId(
        @Query("id") id: Long,
        @Query("uid") uid: Long? = Resource.uid
    ): Observable<Response<RespData<Feed>>>

    @GET("api/blog/format")
    fun blogFormat(
        @Query("uid") uid: Long? = Resource.uid,
        @Query("format") format: Int = 0,
        @Query("title") title: String = "",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @FormUrlEncoded
    @POST("api/blog/commentAdd")
    fun commentAdd(
        @Field("uid") uid: Long? = Resource.uid,
        @Field("contentId") contentId: Long? = 0,
        @Field("content") content: String?,
        @Field("at") at: String?,
        @Field("topic") topic: String?,
        @Field("urls") urls: String? = "",
        @Field("status") status: Int? = 1
    ): Observable<Response<RespData<Total>>>

    @GET("api/blog/comment")
    fun comment(
        @Query("uid") uid: Long? = Resource.uid,
        @Query("contentId") contentId: Long,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Comment>>>>

    @FormUrlEncoded
    @POST("api/blog/praiseCommentAdd")
    fun praiseCommentAdd(
        @Field("id") id: Long = 0,
        @Field("uid") uid: Long? = Resource.uid,
        @Field("commentId") commentId: Long? = 0,
        @Field("status") status: Int
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("api/blog/commentUidAdd")
    fun commentUidAdd(
        @Field("commentId") commentId: Long,
        @Field("uid") uid: Long? = Resource.uid,
        @Field("fromUid") fromUid: Long? = 0,
        @Field("contentId") contentId: Long? = 0,
        @Field("content") content: String?,
        @Field("at") at: String?,
        @Field("topic") topic: String?,
        @Field("urls") urls: String? = "",
        @Field("reply") reply: Int = 0,
        @Field("status") status: Int? = 1
    ): Observable<Response<RespData<Total>>>

    @GET("api/blog/commentUid")
    fun commentUid(
        @Query("commentId") commentId: Long,
        @Query("uid") uid: Long? = Resource.uid,
        @Query("fromUid") fromUid: Long? = 0,
        @Query("contentId") contentId: Long,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Comment>>>>

    @FormUrlEncoded
    @POST("api/blog/praiseCommentUidAdd")
    fun praiseCommentUidAdd(
        @Field("id") id: Long = 0,
        @Field("uid") uid: Long? = Resource.uid,
        @Field("commentUidId") commentUidId: Long? = 0,
        @Field("status") status: Int
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("api/blog/topAdd")
    fun topAdd(
        @Field("blogId") blogId: Long?,
        @Field("status") status: Int,
        @Field("uid") uid: Long? = Resource.uid
    ): Observable<Response<RespData<Any>>>

    @FormUrlEncoded
    @POST("api/blog/collectionAdd")
    fun collectionAdd(
        @Field("blogId") blogId: Long?,
        @Field("status") status: Int,
        @Field("uid") uid: Long? = Resource.uid
    ): Observable<Response<RespData<Any>>>

    @FormUrlEncoded
    @POST("api/user/followAdd")
    fun followAdd(
        @Field("uid") uid: Long?,
        @Field("status") status: Int,
        @Field("fromId") fromId: Long? = Resource.uid
    ): Observable<Response<RespData<Any>>>

    @GET("api/blog/share")
    fun share(
        @Query("blogId") blogId: Long?,
        @Query("status") status: Int? = 1,
        @Query("station") station: Int? = 0
    ): Observable<Response<RespData<MutableList<User>>>>

    @FormUrlEncoded
    @POST("api/blog/shareAdd")
    fun shareAdd(
        @Field("uid") uid: Long? = Resource.uid,
        @Field("blogId") blogId: Long?,
        @Field("status") status: Int? = 0,
        @Field("fromIds") fromIds: String?,
        @Field("station") station: Int = 0
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("api/blog/viewAdd")
    fun viewAdd(
        @Field("fromId") fromId: Long?,
        @Field("blogId") blogId: Long?,
        @Field("uid") uid: Long? = Resource.uid,
        @Field("num") num: Int
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("api/blog/praiseAdd")
    fun praiseAdd(
        @Field("fromId") fromId: Long?,
        @Field("uid") uid: Long? = Resource.uid,
        @Field("blogId") blogId: Long?,
        @Field("status") status: Int
    ): Observable<Response<RespData<Int>>>

    @GET("api/format")
    fun format(): Observable<Response<RespData<MutableList<Format>>>>

    @GET("api/format/banner")
    fun banner(
        @Query("name") name: String = "",
        @Query("uid") uid: Long? = Resource.uid
    ): Observable<Response<RespData<MutableList<Word>>>>

    @GET("api/format/del")
    fun delWord(
        @Query("name") name: String = "",
        @Query("uid") uid: Long? = Resource.uid
    ): Observable<Response<RespData<Int>>>

    @GET("api/blog/collection")
    fun collectionBlog(
        @Query("status") status: Int,
        @Query("uid") uid: Long?,
        @Query("blogStatus") blogStatus: Int,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/user")
    fun userBlog(
        @Query("status") status: Int,
        @Query("uid") uid: Long?,
        @Query("fromUid") fromUid: Long?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/city")
    fun cityBlog(
        @Query("status") status: Int,
        @Query("mode") mode: Int,
        @Query("city") city: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/follow")
    fun followBlog(
        @Query("status") status: Int,
        @Query("uid") uid: Long?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/recommend")
    fun recommendBlog(
        @Query("format") format: Int? = -1,
        @Query("uid") uid: Long?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/find")
    fun findBlog(
        @Query("status") status: Int,
        @Query("uid") uid: Long?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/praise")
    fun praiseBlog(
        @Query("uid") uid: Long?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/browse")
    fun browseBlog(
        @Query("uid") uid: Long?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @GET("api/blog/channel")
    fun channelBlog(
        @Query("channelId") channelId: Long?,
        @Query("sort") sort: Int? = 1,
        @Query("uid") uid: Long? = Resource.uid,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Observable<Response<RespData<MutableList<Feed>>>>

    @FormUrlEncoded
    @POST("api/blog/update")
    fun update(
        @Field("id") id: Long,
        @Field("status") status: Int,
        @Field("reason") reason: String?
    ): Observable<Response<RespData<Int>>>

    @FormUrlEncoded
    @POST("api/blog/remove")
    fun remove(@Field("id") id: Long): Observable<Response<RespData<Int>>>
}