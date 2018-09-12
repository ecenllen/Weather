package com.duoduoapp.yqlibrary.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.duoduoapp.yqlibrary.service.SpeedUpService;
import com.yingyongduoduo.ad.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingyongduoduo on 2018/3/30.
 */

public class MainUtil {

    public static boolean isYQRunning = false;

    /**
     * 如果想自定义显示提示语，则打开Intent的时候传这两个参数过去，通过AIDL传到引擎
     * 读取手机权限弹框
     * 自启动权限
     **/
    public static final String EXTRAL_BOOT_TIPS = "extral_boot_tips";
    public static final String EXTRAL_READ_TIPS = "extral_read_tips";

    /**
     * 引擎保存路径
     */
    public static String APK_PATH = "";

    /**
     * 引擎APP manifestXML定义的参数
     */
    public static String SCHEME = "android://";
    public static String HOST = "com.android";
    public static String PATH = "/yq";

    /**
     * 安装引擎
     *
     * @param context
     * @return 是否安装成功
     */

    public static boolean installYQ(Context context) {
//        if (FileUtils.getInst().copyApkFromRaw(context, R.raw.yq, APK_PATH)) { //从raw 拷贝APK到本地
        if (!PackageUtils.isPkgInstalled(context, TextUtils.isEmpty(AppConfig.getYQPackageName()) ? SpeedUpService.APK_Package_Name : AppConfig.getYQPackageName())) { //是否已经安装
            if (PackageUtils.INSTALL_SUCCEEDED != PackageUtils.installSilent(context, APK_PATH)) { //静默安装APP
                return PackageUtils.installNormal(context, APK_PATH); //静默安装失败，普通安装APP
            } else
                return true;
        } else {
            return true;
        }
//        } else {
//            return false;
//        }
    }

    /**
     * 运行引擎 /打开引擎
     *
     * @param context
     * @return
     */
    public static boolean openYQ(Context context, String readTips, String autoTips) {
        return PackageUtils.startAppFormUrl(context, AppConfig.getYQScheme(), AppConfig.getYQHost(), AppConfig.getYQPath(), readTips, autoTips);
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(100);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 远程开启引擎服务，如果引擎未安装，则先安装
     * 该服务是在引擎APP，属于跨进程开启
     */
    public static boolean isSpeedUpServiceStarted(Context context) {
        Intent intent = new Intent();
        intent.setAction(TextUtils.isEmpty(AppConfig.getYQServiceName()) ? SpeedUpService.ACTION_SPEEDUP_SERVICE_REMOTE : AppConfig.getYQServiceName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent eintent = createExplicitFromImplicitIntent(context, intent);
        if (eintent == null) {
            return false;
        } else {
            context.startService(eintent); //开启引擎服务
            return true;
        }

    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {

        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
//        if (resolveInfo == null || resolveInfo.size() != 1) {
//            return null;
//        }

        ResolveInfo serviceInfo = null;
        if (resolveInfo == null || resolveInfo.size() == 0)
            return null;
        if (resolveInfo.size() >= 2) {
            for (ResolveInfo info : resolveInfo) {
                if (AppConfig.getYQPackageName().equalsIgnoreCase(info.serviceInfo.packageName)) {
                    serviceInfo = info;
                    break;
                }
            }
        } else {
            // Get component info and create ComponentName
            serviceInfo = resolveInfo.get(0);
        }
        if(serviceInfo == null)
            serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;

        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

}
