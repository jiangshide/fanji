package com.fanji.android.net.state.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.fanji.android.net.state.NetState
import com.fanji.android.net.state.annotation.INetType
import com.fanji.android.net.state.annotation.NetType
import com.fanji.android.net.state.model.NetModel
import java.lang.reflect.InvocationTargetException
import java.util.*


/**
 * created by jiangshide on 2019-08-14.
 * email:18311271399@163.com
 */
class NetReceiver : BroadcastReceiver() {

    private var networkList: MutableMap<Any, List<NetModel>>? = null

    var ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

    init {
        networkList = HashMap()
    }

    fun registerObserver(register: Any) {
        if(networkList == null)return
        var methodManagers = networkList!![register]
        if (methodManagers == null) {
            methodManagers = findAnnotationMethod(register)
            networkList!![register] = methodManagers
        }
    }

    fun unRegisterObserver(register: Any) {
        networkList?.remove(register)
        networkList?.clear()
        networkList = null
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (null == intent || TextUtils.isEmpty(intent.action)) return
        if (intent.action!!.equals(ACTION, ignoreCase = true)) {
            post(NetState.instance.getNetType())
        }
    }

    open fun post(@INetType netType: Int) {
        if (networkList == null) return
        val set = networkList?.keys ?: return
        for (getter in set) {
            val methodManagers = networkList!![getter]
            if (methodManagers != null) {
                for (methodManager in methodManagers) {
                    if (methodManager?.type?.isAssignableFrom(INetType::class.java) == true) {
                        when (methodManager?.netType) {
                            INetType.AUTO -> invoke(methodManager, getter, netType)
                            INetType.WIFI -> if (netType == INetType.WIFI || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                            INetType.CELLULAR -> if (netType == INetType.CELLULAR || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                            INetType.BLUETOOTH -> if (netType == INetType.BLUETOOTH || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                            INetType.ETHERNET -> if (netType == INetType.ETHERNET || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                            INetType.VPN -> if (netType == INetType.VPN || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                            INetType.WIFI_AWARE -> if (netType == INetType.WIFI_AWARE || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                            INetType.LOWPAN -> if (netType == INetType.LOWPAN || netType == INetType.NONE) {
                                invoke(methodManager, getter, netType)
                            }
                        }
                    }
                }
            }
        }
    }

    private operator fun invoke(methodManager: NetModel, getter: Any, @INetType netType: Int) {
        try {
            methodManager?.method?.invoke(getter, netType)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    private fun findAnnotationMethod(register: Any): List<NetModel> {
        val methodManagers = ArrayList<NetModel>()
        val clazz = register?.javaClass
        val methods = clazz?.methods
        if(methods != null){
            for (method in methods) {
                val netWork = method?.getAnnotation(NetType::class.java) ?: continue
                val paramterTypes = method?.parameterTypes
                if (paramterTypes!!.size != 1) {
                    continue
                }
                val methodManager = NetModel(INetType::class.java, netWork.netType, method)
                methodManagers?.add(methodManager)
            }
        }
        return methodManagers
    }
}
