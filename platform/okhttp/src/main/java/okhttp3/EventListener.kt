/*
 * Copyright (C) 2017 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3

import androidx.annotation.Nullable
import com.fanji.android.util.LogUtil
import com.fanji.android.util.ThreadPoolUtil
import okhttp3.data.CONNECTED
import okhttp3.data.CONNECTING
import okhttp3.data.CONNECT_FAIL
import okhttp3.data.EventData
import okhttp3.room.SaveEventRunnable
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Listener for metrics events. Extend this class to monitor the quantity, size, and duration of
 * your application's HTTP calls.
 *
 * All start/connect/acquire events will eventually receive a matching end/release event, either
 * successful (non-null parameters), or failed (non-null throwable). The first common parameters of
 * each event pair are used to link the event in case of concurrent or repeated events e.g.
 * `dnsStart(call, domainName)` → `dnsEnd(call, domainName, inetAddressList)`.
 *
 * Events are typically nested with this structure:
 *
 *  * call ([callStart], [callEnd], [callFailed])
 *    * proxy selection ([proxySelectStart], [proxySelectEnd])
 *    * dns ([dnsStart], [dnsEnd])
 *    * connect ([connectStart], [connectEnd], [connectFailed])
 *      * secure connect ([secureConnectStart], [secureConnectEnd])
 *    * connection held ([connectionAcquired], [connectionReleased])
 *      * request ([requestFailed])
 *        * headers ([requestHeadersStart], [requestHeadersEnd])
 *        * body ([requestBodyStart], [requestBodyEnd])
 *      * response ([responseFailed])
 *        * headers ([responseHeadersStart], [responseHeadersEnd])
 *        * body ([responseBodyStart], [responseBodyEnd])
 *
 * This nesting is typical but not strict. For example, when calls use "Expect: continue" the
 * request body start and end events occur within the response header events. Similarly,
 * [duplex calls][RequestBody.isDuplex] interleave the request and response bodies.
 *
 * Since connections may be reused, the proxy selection, DNS, and connect events may not be present
 * for a call. In future releases of OkHttp these events may also occur concurrently to permit
 * multiple routes to be attempted simultaneously.
 *
 * Events and sequences of events may be repeated for retries and follow-ups.
 *
 * All event methods must execute fast, without external locking, cannot throw exceptions, attempt
 * to mutate the event parameters, or be re-entrant back into the client. Any IO - writing to files
 * or network should be done asynchronously.
 */
abstract class EventListener : EventData() {

    val CALL_START = 1

    val PROXY_SELECT_START = 2
    val PROXY_SELECT_END = 3

    val DNS_START = 4
    val DNS_END = 5

    val CONNECT_START = 6
    val SECURE_CONNECT_START = 7
    val SECURE_CONNECT_END = 8

    val CONNECT_END = 9
    val CONNECT_FAILED = 10

    val CONNECTION_ACQUIRED = 11
    val CONNECTION_RELEASED = 12

    val REQUEST_HEADERS_START = 13
    val REQUEST_HEADERS_END = 14
    val REQUEST_BODY_START = 15
    val REQUEST_BODY_END = 16
    val REQUEST_FAILED = 17

    val RESPONSE_HEADERS_START = 18
    val RESPONSE_HEADERS_END = 19
    val RESPONSE_BODY_START = 20
    val RESPONSE_BODY_END = 21
    val RESPONSE_FAILED = 22

    val CALL_END = 23
    val CALL_FAILED = 24

    val CANCELED = 25

    val SATISFACTION_FAILURE = 26

    val CACHE_HIT = 27

    val CACHE_MISS = 28

    val CACHE_CONDITIONAL_HIT = 29

    val SMALLEST = 10

    private var addressList: List<InetAddress>? = null

    var saveEventDb: Boolean = false

    // request method
    val METHOD_HEAD = "HEAD"

    val IF_MATCH = "If-Match"
    val USER_AGENT = "User-Agent"

