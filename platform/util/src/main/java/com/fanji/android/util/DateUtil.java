package com.fanji.android.util;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间日期相关工具类
 * created by jiangshide on 2015-09-12.
 * email:18311271399@163.com
 */
public final class DateUtil {

    private TextView textView;
    private String tips;
    private int beforeOrAfter;
    private String beforeTips, afterTips;
    private String lastTips;
    private String normalText;
    private Class _class;

    public static final String ymdhms = "yyyy-MM-dd HH:mm:ss";
    public static final String ymd = "yyyy-MM-dd";
    private static String WEEK[] = {"日", "一", "二", "三", "四", "五", "六"};
    private static String MONTH[] = {
            "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"
    };

    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long MOTH = 30 * DAY;
    public static final long YEAR = 360 * DAY;
    public static final String A_H_M = "aa hh:mm";
    public static final String Y_M_D = "yyyy/MM/dd";
    public static final String EEEE = "EEEE";
    public static final String Y_M_D_H_M = "yyyy-MM-dd HH:mm";
    public static final String EEEE_A_H_M = "EEEE aa HH:mm";
    public static final String M_D_H_M = "MM月dd日 HH:mm";

    public static final String DIFF_TIME = "DIFF_TIME";

    private boolean isPause;


    public DateUtil setView(TextView textView) {
        this.textView = textView;
        this.textView.setVisibility(View.VISIBLE);
        return this;
    }

    public DateUtil setView(TextView textView, String tips) {
        this.textView = textView;
        this.tips = tips;
        this.textView.setVisibility(View.VISIBLE);
        return this;
    }

    public DateUtil setView(TextView textView, String tips, int beforeOrAfter) {
        this.textView = textView;
        this.tips = tips;
        this.beforeOrAfter = beforeOrAfter;
        this.textView.setVisibility(View.VISIBLE);
        return this;
    }

    public DateUtil setView(TextView textView, String beforeTips, String afterTips) {
        this.textView = textView;
        this.beforeTips = beforeTips;
        this.afterTips = afterTips;
        this.textView.setVisibility(View.VISIBLE);
        return this;
    }

    public DateUtil setNormalText(String text) {
        this.normalText = text;
        return this;
    }

    /**
     * 获取当前时区
     */
    public static String getGmtTimeZone() {
        return String.valueOf(
                TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (3600 * 1000));
    }

