package com.kaku.weac.util;

import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by yingyongduoduo on 2018/4/12.
 */

public class DateTimeUtil {
    public static int getCurrentHour(Context context) {
        Calendar calendar = Calendar.getInstance();
//获取系统的日期
//年
        int year = calendar.get(Calendar.YEAR);
//月
        int month = calendar.get(Calendar.MONTH);
//日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
//获取系统时间
//小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//分钟
        int minute = calendar.get(Calendar.MINUTE);
//秒
        int second = calendar.get(Calendar.SECOND);

        hour = calculateTime(context, hour);
        return hour;
    }

    /**
     * 根据系统时间24小时或12小时制,计算当前小时数
     * @param context
     * @param hour    当前时间
     * @return
     */
    public static int calculateTime(Context context, int hour) {
        //获得内容提供者
        ContentResolver mResolver = context.getContentResolver();
//获得系统时间制
        String timeFormat = android.provider.Settings.System.getString(mResolver, android.provider.Settings.System.TIME_12_24);
        //判断时间制
        if (TextUtils.isEmpty(timeFormat) || "24".equals(timeFormat)) {
            //24小时制
            return hour;
        } else {
            //12小时制
            //获得日历
            Calendar mCalendar = Calendar.getInstance();
            if (mCalendar.get(Calendar.AM_PM) == 0) {
                //白天
                return hour;
            } else {
                //晚上
                return hour + 12;
            }
        }
    }


}