    // response header fields.
    val CONTENT_LENGTH = "Content-Length"
    val CONTENT_RANGE = "Content-Range"
    val ETAG = "Etag"
    val TRANSFER_ENCODING = "Transfer-Encoding"
    val ACCEPT_RANGES = "Accept-Ranges"
    val CONTENT_DISPOSITION = "Content-Disposition"

    // response header value.
    val CHUNKED = "chunked"

    val NO_CONTENT_LENGTH = -1L

    // response special code.
    val RANGE_NOT_SATISFIABLE = 416

    fun setClient(client: OkHttpClient) {
        this.saveEventDb = client.saveEventDb
        connectTimeout = client.readTimeoutMillis
        readTimeout = client.readTimeoutMillis
        writeTimeout = client.writeTimeoutMillis
        pingInterval = client.pingIntervalMillis
        netState = client.netState
        netLevel = client.netLevel
        netSpeed = client.netSpeed
    }

    /**
     * Invoked as soon as a call is enqueued or executed by a client. In case of thread or stream
     * limits, this call may be executed well before processing the request is able to begin.
     *
     * This will be invoked only once for a single [Call]. Retries of different routes or redirects
     * will be handled within the boundaries of a single [callStart] and [callEnd]/[callFailed] pair.
     */
    open fun callStart(
        call: Call
    ) {
        val request = call.request()
        url = request.url.toString()
        method = request.method
        host = request.url.host
        port = request.url.port

        var path = ""
        request.url.pathSegments?.forEach {
            path += "/$it"
        }
        this.path = path
        startTime = System.currentTimeMillis()
        event(CALL_START)
    }

    /**
     * Invoked prior to a proxy selection.
     *
     * This will be invoked for route selection regardless of whether the client
     * is configured with a single proxy, a proxy selector, or neither.
     *
     * @param url a URL with only the scheme, hostname, and port specified.
     */
    open fun proxySelectStart(
        call: Call,
        url: HttpUrl
    ) {
        event(PROXY_SELECT_START)
    }

    /**
     * Invoked after proxy selection.
     *
     * Note that the list of proxies is never null, but it may be a list containing
     * only [Proxy.NO_PROXY]. This comes up in several situations:
     *
     * * If neither a proxy nor proxy selector is configured.
     * * If the proxy is configured explicitly as [Proxy.NO_PROXY].
     * * If the proxy selector returns only [Proxy.NO_PROXY].
     * * If the proxy selector returns an empty list or null.
     *
     * Otherwise it lists the proxies in the order they will be attempted.
     *
     * @param url a URL with only the scheme, hostname, and port specified.
     */
    open fun proxySelectEnd(
        call: Call,
        url: HttpUrl,
        proxies: List<@JvmSuppressWildcards Proxy>
    ) {
        name = url.username
        psw = url.password
        event(PROXY_SELECT_END)
    }

    /**
     * Invoked just prior to a DNS lookup. See [Dns.lookup].
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different host.
     *
     * If the [Call] is able to reuse an existing pooled connection, this method will not be invoked.
     * See [ConnectionPool].
     */
    open fun dnsStart(
        call: Call,
        domainName: String
    ) {
        event(DNS_START)
    }

    /**
     * Invoked immediately after a DNS lookup.
     *
     * This method is invoked after [dnsStart].
     */
    open fun dnsEnd(
        call: Call,
        domainName: String,
        inetAddressList: List<@JvmSuppressWildcards InetAddress>
    ) {
        addressList = inetAddressList
        if (inetAddressList.isNotEmpty()) {
            dnsSize = inetAddressList.size
            ip = inetAddressList[0].hostAddress
        }
        event(DNS_END)
    }

    /**
     * Invoked just prior to initiating a socket connection.
     *
     * This method will be invoked if no existing connection in the [ConnectionPool] can be reused.
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different address, or a connection is retried.
     */
    open fun connectStart(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy
    ) {
        ip = inetSocketAddress.address.hostAddress
        addressList?.forEachIndexed { index, inetAddress ->
            if (inetAddress == inetSocketAddress.address) {
                position = index
            }
        }
        proxy.type().name
        port = inetSocketAddress.port
        status = CONNECTING
        event(CONNECT_START)
    }

