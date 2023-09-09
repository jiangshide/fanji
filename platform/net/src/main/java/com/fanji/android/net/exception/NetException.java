package com.fanji.android.net.exception;

import com.fanji.android.util.LogUtil;

import java.net.ConnectException;

/**
 * created by jiangshide on 2019-10-08.
 * email:18311271399@163.com
 */
public class NetException extends RuntimeException {

    public String msg;
    public int code = -1;
    public Throwable throwable;
    public static int CONNECT_FAIL = -100;

    public NetException(Throwable throwable) {
        super();
        this.throwable = throwable;
        if (throwable instanceof ConnectException) {
            code = CONNECT_FAIL;
        }
        if (throwable != null) {
            this.msg = throwable.getMessage();
        }
    }

    public NetException(String msg, int code) {
        super();
        this.msg = msg;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "HttpException{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", throwable=" + throwable +
                '}';
    }
}
