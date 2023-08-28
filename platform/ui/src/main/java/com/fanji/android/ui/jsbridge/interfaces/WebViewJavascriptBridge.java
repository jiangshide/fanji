package com.fanji.android.ui.jsbridge.interfaces;


public interface WebViewJavascriptBridge {
    void send(String data);

    void send(String data, CallBackFunction responseCallback);
}
