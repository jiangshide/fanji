package com.fanji.android.util;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * @author: jiangshide
 * @date: 2023/8/24
 * @email: 18311271399@163.com
 * @description:
 */
public class ValidateCodeUtil {

    private ICountDown mICountDown;
    private volatile int mTime;
    private volatile boolean mCancel;
    private final int SECOND = 1000;

    private static ValidateCodeUtil instance;

    private ValidateCodeUtil() {
    }

    public static ValidateCodeUtil getInstance() {
        if (instance == null) {
            synchronized (ValidateCodeUtil.class) {
                if (instance == null) {
                    instance = new ValidateCodeUtil();
                }
            }
        }
        return instance;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mICountDown != null) {
                mICountDown.onCountDown(msg.what);
            }
        }
    };

    public void countDownSecond(ICountDown iCountDown) {
        if (this.mTime == 0) {
            return;
        }
        this.countDownSecond(iCountDown, this.mTime);
    }

    public void countDownSecond(ICountDown iCountDown, int time) {
        this.mICountDown = iCountDown;
        this.mTime = time;
        this.mCancel = false;
        mHandler.postDelayed(mRunnable, SECOND);
    }

    public void cancel() {
        this.mCancel = true;
        mHandler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.e("----jsd---mTime:" + mTime + " | mCancel:" + mCancel);
            if (mTime == 0 || mCancel) {
                return;
            }
            mTime--;
            mHandler.sendEmptyMessage(mTime);
            mHandler.postDelayed(mRunnable, SECOND);
        }
    };

    public interface ICountDown {
        public void onCountDown(int code);
    }
}
