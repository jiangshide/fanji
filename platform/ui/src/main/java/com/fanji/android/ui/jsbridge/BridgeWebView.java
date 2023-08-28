package com.fanji.android.ui.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.fanji.android.ui.jsbridge.interfaces.BridgeHandler;
import com.fanji.android.ui.jsbridge.interfaces.CallBackFunction;
import com.fanji.android.ui.jsbridge.interfaces.OnLoadChildListener;
import com.fanji.android.ui.jsbridge.interfaces.WebViewJavascriptBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements WebViewJavascriptBridge, NestedScrollingChild {

    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private List<Message> startupMessage = new ArrayList<Message>();

    private int mLastMotionY;

    private final int[] mScrollOffset = new int[2];
    private final int[] mScrollConsumed = new int[2];

    private int mNestedYOffset;

    private NestedScrollingChildHelper mChildHelper;

    private OnLoadChildListener mOnLoadChildListener;

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public BridgeWebView setShowUrlListener(OnLoadChildListener listener) {
        this.mOnLoadChildListener = listener;
        return this;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

    /**
     * @param handler default handler,handle messages send by js without assigned handler name,
     *                if js message has handler name, it will be handled by named handlers registered by native
     */
    public void setDefaultHandler(BridgeHandler handler) {
        this.defaultHandler = handler;
    }

    private void init() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.setWebViewClient(generateBridgeWebViewClient());

        mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        MotionEvent trackedEvent = MotionEvent.obtain(event);
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0;
        }

        int y = (int) event.getY();
        event.offsetLocation(0, mNestedYOffset);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;

                int nestedScrollAxis = ViewCompat.SCROLL_AXIS_VERTICAL;
                nestedScrollAxis |= ViewCompat.SCROLL_AXIS_HORIZONTAL;  //按位或运算

//                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                startNestedScroll(nestedScrollAxis);
                result = super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = mLastMotionY - y;

                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }

                int oldY = getScrollY();
                mLastMotionY = y - mScrollOffset[1];
                int newScrollY = Math.max(0, oldY + deltaY);
                deltaY -= newScrollY - oldY;
                if (dispatchNestedScroll(0, newScrollY - deltaY, 0, deltaY, mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1];
                    trackedEvent.offsetLocation(0, mScrollOffset[1]);
                    mNestedYOffset += mScrollOffset[1];
                }
                if (mScrollConsumed[1] == 0 && mScrollOffset[1] == 0) {
                    trackedEvent.recycle();
                    result = super.onTouchEvent(trackedEvent);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                stopNestedScroll();
                result = super.onTouchEvent(event);
                break;
        }
        return result;
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {//设置嵌套滑动是否可用
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {//嵌套滑动是否可用
        return mChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 开始嵌套滑动,
     *
     * @param axes 表示方向 有一下两种值
     *             ViewCompat.SCROLL_AXIS_HORIZONTAL 横向滑动
     *             ViewCompat.SCROLL_AXIS_VERTICAL 纵向滑动
     */
    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {//停止嵌套滑动
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {//是否有父View 支持 嵌套滑动,  会一层层的网上寻找父View
        return mChildHelper.hasNestedScrollingParent();
    }

    /**
     * 在处理滑动之后 调用
     *
     * @param dxConsumed     x轴上 被消费的距离
     * @param dyConsumed     y轴上 被消费的距离
     * @param dxUnconsumed   x轴上 未被消费的距离
     * @param dyUnconsumed   y轴上 未被消费的距离
     * @param offsetInWindow view 的移动距离
     * @return
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    /**
     * 一般在滑动之前调用, 在ontouch 中计算出滑动距离, 然后调用该方法, 就给支持的嵌套的父View 处理滑动事件
     *
     * @param dx             x 轴上滑动的距离, 相对于上一次事件, 不是相对于 down事件的 那个距离
     * @param dy             y 轴上滑动的距离
     * @param consumed       一个数组, 可以传 一个空的 数组,  表示 x 方向 或 y 方向的事件 是否有被消费
     * @param offsetInWindow 支持嵌套滑动到额父View 消费 滑动事件后 导致 本 View 的移动距离
     * @return 支持的嵌套的父View 是否处理了 滑动事件
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    /**
     * @param velocityX x 轴上的滑动速度
     * @param velocityY y 轴上的滑动速度
     * @param consumed  是否被消费
     * @return
     */
    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * @param velocityX x 轴上的滑动速度
     * @param velocityY y 轴上的滑动速度
     * @return
     */
    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    protected BridgeWebViewClient generateBridgeWebViewClient() {
        return new BridgeWebViewClient(this, new OnLoadChildListener() {
            @Override
            public void onChild(String url) {
                if (mOnLoadChildListener != null) {
                    mOnLoadChildListener.onChild(url);
                }
            }
        });
    }

    /**
     * 获取到CallBackFunction data执行调用并且从数据集移除
     *
     * @param url
     */
    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    /**
     * 保存message到消息队列
     *
     * @param handlerName      handlerName
     * @param data             data
     * @param responseCallback CallBackFunction
     */
    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    /**
     * list<message> != null 添加到消息集合否则分发消息
     *
     * @param m Message
     */
    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    /**
     * 分发message 必须在主线程才分发成功
     *
     * @param m Message
     */
    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string  为json字符串转义特殊字符
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\')", "\\\\\'");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    /**
     * 刷新消息队列
     */
    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage 反序列化消息
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response  CallBackFunction
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId 如果有回调Id
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            // BridgeHandler执行
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null) {
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }


    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        // 添加至 Map<String, CallBackFunction>
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     * 注册处理程序,以便javascript调用它
     *
     * @param handlerName handlerName
     * @param handler     BridgeHandler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            // 添加至 Map<String, BridgeHandler>
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * unregister handler
     *
     * @param handlerName
     */
    public void unregisterHandler(String handlerName) {
        if (handlerName != null) {
            messageHandlers.remove(handlerName);
        }
    }

    /**
     * call javascript registered handler
     * 调用javascript处理程序注册
     *
     * @param handlerName handlerName
     * @param data        data
     * @param callBack    CallBackFunction
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }
}
