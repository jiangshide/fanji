package com.fanji.android.util;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * @Author:jiangshide
 * @Date:8/27/23
 * @Email:18311271399@163.com
 * @Description:
 */
final public class CountDownUtil {

    private static ICountDown mICountDown;

    private static volatile int mTime;

    private static volatile boolean mCancel;

    private static final int SECOND = 1000;


    private static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(mICountDown != null){
                mICountDown.onCountDown(msg.what);
            }
        }
    };

    public static void countDownSecond(ICountDown iCountDown) {
        if (mTime == 0) {
            return;
        }
        countDownSecond(iCountDown, mTime);
    }

    public static void countDownSecond(ICountDown iCountDown, int time) {
        cancel();
        mICountDown = iCountDown;
        mTime = time;
        mCancel = false;
        mHandler.postDelayed(mRunnable, SECOND);
    }

    public static void cancel() {
        mCancel = true;
        mHandler.removeCallbacks(mRunnable);
    }

    private static Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mTime == 0 || mCancel) {
                return;
            }
            mTime--;
            mHandler.sendEmptyMessage(mTime);
            mHandler.postDelayed(mRunnable, SECOND);
        }
    };

    public interface ICountDown {
        void onCountDown(int time);
    }
}
