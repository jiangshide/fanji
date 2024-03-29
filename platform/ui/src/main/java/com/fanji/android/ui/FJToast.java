package com.fanji.android.ui;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;

import com.fanji.android.util.AppUtil;
import com.fanji.android.util.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * created by jiangshide on 2019-07-24.
 * email:18311271399@163.com
 */
public class FJToast extends Toast {

    private static FJDialog mZdDialog;
    private static Handler mHandler = new Handler();
    private static final int DEFAULT_DURATION = 3;
    private static PopupWindow mPmpupWindow;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link AppCompatActivity} object.
     */
    public FJToast(Context context) {
        super(context);
    }

    @IntDef({Duration.SHORT, Duration.LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
        int SHORT = 0, LONG = 1;
    }

    /**
     * Toast显示
     *
     * @param text 显示内容
     */
    public static void txt(String text) {
        txt(text, Duration.LONG, null);
    }

    /**
     * Toast显示
     * OnToastListener listener
     *
     * @param text 显示内容
     */
    public static void txt(String text, OnToastListener listener) {
        txt(text, Duration.SHORT, listener);
    }


    /**
     * Toast显示
     *
     * @param resId 显示内容资源
     */
    public static void txt(int resId) {
        txt(AppUtil.getApplicationContext().getString(resId));
    }

    /**
     * Toast显示:可以控制显示时间
     *
     * @param resId    显示内容资源
     * @param duration 显示时间
     */
    public static void txt(int resId, int duration) {
        txt(AppUtil.getApplicationContext().getString(resId), duration, null);
    }

    /**
     * Toast显示:可以控制显示时间
     *
     * @param text     显示内容
     * @param duration 显示时间
     */
    public static void txt(String text, @Duration Integer duration, OnToastListener listener) {
        Toast toast = makeText(AppUtil.getApplicationContext(), text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        View view = toast.getView();
        LogUtil.e("view:"+view);
        if(view == null)return;
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (listener != null) {
                    listener.onClose();
                }
            }
        });
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param layout 图片View
     */
    public static void view(int layout) {
        view(LayoutInflater.from(AppUtil.getApplicationContext()).inflate(layout, null));
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param view 图片View
     */
    public static void view(View view) {
        view(Gravity.CENTER, 10, 10, Duration.LONG, view);
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param layout 图片View
     */
    public static void view(int gravity, int layout) {
        view(gravity, LayoutInflater.from(AppUtil.getApplicationContext()).inflate(layout, null));
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param view 图片View
     */
    public static void view(int gravity, View view) {
        view(gravity, 10, 10, Duration.LONG, view);
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param layout 图片View
     */
    public static void view(int gravity, @Duration Integer duration, int layout) {
        view(gravity, duration,
                LayoutInflater.from(AppUtil.getApplicationContext()).inflate(layout, null));
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param view 图片View
     */
    public static void view(int gravity, @Duration Integer duration, View view) {
        view(gravity, 10, 10, duration, view);
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param layout 图片View
     */
    public static void view(int gravity, int xOffset, int yOffset, int layout) {
        view(gravity, xOffset, yOffset,
                LayoutInflater.from(AppUtil.getApplicationContext()).inflate(layout, null));
    }

    /**
     * Toast显示:可以显示图片等控件
     *
     * @param view 图片View
     */
    public static void view(int gravity, int xOffset, int yOffset, View view) {
        view(gravity, xOffset, yOffset, Duration.LONG, view);
    }

    /**
     * Toast显示:可以显示图片等控件、控制显示时间
     *
     * @param duration 显示时间
     * @param layout   图片View
     */
    public static void view(int gravity, int xOffset, int yOffset, @Duration Integer duration,
                            int layout) {
        view(gravity, xOffset, yOffset, duration,
                LayoutInflater.from(AppUtil.getApplicationContext()).inflate(layout, null));
    }

    /**
     * Toast显示:可以显示图片等控件、控制显示时间
     *
     * @param duration 显示时间
     * @param view     图片View
     */
    public static void view(int gravity, int xOffset, int yOffset, @Duration Integer duration,
                            View view) {
        FJToast zdToast = new FJToast(AppUtil.getApplicationContext());
        zdToast.setGravity(gravity, xOffset, yOffset);
        zdToast.setDuration(duration);
        zdToast.setView(view);

        zdToast.show();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = 500;
        layoutParams.height = 100;
        view.setLayoutParams(layoutParams);
    }

    public static void fixTxt(Context context, int resTxt) {
        fixTxt(context, AppUtil.getApplicationContext().getString(resTxt), DEFAULT_DURATION);
    }

    public static void fixTxt(Context context, int resTxt, int time) {
        fixTxt(context, AppUtil.getApplicationContext().getString(resTxt), time);
    }

    public static void fixTxt(Context context, String txt) {
        fixTxt(context, -1, txt, DEFAULT_DURATION);
    }

    public static void fixTxt(Context context, String txt, int time) {
        fixTxt(context, -1, txt, time);
    }

    public static void fixTxt(Context context, int resImg, String txt) {
        fixTxt(context, resImg, txt, R.style.topAnim, DEFAULT_DURATION);
    }

    public static void fixTxt(Context context, int resImg, String txt, int time) {
        fixTxt(context, resImg, txt, R.style.topAnim, time);
    }

    public static void fixTxt(Context context, int resImg, String txt, int anim, int time) {
        fixTxt(context, resImg, txt, Gravity.TOP, anim, time);
    }

    public static void fixTxt(Context context, int resImg, String txt, int gravity, int anim,
                              int time) {
        fixTxt(context, resImg, txt, R.style.DialogThemeAlpha, gravity, anim, time);
    }

    public static void fixTxt(Context context, int resImg, String txt, int theme, int gravity,
                              int anim, int time) {
        fixView(context,
                LayoutInflater.from(AppUtil.getApplicationContext()).inflate(R.layout.default_toast, null),
                resImg, txt, theme, gravity, anim, time, null);
    }

    public static void fixView(Context context, int layout) {
        fixView(context, LayoutInflater.from(AppUtil.getApplicationContext()).inflate(layout, null));
    }

    public static void fixView(Context context, View view) {
        fixView(context, view, R.style.DialogThemeAlpha);
    }

    public static void fixView(Context context, View view, int theme) {
        fixView(context, view, theme, Gravity.TOP);
    }

    public static void fixView(Context context, View view, int theme, int gravity) {
        fixView(context, view, theme, gravity, R.style.topAnim);
    }

    public static void fixView(Context context, View view, int theme, int gravity, int anim) {
        fixView(context, view, -1, null, theme, gravity, anim, DEFAULT_DURATION, null);
    }

    public static void fixView(Context context, View view, int theme, int gravity, int anim,
                               FJDialog.DialogViewListener dialogViewListener) {
        fixView(context, view, -1, null, theme, gravity, anim, DEFAULT_DURATION, dialogViewListener);
    }

    public static void fixView(Context context, View view, int theme, int gravity, int anim, int time,
                               FJDialog.DialogViewListener dialogViewListener) {
        fixView(context, view, -1, null, theme, gravity, anim, time, dialogViewListener);
    }

    public static void fixPop(AppCompatActivity activity, View view) {
        fixPop(activity, view, DEFAULT_DURATION);
    }

    public static void fixPop(AppCompatActivity activity, View view, int duration) {
        if (activity == null) return;
        cancelPop();
        mPmpupWindow = new PopupWindow();
        mPmpupWindow.setContentView(view);
        mPmpupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPmpupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPmpupWindow.setAnimationStyle(R.style.topAnim);
        mPmpupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP, 0, 0);
        if (duration == -1) return;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelPop();
            }
        }, duration * 1000);
    }

    public static void fixView(Context context, View view, final int resImg, final String txt,
                               int theme, int gravity, int anim, int time, FJDialog.DialogViewListener dialogViewListener) {
        cancelLoading();
        mZdDialog = new FJDialog(context, theme, view, new FJDialog.DialogViewListener() {
            @Override
            public void onView(View view) {
                if (resImg > 0) {
                    ((ImageView) view.findViewById(R.id.toastIcon)).setImageResource(resImg);
                }
                if (!TextUtils.isEmpty(txt)) {
                    ((TextView) view.findViewById(R.id.toastTxt)).setText(txt);
                }
            }
        }).setOutsideClose(true).setGravity(gravity).setAnim(anim);
        mZdDialog.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelLoading();
            }
        }, time * 1000);
    }

    public static void cancelToast() {
//        if (toast != null) {
//            toast.cancel();
//        }
//        toast = null;
    }

    public static void cancelLoading() {
        if (mZdDialog != null) {
            mZdDialog.dismiss();
            mZdDialog = null;
        }
    }

    public static void cancelPop() {
        if (mPmpupWindow != null) {
            try {
                mPmpupWindow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPmpupWindow = null;
        }
    }

    public interface OnToastListener {
        public void onClose();
    }
}