    /**
     * Invoked just prior to initiating a TLS connection.
     *
     * This method is invoked if the following conditions are met:
     *
     *  * The [Call.request] requires TLS.
     *
     *  * No existing connection from the [ConnectionPool] can be reused.
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different address, or a connection is retried.
     */
    open fun secureConnectStart(
        call: Call
    ) {
        event(SECURE_CONNECT_START)
    }

    /**
     * Invoked immediately after a TLS connection was attempted.
     *
     * This method is invoked after [secureConnectStart].
     */
    open fun secureConnectEnd(
        call: Call,
        handshake: Handshake?
    ) {
        tls = handshake?.tlsVersion?.name
        event(SECURE_CONNECT_END)
    }

    /**
     * Invoked immediately after a socket connection was attempted.
     *
     * If the `call` uses HTTPS, this will be invoked after [secureConnectEnd], otherwise it will
     * invoked after [connectStart].
     */
    open fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        ip = inetSocketAddress.address?.hostAddress
        port = inetSocketAddress.port
        protocolType = protocol?.name
        status = CONNECTED
        event(CONNECT_END)
    }

    /**
     * Invoked when a connection attempt fails. This failure is not terminal if further routes are
     * available and failure recovery is enabled.
     *
     * If the `call` uses HTTPS, this will be invoked after [secureConnectEnd], otherwise it will
     * invoked after [connectStart].
     */
    open fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        ip = inetSocketAddress.address?.hostAddress
        port = inetSocketAddress.port
        status = CONNECT_FAIL
        event(CONNECT_FAILED, ioe, "connectFailed")
    }

    /**
     * Invoked after a connection has been acquired for the `call`.
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response
     * to the [Call.request] is a redirect to a different address.
     */
    open fun connectionAcquired(
        call: Call,
        connection: Connection
    ) {
        ip = connection.route().socketAddress.address?.hostAddress
        port = connection.route().socketAddress.port
        event(CONNECTION_ACQUIRED)
    }

    /**
     * Invoked after a connection has been released for the `call`.
     *
     * This method is always invoked after [connectionAcquired].
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different address.
     */
    open fun connectionReleased(
        call: Call,
        connection: Connection
    ) {
        event(CONNECTION_RELEASED)
    }

    /**
     * Invoked just prior to sending request headers.
     *
     * The connection is implicit, and will generally relate to the last [connectionAcquired] event.
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different address.
     */
    open fun requestHeadersStart(
        call: Call
    ) {
        requestHeadersSize = call.request().headers.size
        requestHeadersCount = call.request().headers.byteCount()
        event(REQUEST_HEADERS_START)
    }

    /**
     * Invoked immediately after sending request headers.
     *
     * This method is always invoked after [requestHeadersStart].
     *
     * @param request the request sent over the network. It is an error to access the body of this
     *     request.
     */
    open fun requestHeadersEnd(call: Call, request: Request) {
        requestHeadersSize = request.headers.size
        requestHeadersCount = request.headers.byteCount()
        event(REQUEST_HEADERS_END)
    }

    /**
     * Invoked just prior to sending a request body.  Will only be invoked for request allowing and
     * having a request body to send.
     *
     * The connection is implicit, and will generally relate to the last [connectionAcquired] event.
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different address.
     */
    open fun requestBodyStart(
        call: Call
    ) {
        requestBodySize = call.request().body?.contentLength()
        event(REQUEST_BODY_START)
    }

    /**
     * Invoked immediately after sending a request body.
     *
     * This method is always invoked after [requestBodyStart].
     */
    open fun requestBodyEnd(
        call: Call,
        byteCount: Long
    ) {
        requestBodySize = byteCount
        event(REQUEST_BODY_END)
    }

    /**
     * Invoked when a request fails to be written.
     *
     * This method is invoked after [requestHeadersStart] or [requestBodyStart]. Note that request
     * failures do not necessarily fail the entire call.
     */
    open fun requestFailed(
        call: Call,
        ioe: IOException
    ) {
        event(REQUEST_FAILED, ioe, "requestFailed")
    }

    /**
     * Invoked when response headers are first returned from the server.
     *
     * The connection is implicit, and will generally relate to the last [connectionAcquired] event.
     *
     * This can be invoked more than 1 time for a single [Call]. For example, if the response to the
     * [Call.request] is a redirect to a different address.
     *
     * Prior to OkHttp 4.3 this was incorrectly invoked when the client was ready to read headers.
     * This was misleading for tracing because it was too early.
     */
    open fun responseHeadersStart(
        call: Call
    ) {
        event(RESPONSE_HEADERS_START)
    }

    /**
     * Invoked immediately after receiving response headers.
     *
     * This method is always invoked after [responseHeadersStart].
     *
     * @param response the response received over the network. It is an error to access the body of
     *     this response.
     */
    open fun responseHeadersEnd(
        call: Call,
        response: Response
    ) {
        code = "${response.code}"
        responseHeadersSize = response.headers.size
        responseHeadersCount = response.headers.byteCount()
        contentLength = response?.header(CONTENT_LENGTH)?.toLong()!!
        if (contentLength == NO_CONTENT_LENGTH || contentLength < 10) {
            contentLength = parseContentRangeFoInstanceLength(response.header(CONTENT_RANGE))
        }

        event(RESPONSE_HEADERS_END)
    }

    private fun parseContentRangeFoInstanceLength(@Nullable contentRange: String?): Long {
        if (contentRange == null) return NO_CONTENT_LENGTH
        val session = contentRange.split("/").toTypedArray()
        if (session.size >= 2) {
            try {
                return session[1].toLong()
            } catch (e: NumberFormatException) {
                LogUtil.e(
                    "parse instance length failed with $contentRange"
                )
            }
        }
        return NO_CONTENT_LENGTH
    }

    /**
     * Invoked when data from the response body is first available to the application.
     *
     * This is typically invoked immediately before bytes are returned to the application. If the
     * response body is empty this is invoked immediately before returning that to the application.
     *
     * If the application closes the response body before attempting a read, this is invoked at the
     * time it is closed.
     *
     * The connection is implicit, and will generally relate to the last [connectionAcquired] event.
     *
     * This will usually be invoked only 1 time for a single [Call], exceptions are a limited set of
     * cases including failure recovery.
     *
     * Prior to OkHttp 4.3 this was incorrectly invoked when the client was ready to read the response
     * body. This was misleading for tracing because it was too early.
     */
    open fun responseBodyStart(
        call: Call
    ) {
        event(RESPONSE_BODY_START)
    }

    /**
     * Invoked immediately after receiving a response body and completing reading it.
     *
     * Will only be invoked for requests having a response body e.g. won't be invoked for a web socket
     * upgrade.
     *
     * If the response body is closed before the response body is exhausted, this is invoked at the
     * time it is closed. In such calls [byteCount] is the number of bytes returned to the
     * application. This may be smaller than the resource's byte count if were read to completion.
     *
     * This method is always invoked after [responseBodyStart].
     */
    open fun responseBodyEnd(
        call: Call,
        byteCount: Long
    ) {
        responseBodySize = byteCount
        event(RESPONSE_BODY_END)
    }

    /**
     * Invoked when a response fails to be read.
     *
     * Note that response failures do not necessarily fail the entire call.
     *
     * Starting with OkHttp 4.3 this may be invoked without a prior call to [responseHeadersStart]
     * or [responseBodyStart]. In earlier releases this method was documented to only be invoked after
     * one of those methods.
     */
    open fun responseFailed(
        call: Call,
        ioe: IOException
    ) {
        event(RESPONSE_FAILED, ioe, "responseFailed")
    }

    /**
     * Invoked immediately after a call has completely ended.  This includes delayed consumption
     * of response body by the caller.
     *
     * This method is always invoked after [callStart].
     */
    open fun callEnd(
        call: Call
    ) {
        event(CALL_END)
    }

    /**
     * Invoked when a call fails permanently.
     *
     * This method is always invoked after [callStart].
     */
    open fun callFailed(
        call: Call,
        ioe: IOException
    ) {
        event(CALL_FAILED, ioe, "callFailed")
    }

    /**
     * Invoked when a call is canceled.
     *
     * Like all methods in this interface, this is invoked on the thread that triggered the event. But
     * while other events occur sequentially; cancels may occur concurrently with other events. For
     * example, thread A may be executing [responseBodyStart] while thread B executes [canceled].
     * Implementations must support such concurrent calls.
     *
     * Note that cancellation is best-effort and that a call may proceed normally after it has been
     * canceled. For example, happy-path events like [requestHeadersStart] and [requestHeadersEnd] may
     * occur after a call is canceled. Typically cancellation takes effect when an expensive I/O
     * operation is required.
     *
     * This is invoked at most once, even if [Call.cancel] is invoked multiple times. It may be
     * invoked at any point in a call's life, including before [callStart] and after [callEnd].
     */
    open fun canceled(
        call: Call
    ) {
        event(CANCELED)
    }

    /**
     * Invoked when a call fails due to cache rules.
     * For example, we're forbidden from using the network and the cache is insufficient
     */
    open fun satisfactionFailure(call: Call, response: Response) {
        val length = response?.body?.contentLength()
        if (length != null && length > SMALLEST) {
            contentLength = length
        }
        event(SATISFACTION_FAILURE)
    }

    /**
     * Invoked when a result is served from the cache. The Response provided is the top level
     * Response and normal event sequences will not be received.
     *
     * This event will only be received when a Cache is configured for the client.
     */
    open fun cacheHit(call: Call, response: Response) {
        val length = response?.body?.contentLength()
        if (length != null && length > SMALLEST) {
            contentLength = length
        }
        event(CACHE_HIT)
    }

    /**
     * Invoked when a response will be served from the network. The Response will be
     * available from normal event sequences.
     *
     * This event will only be received when a Cache is configured for the client.
     */
    open fun cacheMiss(call: Call) {
        event(CACHE_MISS)
    }

    /**
     * Invoked when a response will be served from the cache or network based on validating the
     * cached Response freshness. Will be followed by cacheHit or cacheMiss after the network
     * Response is available.
     *
     * This event will only be received when a Cache is configured for the client.
     */
    open fun cacheConditionalHit(call: Call, cachedResponse: Response) {
        val length = cachedResponse?.body?.contentLength()
        if (length != null && length > SMALLEST) {
            contentLength = length
        }
        event(CACHE_CONDITIONAL_HIT)
    }

    private fun event(step: Int, exception: IOException? = null, event: String? = "") {
        this.step = step
        this.endTime = System.currentTimeMillis()
        this.rtt = endTime - startTime
        if (exception != null) {
            this.exception = event + ":" + exception.message
            code = exception.javaClass.canonicalName
        }
        event(this)
    }

    open fun event(data: EventData) {
        if (saveEventDb) {
            ThreadPoolUtil.addTask("saveEventDb", SaveEventRunnable(data))
        }
    }

    fun interface Factory {
        /**
         * Creates an instance of the [EventListener] for a particular [Call]. The returned
         * [EventListener] instance will be used during the lifecycle of [call].
         *
         * This method is invoked after [call] is created. See [OkHttpClient.newCall].
         *
         * **It is an error for implementations to issue any mutating operations on the [call] instance
         * from this method.**
         */
        fun create(call: Call): EventListener
    }

    companion object {
        @JvmField
        val NONE: EventListener = object : EventListener() {
        }
    }
}

const val RANGE = "Range"