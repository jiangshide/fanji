package com.fanji.android.net.retrofit.data

/**
 * created by jiangshide on 5/28/21.
 * email:18311271399@163.com
 */
data class CallData(
    private val baseUrl: String,
    private val data: RequestMethodData,
    private val args: Array<Any>
) {
    private var callMap: HashMap<String, Any> = HashMap()

    init {
        data.paramList.forEachIndexed { index, param ->
            callMap[param] = args[index]
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CallData

        if (baseUrl != other.baseUrl) return false
        if (data != other.data) return false
        if (!args.contentEquals(other.args)) return false
        if (callMap != other.callMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = baseUrl.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + args.contentHashCode()
        result = 31 * result + callMap.hashCode()
        return result
    }


}