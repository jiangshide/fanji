package com.fanji.android.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * created by jiangshide on 4/4/21.
 * email:18311271399@163.com
 */
public final class AppUtil {
    @SuppressLint("StaticFieldLeak")
    private static volatile Application sApplication;

    public static Application getApplicationContext() {
        if (sApplication == null) {
            synchronized (AppUtil.class) {
                if (sApplication == null) {
                    try {
                        sApplication = (Application) Class.forName("android.app.ActivityThread")
                                .getMethod("currentApplication")
                                .invoke(null, (Object[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sApplication;
    }

    /**
     * Return the application's package name.
     *
     * @return the application's package name
     */
    public static String getPackageName() {
        return getApplicationContext().getPackageName();
    }

    public static String getPackageName(String path) {
        return getApplicationContext().getPackageManager()
                .getPackageArchiveInfo(path,
                        PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES).packageName;
    }

    /**
     * Return the application's version name.
     *
     * @return the application's version name
     */
    public static String getAppVersionName() {
        return getAppVersionName(getApplicationContext().getPackageName());
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    public static String getAppVersionName(final String packageName) {
        if (isSpace(packageName)) return "";
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Return the application's version code.
     *
     * @return the application's version code
     */
    public static int getAppVersionCode() {
        return getAppVersionCode(getApplicationContext().getPackageName());
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    public static int getAppVersionCode(final String packageName) {
        if (isSpace(packageName)) return -1;
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Return the application's name.
     *
     * @return the application's name
     */
    public static String getAppName() {
        return getAppName(getApplicationContext().getPackageName());
    }

    /**
     * Return the application's name.
     *
     * @param packageName The name of the package.
     * @return the application's name
     */
    public static String getAppName(final String packageName) {
        if (isSpace(packageName)) return "";
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getIcon() {
        return getIcon("ic_launcher", "mipmap");
    }

    public static int getIcon(String iconName, String type) {
        String iconValue = getIconValue(iconName);
        if (TextUtils.isEmpty(iconValue)) {
            return -1;
        }
        if (TextUtils.isEmpty(type)) {
            type = "mipmap";
        }
        int resId = AppUtil.getApplicationContext()
                .getResources().getIdentifier(iconValue, type,
                        getPackageName());
        if (resId <= 0) {
            type = "drawable";
            resId = AppUtil.getApplicationContext()
                    .getResources().getIdentifier(iconValue, type,
                            getPackageName());
        }
        return resId;
    }

    public static String getMeta(Context context, String name) {
        String value = "";
        try {
            ApplicationInfo activityInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = activityInfo.metaData.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getIconValue(String iconName) {
        String value = "";
        if (TextUtils.isEmpty(iconName)) {
            iconName = "ic_launcher";
        }
        try {
            ApplicationInfo appInfo = AppUtil.getApplicationContext().getPackageManager().
                    getApplicationInfo(AppUtil.getApplicationContext().getPackageName(),
                            PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(iconName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取当前IP地址
     */
    public static String getIp() {
        String ip = getWifiIpAddress();
        if (TextUtils.isEmpty(ip)) {
            ip = getGprsIpAddress();
        }
        return ip;
    }

    /**
     * 获取当前ip地址~wifi
     */
    public static String getWifiIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) AppUtil.getApplicationContext()
                    .getSystemService(AppUtil.getApplicationContext().WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            LogUtil.e(" 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage());
        }
        return null;
    }

    /**
     * 将ip的整数形式转换成ip形式
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址~GPRS
     */
    public static String getGprsIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }
}
