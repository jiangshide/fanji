package com.fanji.android.net.okhttp

import android.os.Handler
import com.fanji.android.util.LogUtil
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * created by jiangshide on 2020/5/6.
 * email:18311271399@163.com
 */
object WebSocketHandler : WebSocketListener() {

    private val reentrantLock: ReentrantLock = ReentrantLock()
    private val condition: Condition = reentrantLock.newCondition()

    @Volatile
    private var mUrl: String? = null

    @Volatile
    private var webSocket: WebSocket? = null

    @Volatile
    private var mSocketIOCallBack: WebSocketCallBack? = null

    @Volatile
    var connectStatus: ConnectStatus? = null
        private set

    @Volatile
    private var okHttpClient: OkHttpClient? = null

    private fun signal() {
        reentrantLock.lock()
        condition.signal()
        reentrantLock.unlock()
    }

    private fun await(timeOut: Long) {
        reentrantLock.lock()
        try {
            if (timeOut == -1L) {
                condition.await()
            }
        } finally {
            reentrantLock.unlock()
        }
    }

    fun register(url: String?, callBack: WebSocketCallBack?): WebSocketHandler {
        this.mUrl = url
        this.mSocketIOCallBack = callBack
        connect()
        return this
    }

    fun connect(): WebSocketHandler {
        if (okHttpClient != null && webSocket != null) {
            webSocket = okHttpClient?.newWebSocket(webSocket!!.request(), this)
            return this
        }
        val request = Request.Builder().url(mUrl!!).build()
        okHttpClient = OkHttpClient.Builder().pingInterval(40, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS).build()
        webSocket = okHttpClient?.newWebSocket(request, this)
        connectStatus = ConnectStatus.Connecting
        LogUtil.e("-------webSocket:", webSocket, " | state:", connectStatus, " | url:", mUrl)
        return this
    }

    fun send(str: String?): WebSocketHandler {
        if (webSocket != null) {
            webSocket!!.send(str!!)
        }
        return this
    }

    fun cancel() {
        if (webSocket != null) {
            webSocket!!.cancel()
        }
    }

    fun close() {
        if (webSocket != null) {
            webSocket!!.close(1000, "the client close!")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        LogUtil.e("onOpen")
        connectStatus = ConnectStatus.Open
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack!!.onOpen()
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        LogUtil.e("onMessage: $text")
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack!!.onMessage(text)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        connectStatus = ConnectStatus.Closing
        LogUtil.e("onClosing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        LogUtil.e("onClosed")
        connectStatus = ConnectStatus.Closed
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack!!.onClose()
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        LogUtil.e("onFailure~webSocket:$webSocket | t:$t | response:$response")
        connectStatus = ConnectStatus.Canceled
        if (mSocketIOCallBack != null) {
            mSocketIOCallBack!!.onConnectError(t)
        }

        connect()
    }

    fun removeSocketIOCallBack() {
        mSocketIOCallBack = null
    }

    enum class ConnectStatus {
        Connecting,  // the initial state of each web socket.
        Open,  // the web socket has been accepted by the remote peer
        Closing,  // one of the peers on the web socket has initiated a graceful shutdown
        Closed,  //  the web socket has transmitted all of its messages and has received all messages from the peer
        Canceled // the web socket connection failed
    }

    interface WebSocketCallBack {
        fun onOpen()
        fun onMessage(str: String?)
        fun onClose()
        fun onConnectError(t: Throwable?)
    }
}