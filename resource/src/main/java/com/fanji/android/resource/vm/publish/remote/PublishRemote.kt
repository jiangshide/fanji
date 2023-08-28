package com.fanji.android.resource.vm.publish.remote

import com.fanji.android.net.vm.data.RespData
import com.fanji.android.resource.Resource
import com.fanji.android.resource.vm.feed.data.Feed
import com.fanji.android.util.data.AUDIO
import com.fanji.android.util.data.IMG
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * created by jiangshide on 2020/3/16.
 * email:18311271399@163.com
 */
interface PublishRemote {

  @GET("api/blog/syncStatus")
  fun syncStatus(
      @Query("uid") uid: Long? = Resource.uid,
      @Query("name") name: String? = "",
      @Query("sufix") sufix: String? = ""
  ): Observable<Response<RespData<Feed>>>

  @FormUrlEncoded
  @POST("api/blog/syncBlog")
  fun syncBlog(
      @Field("name") name: String? = "",
      @Field("sufix") sufix: String? = "",
      @Field("width") width: Int? = 0,
      @Field("height") height: Int? = 0,
      @Field("duration") duration: Long? = 0,
      @Field("bitrate") bitrate: Int? = 0,
      @Field("size") size: Long? = 0,
      @Field("rotate") rotate: Int? = 0,
      @Field("url") url: String,
      @Field("cover") cover: String? = "",
      @Field("channelId") channelId: Long? = 0,
      @Field("title") title: String? = "",
      @Field("des") des: String? = "",
      @Field("city") city: String? = "",
      @Field("position") position: String? = "",
      @Field("address") address: String? = "",
      @Field("format") format: Int? = AUDIO,
      @Field("uid") uid: Long? = Resource.uid,
      @Field("latitude") latitude: Double? = 0.0,
      @Field("longitude") longitude: Double? = 0.0,
      @Field("locationType") locationType: String? = "",
      @Field("adCode") adCode: String? = ""
  ): Observable<Response<RespData<Long>>>

  @FormUrlEncoded
  @POST("api/blog/publish")
  fun publish(
      @Field("channelId") channelId: Long,
      @Field("content") content: String? = "",
      @Field("title") title: String,
      @Field("des") des: String?,
      @Field("city") city: String?,
      @Field("position") position: String?,
      @Field("address") address: String?,
      @Field("netInfo") netInfo: String?,
      @Field("device") device: String?,
      @Field("atsJson") atsJson: String?,
      @Field("filesJson") filesJson: String?,
      @Field("format") format: Int = IMG,
      @Field("styleJson") styleJson: String? = null,
      @Field("uid") uid: Long = Resource.uid!!
  ): Observable<Response<RespData<Int>>>
}