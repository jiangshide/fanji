package com.fanji.android.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.fanji.android.util.AppUtil;
import com.fanji.android.util.LogUtil;

/**
 * created by jiangshide on 2019-09-05.
 * email:18311271399@163.com
 */
public class FJNotification {

  private static class ZdNotificationHolder {
    private static FJNotification instance = new FJNotification();
  }

  public static FJNotification getInstance() {
    return ZdNotificationHolder.instance;
  }

  private int defaultNotificationId = 0x1000000;//默认通知Id

  private int mNotificationId = defaultNotificationId;

  /**
   * 默认通道id
   */
  private String channelId = "1";

  public static final String NOTIFICATION = "notification";

  /**
   * 默认通道名称
   */
  public String channelName = "sanskrit";
  private Context context;
  public static int REQUEST = 1;
  public int iconId = AppUtil.getIcon();
  public int mLargeId = AppUtil.getIcon();
  public boolean mIsClear;

  public int lightColor = 0xffff0000;//blue
  public int lightTime = 500;
  public int unLightTime = 100;

  public int vibrate = 1;//the vibrate with time second

  private int mPriority = NotificationCompat.PRIORITY_DEFAULT;

  public static final String ACTION = "com.android.notification";
  public static final String NOTIFICATION_MSG = "notificationMsg";

  public NotificationManager notificationManager;
  private NotificationCompat.Builder builder;

  private MediaPlayer mediaPlayer;

  private Service mService;

  private String mTag;

  public NotificationManager build() {
    notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel
          channel =
          new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
      channel.enableLights(true); //是否在桌面icon右上角展示小红点
      channel.setLightColor(Color.RED); //小红点颜色
      channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
      notificationManager.createNotificationChannel(channel);
    }
    builder.setChannelId(channelId);
    builder.setSmallIcon(iconId);
    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), mLargeId));
    Notification notification = builder.build();
    if (mService != null) {
      mService.startForeground(mNotificationId,notification);
    }
    notification.flags = mIsClear ? Notification.FLAG_AUTO_CANCEL
        : Notification.FLAG_NO_CLEAR | Notification.FLAG_SHOW_LIGHTS;
//    notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
    builder.setPublicVersion(notification);//锁屏时显示通知
    builder.setPriority(mPriority);
    if(!TextUtils.isEmpty(mTag)){
      notificationManager.notify(mTag,mNotificationId,
              notification);
    }else{
      notificationManager.notify(mNotificationId,
              notification);
    }