    /**
     * Date转String
     */
    public static String date2String(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * String转Date
     *
     * @throws ParseException
     */
    public static Date string2Date(String strTime, String formatType) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    /**
     * Long转Date
     *
     * @throws ParseException
     */
    public static Date long2Date(long currentTime, String formatType) throws ParseException {
        Date date = new Date(currentTime);
        return date;
    }

    /**
     * Date转Long
     */
    public static long date2Long(Date date) {
        return date.getTime();
    }

    /**
     * Long转String
     *
     * @throws ParseException
     */
    public static String long2String(long currentTime) throws ParseException {
        Date date = long2Date(currentTime, ymdhms);
        String strTime = date2String(date, ymdhms);
        return strTime;
    }

    /**
     * Long转String
     *
     * @throws ParseException
     */
    public static String long2Day(long currentTime) throws ParseException {
        Date date = long2Date(currentTime, "yyyy-MM-dd");
        String strTime = date2String(date, "yyyy-MM-dd");
        return strTime;
    }

    /**
     * Long转String
     *
     * @throws ParseException
     */
    public static String long2String(long currentTime, String formatType) throws ParseException {
        Date date = long2Date(currentTime, formatType);
        String strTime = date2String(date, formatType);
        return strTime;
    }

    /**
     * String转Long
     *
     * @throws ParseException
     */
    public static long stringToLong(String strTime) {
        Date date = null;
        try {
            date = string2Date(strTime, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return 0;
        } else {
            long currentTime = date2Long(date); // date类型转成long类型
            return currentTime;
        }
    }

    /**
     * String转Long
     *
     * @throws ParseException
     */
    public static long stringToLong(String strTime, String formatType) throws ParseException {
        Date date = string2Date(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = date2Long(date); // date类型转成long类型
            return currentTime;
        }
    }

    public static String amTime(long time) {
        Date date = null;
        try {
            date = long2Date(time, "hh:mm a");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String strTime = date2String(date, "hh:mm a");
        return strTime;
    }

    //时区相关

    /**
     * 判断用户的设备时区是否为东八区（中国）
     */
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08")) {
            defaultVaule = true;
        } else {
            defaultVaule = false;
        }
        return defaultVaule;
    }

    /**
     * 根据不同时区，转换时间
     */
    public static Date transformTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime()) - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;
    }

    /**
     * 根据时间 时区，获取时间戳
     */
    public static String transformTimeZoneLong(String s, String zone) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone timeZone = TimeZone.getTimeZone(zone.replace("UTC ", "GMT") + ":00");
        simpleDateFormat.setTimeZone(timeZone);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime() / 1000;
        res = String.valueOf(ts);
        return res;
    }
    //
    //public static String showTimeAhead(String str){
    //  if(TextUtils.isEmpty(str))return str;
    //  String[] dates = str.split(" ");
    //  String[] date = dates[0].split("-");
    //  String[] time = dates[1].split(":");
    //  int year = Integer.parseInt(date[0]);
    //  int month = Integer.parseInt(date[1]);
    //  int day = Integer.parseInt(date[2]);
    //  int hour = Integer.parseInt(time[0]);
    //  int  minute= Integer.parseInt(time[1]);
    //  int second = Integer.parseInt(time[2]);
    //
    //  int nowYear = getYear();
    //  int nowMonty = getMonth();
    //  int nowDay = getDay();
    //  int nowHour = getHour();
    //  int nowMinute = getMinute();
    //
    //}

    /**
     * 获取年
     */
    public static int getYear() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.YEAR);
    }

    /**
     * 获取月
     */
    public static int getMonth() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日
     */
    public static int getDay() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.DATE);
    }

    /**
     * 获取时
     */
    public static int getHour() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.HOUR);
    }

    /**
     * 获取分
     */
    public static int getMinute() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MINUTE);
    }

    /**
     * 显示日期为x小时前、昨天、前天、x天前等
     *
     * @throws ParseException
     */
    public static String showTimeAhead(long longTime) throws ParseException {
        if (longTime == 0) return "";
        String resultTime = "";
        //传入的日期
        Date date = null;
        if (isInEasternEightZones()) {
            date = long2Date(longTime, ymdhms);
        } else {
            date = transformTimeZone(long2Date(longTime, ymdhms), TimeZone.getTimeZone("GMT+08"),
                    TimeZone.getDefault());
        }

        //当前日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int days = (int) (getDay() - cal.get(Calendar.DATE));
        int months = (getMonth() - (cal.get(Calendar.MONTH) + 1));
        //如果日期相同
        if (days == 0) {
            int hour = getHour() - cal.get(Calendar.HOUR);
            LogUtil.e("--------getHour():", getHour(), " | cal.get(Calendar.HOUR):", cal.get(Calendar.HOUR), " | cal:", cal);
            //如果小时相同
            if (hour == 0) {
                int minutes = getMinute() - cal.get(Calendar.MINUTE);
                if (minutes == 0) {
                    resultTime = "刚刚";
                } else {
                    resultTime = minutes + "分钟前";
                }
            } else {
                resultTime = hour + "小时前";
            }
        } else if (days == 1) {
            resultTime = "昨天";
        } else if (days == 2) {
            resultTime = "前天 ";
        } else if (days > 2 && days < 31) {
            resultTime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            resultTime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            resultTime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            resultTime = "3个月前";
        } else {
            resultTime = long2String(longTime, "yyyy/MM/dd");
        }

//    LogUtil.e("resultTime:",resultTime," | longTime:",longTime);
        return resultTime;
    }

    public static int getDay(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.get(Calendar.DATE);
    }

    public static int getDay(String year, String month) {
        return getDay(Integer.parseInt(year), Integer.parseInt(month));
    }

    public static String getWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return "星期" + WEEK[dayOfWeek < 1 && dayOfWeek > 7 ? 1 : dayOfWeek];
    }

    public static String[] getDateAfter() {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        long time = new Date().getTime() + 86400000;
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        String[] sta = res.split("-");
        return res.split("-");
    }

    public static int getMonth(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.YEAR, year);
        int month = calendar.get(Calendar.MONTH);
        return month;
    }

    public static int getMonth(String year) {
        return getMonth(Integer.parseInt(year));
    }

    public static String generateShortTime(int timeSecond) {
        int seconds = timeSecond % 60;
        int minutes = (timeSecond / 60) % 60;
        int hours = timeSecond / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(String.format("%02d:", hours));
        }
        stringBuilder.append(String.format("%02d:", minutes));
        stringBuilder.append(String.format("%02d", seconds));

        return stringBuilder.toString();
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为毫秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp);
        Date currenTimeZone = inputTime.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        if (calendar.before(inputTime)) {
            //今天23:59在输入时间之前，解决一些时间误差，把当天时间显示到这里
            SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D);
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat(A_H_M);
            return filterAMPM(sdf.format(currenTimeZone));
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return "昨天";
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (calendar.get(Calendar.YEAR) == inputTime.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) - inputTime.get(Calendar.DAY_OF_YEAR)
                            < 7) {//7天之内，按照星期算
                SimpleDateFormat sdf = new SimpleDateFormat(EEEE);
                return sdf.format(currenTimeZone);
            }

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D);
            return sdf.format(currenTimeZone);
        }
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp 单位为秒
     */
    public static String getChatTimeStr(long timeStamp) {

        LogUtil.e("timeStamp:", timeStamp);
        try {
            if (timeStamp == 10) {
                timeStamp = timeStamp * 1000;
            }
            String format = "yyyy/MM/dd HH:mm a";
            long currTime = System.currentTimeMillis();
            long day = 1000 * 3600 * 24;
            long difTime = currTime - timeStamp;
            boolean beforeDay = (difTime - day > 0) ? true : false;
            if (!beforeDay) {
                format = "HH:mm a";
            }
            return long2String(timeStamp, format);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (timeStamp == 0) return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        if (calendar.before(inputTime)) {
            //今天23:59在输入时间之前，解决一些时间误差，把当天时间显示到这里
            SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D_H_M);
            return sdf.format(currenTimeZone);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {       //今天
            SimpleDateFormat sdf = new SimpleDateFormat(A_H_M);
            return filterAMPM(sdf.format(currenTimeZone));
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {       //昨天
            SimpleDateFormat sdf = new SimpleDateFormat("昨天 aa hh:mm");
            return filterAMPM(sdf.format(currenTimeZone));
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (calendar.get(Calendar.DAY_OF_YEAR) - inputTime.get(Calendar.DAY_OF_YEAR)
                    < 7) {//7天之内，按照星期算
                SimpleDateFormat sdf = new SimpleDateFormat(EEEE_A_H_M);
                return filterAMPM(sdf.format(currenTimeZone));
            }
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat(M_D_H_M);
                return sdf.format(currenTimeZone);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(Y_M_D_H_M);
                return sdf.format(currenTimeZone);
            }
        }
    }

    private static String filterAMPM(String timeStr) {
        if (timeStr.contains("am")) {
            timeStr = timeStr.replace("am", "上午");
        } else if (timeStr.contains("AM")) {
            timeStr = timeStr.replace("AM", "上午");
        } else if (timeStr.contains("pm")) {
            timeStr = timeStr.replace("pm", "下午");
        } else if (timeStr.contains("PM")) {
            timeStr = timeStr.replace("PM", "下午");
        }

        return timeStr;
    }

    public static boolean longInterval(long current, long last, long interval) {
        return current - last > interval;
    }

    public static int getCurrentAge(String birthday) {
        return getCurrentAge(birthday, "yyyyMMdd");
    }

    /**
     * 根据生日计算当前周岁数
     */
    public static int getCurrentAge(String birthday, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        Calendar curr = Calendar.getInstance();
        Calendar born = Calendar.getInstance();
        try {
            born.setTime(simpleDateFormat.parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int age = curr.get(Calendar.YEAR) - born.get(Calendar.YEAR);
        if (age <= 0) {
            return 0;
        }
        int currMonth = curr.get(Calendar.MONTH);
        int currDay = curr.get(Calendar.DAY_OF_MONTH);
        int bornMonth = born.get(Calendar.MONTH);
        int bornDay = born.get(Calendar.DAY_OF_MONTH);
        if ((currMonth < bornMonth) || (currMonth == bornMonth && currDay <= bornDay)) {
            age--;
        }
        return age < 0 ? 0 : age;
    }

    public static String formatSeconds(long seconds) {
        return formatSeconds((int) seconds);
    }

    public static String formatSeconds(int seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime =
                    String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60,
                            seconds % 60);
        }
        return standardTime;
    }

    public static String getDiffTime(long start, int duration) {
        LogUtil.e("-------------start:", start, " | duration:", duration);
        long currTime = System.currentTimeMillis() + SPUtil.getLong(DIFF_TIME);
        long diffTime = duration * 1000 * 60 - (currTime - start);

        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;

        day = diffTime / (24 * 60 * 60 * 1000);
        hour = (diffTime / (60 * 60 * 1000) - day * 24);
        min = ((diffTime / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diffTime / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
        if (hour != 0) return hour + "小时" + min + "分钟" + sec + "秒";
        if (min != 0) return min + "分钟" + sec + "秒";
        if (sec != 0) return sec + "秒";
        return "0秒";
    }

    public static String getDatePoor(long timeStamp) {
        long currTime = System.currentTimeMillis();
        if (timeStamp < 100000000000L) {
            timeStamp = timeStamp * 1000;
        }
        currTime = currTime + SPUtil.getLong(DIFF_TIME);

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = currTime - timeStamp;
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        if (day >= 365) {
            try {
                return long2String(timeStamp, "yyyy/MM/dd");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (day >= 30) {
            return day / 30 + "月前";
        }
        if (day >= 7) {
            return day / 7 + "周前";
        }
        if (day > 0) {
            return day + "天前";
        }
        if (hour > 0) {
            return hour + "小时前";
        }
        if (min > 0) {
            return min + "分钟前";
        }
        return "刚刚";
    }

    public static String stringForTime(int timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static Date parse(String strDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(strDate);
    }

    /**
     * format:yyyy-MM-dd
     *
     * @throws Exception
     */
    public static int getAge(String date) throws Exception {
        if (TextUtils.isEmpty(date)) return 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDay = simpleDateFormat.parse(date);
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private final static String[] constellationArr = new String[]{
            "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"
    };

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }
}
