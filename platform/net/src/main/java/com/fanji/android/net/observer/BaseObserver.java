package com.fanji.android.net.observer;

import android.content.Context;

import com.fanji.android.net.exception.NetException;
import com.fanji.android.net.state.NetState;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by jiangshide on 2019-07-31.
 * email:18311271399@163.com
 */
public class BaseObserver<T> implements Observer<T> {

    public Context getContext() {
        return mContext;
    }

    protected Context mContext;

    public BaseObserver() {
        this(null);
    }

    public BaseObserver(Context context) {
        super();
        if (context == null) {
            context = NetState.Companion.getInstance().getContext().getApplicationContext();
        } else {
            this.mContext = context.getApplicationContext();
        }
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {
        if (e != null && e instanceof NetException) {
            onFail((NetException) e);
        } else {
            onFail(new NetException(e));
        }
    }

    public void onFail(NetException e) {
    }

    @Override
    public void onComplete() {
    }
}