//    defaultNotificationId++;
//    if (defaultNotificationId == mNotificationId) {
//      defaultNotificationId++;
//    }
    return notificationManager;
  }

  //public Notification getNotification() {
  //  return create().build().builder.build();
  //}

  public FJNotification create() {
    return create(AppUtil.getAppName());
  }

  public FJNotification create(String channelID) {
    this.context = AppUtil.getApplicationContext();
    builder = new NotificationCompat.Builder(context, channelID);
    return this;
  }

  public FJNotification setService(Service service) {
    this.mService = service;
    return this;
  }

  public FJNotification setView(int layout) {
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layout);
    builder.setCustomBigContentView(remoteViews)//设置通知的布局
        .setCustomHeadsUpContentView(remoteViews)//设置悬挂通知的布局
        .setCustomContentView(remoteViews);
    return this;
  }

  public FJNotification setView(RemoteViews remoteViews) {
    builder.setCustomBigContentView(remoteViews)//设置通知的布局
        .setCustomHeadsUpContentView(remoteViews)//设置悬挂通知的布局
        .setCustomContentView(remoteViews);
    return this;
  }

  public FJNotification setCustomBigContentView(RemoteViews remoteViews) {
    builder.setCustomBigContentView(remoteViews);//设置通知的布局
    return this;
  }

  public FJNotification setCustomHeadsUpContentView(RemoteViews remoteViews) {
    builder.setCustomHeadsUpContentView(remoteViews);
    return this;
  }

  public FJNotification setCustomContentView(RemoteViews remoteViews) {
    builder.setCustomContentView(remoteViews);
    return this;
  }

  public FJNotification setPriority(int priority){
    this.mPriority = priority;
    return this;
  }

  public FJNotification setClass(Class clazz) {
    return setClass(null, clazz);
  }

  public FJNotification setClass(Bundle bundle, Class clazz) {
    return setClass(ACTION, bundle, clazz);
  }

  public FJNotification setClass(String action, Bundle bundle, Class clazz) {
    Intent intent = new Intent(context, clazz);
    intent.setAction(action);
    intent.putExtra(NOTIFICATION, mNotificationId);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(context, REQUEST, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    builder.setContentIntent(pendingIntent);
    //builder.setDeleteIntent(pendingIntent);
    //builder.setOnlyAlertOnce(true);
    //builder.setUsesChronometer(true);
    builder.setOngoing(false);
    builder.setLocalOnly(true);
    builder.setAutoCancel(true);
    builder.setSmallIcon(iconId);
    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), mLargeId));
    builder.setFullScreenIntent(pendingIntent,true);
    builder.setDefaults(NotificationCompat.DEFAULT_ALL);
    return this;
  }

  public FJNotification setId(int notificationId) {
    this.mNotificationId = notificationId;
    return this;
  }

  public FJNotification setTag(String tag){
    this.mTag = tag;
    return this;
  }

  public FJNotification setIcon(int icon) {
    this.iconId = icon;
    return this;
  }

  public FJNotification setLargeIcon(int icon) {
    this.mLargeId = iconId;
    return this;
  }

  public FJNotification setTitle(String title) {
    builder.setContentTitle(title);
    return this;
  }

  public FJNotification setContent(String content) {
    builder.setContentText(content);
    return this;
  }

  public FJNotification setVibrate(boolean isVibrate) {
    if (isVibrate) {
      //builder.setVibrate(new long[] { 0, 1000, 1000, 1000 });
      vibrate(vibrate);
    } else {
      //builder.setVibrate(new long[] { 0 });
      virateCancle();
    }
    return this;
  }

  /**
   * 让手机振动milliseconds毫秒
   */
  public FJNotification vibrate(int time) {
    Vibrator vib =
        (Vibrator) AppUtil.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    if (vib != null && vib.hasVibrator()) {
      vib.vibrate(1000 * time);
    }
    return this;
  }

  /**
   * 设定的pattern[]模式振动
   *
   * @param pattern long pattern[] = {1000, 20000, 10000, 10000, 30000};
   * @param repeat -1以禁用重复,否则重复或者指定位置重复
   */
  public FJNotification vibrate(long[] pattern, int repeat) {
    Vibrator vib =
        (Vibrator) AppUtil.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    if (vib != null && vib.hasVibrator()) {
      vib.vibrate(pattern, repeat);
    }
    return this;
  }

  /**
   * 取消震动
   */
  public FJNotification virateCancle() {
    Vibrator vib =
        (Vibrator) AppUtil.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    if (vib != null) {
      vib.cancel();
    }
    return this;
  }

  /**
   * 播放系统铃声
   */
  public FJNotification ring() {
    Context context = AppUtil.getApplicationContext();
    try {
      cancelRing();
      mediaPlayer = MediaPlayer.create(context, R.raw.hott);
      mediaPlayer.setVolume(1f, 1f);
      mediaPlayer.start();
    } catch (Exception e) {
      LogUtil.e(e);
    }
    return this;
  }

  /**
   * 取消系统铃声
   */
  public FJNotification cancelRing() {
    if (mediaPlayer != null) {
      if (mediaPlayer.isPlaying()) {
        mediaPlayer.stop();
        mediaPlayer.release();
      }
    }
    mediaPlayer = null;
    return this;
  }

  public FJNotification setIsRing(boolean isRing) {
    //Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "raw/hott.wav");
    //if (uri != null && isRing) {
    //  builder.setSound(uri);
    //}
    //if (isRing) {
    //  ring();
    //} else {
    //  cancelRing();
    //}
    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    builder.setSound(uri);
    return this;
  }

  public FJNotification setIsClear(boolean isClear) {
    this.mIsClear = isClear;
    return this;
  }

  public FJNotification setLights(boolean isLight) {
    if (isLight) {
      builder.setLights(lightColor, lightTime, unLightTime);
    }
    return this;
  }

  public FJNotification setProgress(int max, int progress, boolean indeterminate) {
    builder.setProgress(max, progress, indeterminate);
    return this;
  }

  public FJNotification setSubText(String subText) {
    builder.setSubText(subText);
    return this;
  }

  public FJNotification setTicker(String ticker) {
    builder.setTicker(ticker);
    return this;
  }

  public FJNotification setWhen(long when) {
    builder.setWhen(when);
    return this;
  }

  public FJNotification setLocalOnly(boolean b) {
    builder.setLocalOnly(b);
    return this;
  }

  public FJNotification setShowWhen(boolean showWhen) {
    builder.setShowWhen(showWhen);
    return this;
  }

  public FJNotification setChannelId(String channelId) {
    this.channelId = channelId;
    return this;
  }

  public FJNotification setChannelName(String channelName) {
    this.channelName = channelName;
    return this;
  }

  public void clear(){
    clear(mNotificationId);
  }

  public void clear(int notificationId) {
    if(notificationManager != null){
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.deleteNotificationChannel(channelId);
      }else{
        if(!TextUtils.isEmpty(mTag)){
          notificationManager.cancel(mTag,notificationId);
        }else{
          notificationManager.cancel(notificationId);
        }
      }
    }
  }
}
