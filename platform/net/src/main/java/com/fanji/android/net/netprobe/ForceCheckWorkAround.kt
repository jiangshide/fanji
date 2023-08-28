package com.fanji.android.net.netprobe

import com.fanji.android.net.netprobe.okhttp.OkHttpCheckData
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-2 上午11:56
 */
internal object ForceCheckWorkAround {
    val forceCheckMap = ConcurrentHashMap<String,Float>()
    val forceCheckExpires = 60_000L
    var forceCheckTimestamp=0L
    var forceCheckSize=0
    var startHash="-"

    fun start(size:Int,hash:String){
        forceCheckMap.clear()
        forceCheckTimestamp = System.currentTimeMillis()
        forceCheckSize = size
        startHash = hash
    }

    fun onRequestFinished(data:OkHttpCheckData){
        if(data.request.tag(String::class.java) != startHash)return
        if(System.currentTimeMillis() - forceCheckTimestamp < forceCheckExpires){
            val healthValue = (1.0F-data.duration*1.0F/(data.duration+Params.getHttpSmoothFactor(data.host)))*(if(data.exception==null)1F else 0F)
            forceCheckMap[data.host]=healthValue
            if(forceCheckMap.size>= forceCheckSize){
                report(HashMap<String,Float>(forceCheckMap))
                reset()
            }
        }else{
            report(HashMap<String,Float>(forceCheckMap))
            reset()
        }
    }

    fun reset(){
        forceCheckMap.clear()
        forceCheckTimestamp = 0
    }

    fun report(checkers:HashMap<String,Float>){
        if(checkers.isEmpty())return
    }
}