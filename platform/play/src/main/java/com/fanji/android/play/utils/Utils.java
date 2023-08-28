package com.fanji.android.play.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Toast;

//import com.android.player.dplay.player.VideoView;
//import com.android.player.dplay.player.VideoViewConfig;
//import com.android.player.dplay.player.VideoViewManager;

import com.fanji.android.play.dplay.player.VideoView;
import com.fanji.android.play.dplay.player.VideoViewConfig;
import com.fanji.android.play.dplay.player.VideoViewManager;

import java.lang.reflect.Field;

public final class Utils {

    private Utils() {
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Is the live streaming still available
     * @return is the live streaming is available
     */
    public static boolean isLiveStreamingAvailable() {
        // Todo: Please ask your app server, is the live streaming still available
        return true;
    }

    public static void showToastTips(final Context context, final String tips) {
        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取当前的播放核心
     */
    public static Object getCurrentPlayerFactory() {
        VideoViewConfig config = VideoViewManager.getConfig();
        Object playerFactory = null;
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            playerFactory = mPlayerFactoryField.get(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    /**
     * 将View从父控件中移除
     */
    public static void removeViewFormParent(View v) {
        if (v == null) return;
        ViewParent parent = v.getParent();
        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeView(v);
        }
    }

    /**
     * Returns a string containing player state debugging information.
     */
    public static String playState2str(int state) {
        String playStateString;
        switch (state) {
            default:
            case VideoView.STATE_IDLE:
                playStateString = "idle";
                break;
            case VideoView.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case VideoView.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case VideoView.STATE_PLAYING:
                playStateString = "playing";
                break;
            case VideoView.STATE_PAUSED:
                playStateString = "pause";
                break;
            case VideoView.STATE_BUFFERING:
                playStateString = "buffering";
                break;
            case VideoView.STATE_BUFFERED:
                playStateString = "buffered";
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case VideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("playState: %s", playStateString);
    }

    /**
     * Returns a string containing player state debugging information.
     */
    public static String playerState2str(int state) {
        String playerStateString;
        switch (state) {
            default:
            case VideoView.PLAYER_NORMAL:
                playerStateString = "normal";
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                playerStateString = "full screen";
                break;
            case VideoView.PLAYER_TINY_SCREEN:
                playerStateString = "tiny screen";
                break;
        }
        return String.format("playerState: %s", playerStateString);
    }

}
