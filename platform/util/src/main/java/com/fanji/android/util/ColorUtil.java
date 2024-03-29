package com.fanji.android.util;

import android.graphics.Color;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

/**
 * created by jiangshide on 4/11/21.
 * email:18311271399@163.com
 */
public class ColorUtil {
    /**
     * 解析颜色
     *
     * @param colorStr
     * @param defaultColor
     * @return
     */
    public static int parseColor(String colorStr, int defaultColor) {
        if (TextUtils.isEmpty(colorStr)) {
            return defaultColor;
        }
        try {
            if (!colorStr.startsWith("#")) {
                colorStr = "#" + colorStr;
            }
            int color = Color.parseColor(colorStr);
            return color;
        } catch (Exception e) {
            return defaultColor;
        }
    }

    public static int parseColor(String colorStr) {
        if (TextUtils.isEmpty(colorStr)) {
            return 0;
        }
        try {
            if (!colorStr.startsWith("#")) {
                colorStr = "#" + colorStr;
            }
            return Color.parseColor(colorStr);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 解析颜色
     *
     * @param color
     * @return
     */
    public static int parseColor(int color) {
        return ContextCompat.getColor(AppUtil.getApplicationContext(), color);
    }

    /**
     * 设置html字体色值
     *
     * @param text
     * @param color
     * @return
     */
    public static String setTextColor(String text, String color) {
        return "<font color=#" + color + ">" + text + "</font>";
    }

}
