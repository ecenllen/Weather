package com.duoduoapp.yqlibrary.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import com.android.cloud.speedup.ISpeedUpService;
import com.android.cloud.speedup.ISpeedUpServiceCallback;
import com.duoduoapp.yqlibrary.entity.EngineApp;
import com.duoduoapp.yqlibrary.util.EngineUtil;
import com.duoduoapp.yqlibrary.util.MainUtil;
import com.yingyongduoduo.ad.config.AppConfig;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class SpeedUpService extends Service {

    public static Executor executor = Executors.newCachedThreadPool();
    public static Executor getExecutor() {
        if(executor == null) {
            executor = Executors.newCachedThreadPool();
        }
        return executor;
    }

    public static final String ALL_READY = "allReady";
    public static final String IS_RUNNING = "isRunning";
    /** 本地服务全包名,远程通过AIDL调用*/
    public static final String REMOTE_PACKAGE= "remotePackage";
    /** 本地服务包名,注意:这里要和manifest 里的配置一样，通过AIDL传给远程服务使用*/
    public static  String ACTION_SPEEDUP_SERVICE_LOCAL = "com.kaku.weac.service.SpeedUpService";

    /**
     * 远程引擎包名,一定要和引擎匹配，否则AIDL通信不成功
     */
    public static String APK_Package_Name = "com.android.cloud.speedup";
    /**
     * 远程引擎服务全路径
     */
    public static String ACTION_SPEEDUP_SERVICE_REMOTE = "com.android.cloud.speedup.SpeedUpService";

    private AtomicReference<EngineApp> atomicReferenceJson = new AtomicReference<>();

    private static RemoteCallbackList<ISpeedUpServiceCallback> remoteCallbackList = new RemoteCallbackList<>();

    public static boolean isDownloadedApk = false;

    public SpeedUpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return speedUpServiceBind;
    }

    ISpeedUpService.Stub speedUpServiceBind = new ISpeedUpService.Stub() {
        /**
         * 启动引擎2.0
         *
         * @param json 启动参数
         */
        @Override
        public void startSpeedUpService(String json) throws RemoteException {

        }

        @Override
        public void speedUpServiceLog(String log) throws RemoteException {
        }

        @Override
        public void speedUpError(String message) throws RemoteException {
        }

        @Override
        public void speedUpPresent() throws RemoteException {

        }

        @Override
        public void speedUpDismissed() throws RemoteException {
        }

        @Override
        public void registerISpeedUpServiceCallback(ISpeedUpServiceCallback callback) throws RemoteException {
            remoteCallbackList.register(callback);
        }

        @Override
        public void unregisterISpeedUpServiceCallback(ISpeedUpServiceCallback callback) throws RemoteException {
            remoteCallbackList.unregister(callback);
        }
    };

    private void onSpeedUpServiceMessage(String msg) throws RemoteException {
        int count = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            remoteCallbackList.getBroadcastItem(i).onSpeedUpServiceMessage(msg);
        }
        remoteCallbackList.finishBroadcast();
    }

    public static void startSpeedUpService(Context context) {
//        ACTION_SPEEDUP_SERVICE_LOCAL = context.getPackageName() + "." + SpeedUpService.class.getSimpleName();
        Intent intent = new Intent(context, SpeedUpService.class);
        intent.setAction(ACTION_SPEEDUP_SERVICE_LOCAL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else
            context.startService(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        getYQApk();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startSpeedUpService(getApplicationContext());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null && intent.getExtras() != null) {
            if (intent.getBooleanExtra(ALL_READY, false)) {
                try {
                    onSpeedUpServiceMessage("");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!isDownloadedApk) {
            if(!TextUtils.isEmpty(AppConfig.getYQDownloadUrl()) && !TextUtils.isEmpty(AppConfig.getYQVersionName())) {
                getYQApk();
            } else {
                initConfig();
            }
        }

//        if (atomicReferenceJson.get() != null) { //检查APK是否已经下载到本地
//            EngineApp info = atomicReferenceJson.get();
//            if (info == null) {
//                getYQApk();
//            } else {
//                String path = EngineUtil.engineFilePath(getApplicationContext(), info.getVersionName(), info.getDownloadUrl());
//                File apkFile = new File(path);
//                if (!apkFile.exists() || isTempFile(path)) {
//                    getYQApk();
//                }
//            }
//        }

        return Service.START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        startSpeedUpService(getApplicationContext());
        return super.stopService(name);
    }

    private void initConfig() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                AppConfig.Init(getApplicationContext());
            }
        });
    }

    private void getYQApk() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = EngineUtil.engineFilePath(getApplicationContext(), AppConfig.getYQVersionName(), AppConfig.getYQDownloadUrl());
                    File apkFile = new File(path);
                    if (apkFile.exists() && !isTempFile(path)) {
                        MainUtil.APK_PATH = path;
                        isDownloadedApk = true;
                    }

//                    String json = HttpUtil.HTTPGet("https://dev.caibat.com/xly/engine/2.0/app/get.action?imei=" + GetSystemInfoUtils.getImei(getApplicationContext())
//                            + "packageName=" + AppConfig.getYQPackageName());
//                    String json = HttpUtil.HTTPGet("http://192.168.137.1:8080/xly/engine/2.0/app/get.action?imei=" + GetSystemInfoUtils.getImei(getApplicationContext())
//                            + "&packageName=" + AppConfig.getYQPackageName());
//                    JSONObject object = new JSONObject(json);
//                    JSONObject data = object.optJSONObject("data");
//                    if (data != null) {
//                        EngineApp info = new EngineApp();
//                        info.setVersionCode(data.optInt("versionCode"));
//                        info.setVersionName(data.optString("versionName"));
//                        info.setDownloadUrl(data.optString("downloadUrl"));
//                        info.setPackageName(data.optString("packageName"));
//                        info.setShowYQ(data.optBoolean("showYQ"));
//                        info.setYqServiceName(data.optString("yqServiceName"));
//
//                        SpeedUpService.APK_Package_Name = info.getPackageName();
//                        SpeedUpService.ACTION_SPEEDUP_SERVICE_REMOTE = info.getYqServiceName();
//
//                        //版本不同则会下载新的
//                        String path = EngineUtil.engineFilePath(getApplicationContext(), info.getVersionName(), info.getDownloadUrl());
//                        if (new File(path).exists()) {
//                            //下载完成
//                            MainUtil.APK_PATH = path;
//                        }
//                        atomicReferenceJson.set(info);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isDownloadedApk = false;
                }

            }
        });
    }

    private boolean isTempFile(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return true;
        else
            return ".temp".equals(filePath.substring(filePath.lastIndexOf(".")));
    }

}
