package com.fanji.android.net.retrofit

import com.fanji.android.net.retrofit.annotation.GET
import com.fanji.android.net.retrofit.annotation.POST
import com.fanji.android.net.retrofit.annotation.Param
import com.fanji.android.net.retrofit.data.CallData
import com.fanji.android.net.retrofit.data.RequestMethodData
import com.fanji.android.net.retrofit.exception.RetrofitException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import kotlin.collections.HashMap

/**
 * created by jiangshide on 5/28/21.
 * email:18311271399@163.com
 */
class Retrofit(private val baseUrl: String? = null) {
    private var serviceMethodCache: HashMap<String, RequestMethodData>? = null

    fun <T> create(service: Class<T>): T {
        eagerlyValidateMethods(service)
        return Proxy.newProxyInstance(service.classLoader, arrayOf<Class<*>>(service),
            InvocationHandler { proxy, method, args ->
                if (method.declaringClass == Any::class.java) {
                    return@InvocationHandler method.invoke(proxy, *args)
                }
                val requestEntity = serviceMethodCache!![method.name]
                    ?: throw RetrofitException(method.name + " Method illegal")
                newCall(baseUrl!!, requestEntity, args)
            }) as T
    }

    private fun newCall(
        baseUrl: String,
        entity: RequestMethodData,
        args: Array<Any>
    ): CallData? {
        return CallData(baseUrl, entity, args)
    }

    private fun eagerlyValidateMethods(service: Class<*>) {
        if (serviceMethodCache == null) {
            serviceMethodCache = HashMap()
            val methods = service.methods
            for (method in methods) {
                val list = getMethodParameterNamesByAnnotation(method)
                var type: String? = null
                var value: String? = null
                val annotation = method.annotations[0]
                if (annotation is GET) {
                    value = (annotation).value
                    type = "GET"
                } else if (annotation is POST) {
                    value = (annotation).value
                    type = "POST"
                }
                serviceMethodCache!![method.name] = RequestMethodData(value!!, type!!, list)
            }
        }
        if (serviceMethodCache!!.isEmpty()) {
            throw RetrofitException(service.name + " No network request was registered")
        }
    }

    /**
     * 获取指定方法的参数名
     *
     * @param method 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表
     */
    private fun getMethodParameterNamesByAnnotation(method: Method): List<String> {
        val list: MutableList<String> = ArrayList()
        val parameterAnnotations = method.parameterAnnotations
        if (parameterAnnotations.isEmpty()) {
            return list
        }
        for (parameterAnnotation in parameterAnnotations) {
            for (annotation in parameterAnnotation) {
                if (annotation is Param) {
                    val param: Param = annotation as Param
                    list.add(param.value)
                }
            }
        }
        return list
    }

    class Builder {
        private var httpUrl: String? = null
        fun baseUrl(httpUrl: String?): Builder {
            this.httpUrl = httpUrl
            return this
        }

        fun build(): Retrofit {
            return Retrofit(httpUrl)
        }
    }
}