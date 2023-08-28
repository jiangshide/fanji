package com.fanji.android.play

import android.app.Application
import android.content.Context
import com.fanji.android.cache.HttpProxyCacheServer
import com.fanji.android.util.AppUtil
import com.fanji.android.util.FileUtil
import java.io.File

/**
 * created by jiangshide on 2019-09-22.
 * email:18311271399@163.com
 */
object Play {

    var mContext: Context? = null
    var imgProxyServer: HttpProxyCacheServer? = null
    var audioProxyServer: HttpProxyCacheServer? = null
    var videoProxyServer: HttpProxyCacheServer? = null
    var docProxyServer: HttpProxyCacheServer? = null
    val IMG_CACHE_SIZE = 1024*1024*1024L
    val AUDIO_CACHE_SIZE = 10*1024*1024*1024L
    val VIDEO_CACGE_SIZE = 1024*1024*1024L
    val DOC_CACGE_SIZE = 2*1024*1024*1024L

    fun init(context: Application) {
        mContext = context
    }

    fun getImgProxy(): HttpProxyCacheServer {
        if (imgProxyServer == null) {
            if (mContext == null) {
                mContext = AppUtil.getApplicationContext()
            }
            imgProxyServer = HttpProxyCacheServer.Builder(mContext).cacheDirectory(File(FileUtil.getImagesDir())).maxCacheSize(
                IMG_CACHE_SIZE
            ).build()
        }
        return imgProxyServer!!
    }

    fun getAudioProxy(): HttpProxyCacheServer {
        if (audioProxyServer == null) {
            if (mContext == null) {
                mContext = AppUtil.getApplicationContext()
            }
            audioProxyServer = HttpProxyCacheServer.Builder(mContext).cacheDirectory(File(FileUtil.getAudioDir())).maxCacheSize(
                AUDIO_CACHE_SIZE
            ).build()
        }
        return audioProxyServer!!
    }

    fun getVideoProxy(): HttpProxyCacheServer {
        if (videoProxyServer == null) {
            if (mContext == null) {
                mContext = AppUtil.getApplicationContext()
            }
            videoProxyServer = HttpProxyCacheServer.Builder(mContext).cacheDirectory(File(FileUtil.getVideoDir())).maxCacheSize(
                VIDEO_CACGE_SIZE
            ).build()
        }
        return videoProxyServer!!
    }

    fun getDocProxy(): HttpProxyCacheServer {
        if (docProxyServer == null) {
            if (mContext == null) {
                mContext = AppUtil.getApplicationContext()
            }
            docProxyServer = HttpProxyCacheServer.Builder(mContext).cacheDirectory(File(FileUtil.getDocDir())).maxCacheSize(
                DOC_CACGE_SIZE
            ).build()
        }
        return docProxyServer!!
    }

    fun getUrl(url:String):String{
        if(FileUtil.isAudio(url)){
            return getAudioUrl(url)
        }else if(FileUtil.isVideo(url)){
            return getVideoUrl(url)
        }else if(FileUtil.isImg(url)){
            return getImgUrl(url)
        }else{
            return getDocUrl(url)
        }
    }

    fun getImgUrl(url:String):String{
        if (url?.contains("http") == true || url?.contains("https") == true) {
            return getImgProxy().getProxyUrl(url)
        }
        return url
    }

    fun getAudioUrl(url: String): String {
        if (url?.contains("http") == true || url?.contains("https") == true) {
            return getAudioProxy().getProxyUrl(url)
        }
        return url
    }

    fun getVideoUrl(url: String): String {
        if (url?.contains("http") == true || url?.contains("https") == true) {
            return getVideoProxy().getProxyUrl(url)
        }
        return url
    }

    fun getDocUrl(url: String): String {
        if (url?.contains("http") == true || url?.contains("https") == true) {
            return getDocProxy().getProxyUrl(url)
        }
        return url
    }
}
