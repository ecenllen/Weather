package com.kaku.weac.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by dkli on 2017/12/5.
 * @author dkli
 */

public class GetVersionCodeUtils {
    public static String getVersionName(Context context){
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
    public static String getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(info.versionCode); //获取版本cood
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
