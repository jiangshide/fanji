package com.fanji.android.net

import android.content.Context
import android.location.Address
import com.fanji.android.net.codec.BooleanTypeAdapter
import com.fanji.android.net.interceptor.AuthInterceptor
import com.fanji.android.net.interceptor.OfflineCacheInterceptor
import com.fanji.android.net.location.Location
import com.fanji.android.net.okhttp.WebSocketHandler
import com.fanji.android.net.state.NetState
import com.fanji.android.util.AppUtil
import com.fanji.android.util.FileUtil
import com.fanji.android.util.SPUtil
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * created by jiangshide on 4/10/21.
 * email:18311271399@163.com
 */
object Net : Location.AddressCallback {
    private var retrofit: Retrofit? = null

    const val TOKEN = "token"

    private lateinit var mAddress: Address
    open var mLat: Double = 0.0
    open var mLng: Double = 0.0

    open var token: String?
        get() = SPUtil.getString(TOKEN)
        set(token) {
            SPUtil.putString(TOKEN, token!!)
        }

    fun init(context: Context?) {
        val cache = Cache(FileUtil.getHttpDir(context), 1024 * 1024 * 1024)
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(OfflineCacheInterceptor())
//            .addInterceptor(EncryptInterceptor())
//            .addInterceptor(SignInterceptor())
            .cache(cache)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder().registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter())
            .create()
        val host = BuildConfig.API_HOST
        retrofit = Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
        NetState.instance.init(context)
    }

    fun updateAddress() {
        Location.getInstance(AppUtil.getApplicationContext()).addressCallback = this
    }

    fun <T> createService(clazz: Class<T>): T {
        return retrofit!!.create(clazz)
    }

    override fun onGetAddress(address: Address) {
        mAddress = address
    }

    override fun onGetLocation(lat: Double, lng: Double) {
        mLat = lat
        mLng = lng
    }

    fun registerWebSocket(url: String?, webSocketCallback: WebSocketHandler.WebSocketCallBack) {
        WebSocketHandler.register(url, webSocketCallback)
    }

    fun connectWebSocket() {
        WebSocketHandler.connect()
    }

    fun sendMsg(msg: String) {
        WebSocketHandler.send(msg)
    }

    fun closeWebSocket(immediately: Boolean) {
        if (immediately) {
            WebSocketHandler.cancel()
            return
        }
        WebSocketHandler.close()
    }
}

val HTTP_OK = BuildConfig.RESP_CODE_VALUE