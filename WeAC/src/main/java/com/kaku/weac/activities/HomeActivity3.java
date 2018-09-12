package com.kaku.weac.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cloud.speedup.ISpeedUpService;
import com.android.cloud.speedup.ISpeedUpServiceCallback;
import com.duoduoapp.yqlibrary.service.SpeedUpService;
import com.duoduoapp.yqlibrary.util.AppUtils;
import com.duoduoapp.yqlibrary.util.KeepLiveUtils;
import com.duoduoapp.yqlibrary.util.MainUtil;
import com.duoduoapp.yqlibrary.util.PackageUtils;
import com.kaku.weac.R;
import com.kaku.weac.util.MyUtil;
import com.yingyongduoduo.ad.config.AppConfig;

/**
 * 引擎AIDL方式实现
 * Created by yingyongduoduo on 2018/3/29.
 */

public class HomeActivity3 extends AppCompatActivity {

    //        private static final String ACTION_SPEEDUP_SERVICE_REMOTE = "com.android.cloud.speedup.SpeedUpService";

    /**
     * 读取应用权限提示，自启动权限提示，通过AIDL传递到引擎，Dialog弹框提示
     */
    private String readPhonePermissionTips = "";
    private String autoStartPermissionTips = "";

    ImageView fab;
    TextView tvInstall;
    TextView go;

    //也是刚刚打开应用标识
    private boolean isInstalled = false;
    private boolean isServiceRunning = false;
    private boolean isFirstRemote = true;
    //打开应用自动跳转到主页面，默认为true，
    private boolean canAutoJump = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home3);

        String appName = AppUtils.getName(this, getPackageName());
        readPhonePermissionTips += appName + " 需要开启以下权限!"; //实际上是读取应用列表权限
        autoStartPermissionTips += appName + " 添加到自启动，保证软件正常运行!";

        // 禁止滑动后退
//        setSwipeBackEnable(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置欢迎界面壁纸
        setThemeWallpaper();

        initView();

        bindRemoteService();
//        bindSpeedUpService();
        isNeedInstallPlug(true);
    }

    /**
     * 设置主题壁纸
     */
    private void setThemeWallpaper() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.rl_content);
        MyUtil.setBackground(vg, this);
    }

    private void initView() {
        fab = (ImageView) findViewById(R.id.fab_go);
        tvInstall = (TextView) findViewById(R.id.tv_install_plug);
        go = (TextView) findViewById(R.id.tv_go);
        MyClickListener listener = new MyClickListener();
        fab.setOnClickListener(listener);
        tvInstall.setOnClickListener(listener);
        go.setOnClickListener(listener);
    }

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.fab_go) {
                onFabClick(v);
            } else if (i == R.id.tv_install_plug) {
                onInstallClick(v);
            } else if (i == R.id.tv_go) {
                onGoMainClick(v);
            }
        }
    }

    /**
     * AIDL 远程通信绑定引擎服务
     */
    private void bindRemoteService() {
        Intent intent = new Intent();
        intent.setAction(TextUtils.isEmpty(AppConfig.getYQServiceName()) ? SpeedUpService.ACTION_SPEEDUP_SERVICE_REMOTE : AppConfig.getYQServiceName());

        Intent eintent = MainUtil.createExplicitFromImplicitIntent(this, intent);
        if (eintent != null) {
            bindService(eintent, speedUpServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ISpeedUpService iSpeedUpService;

    private ServiceConnection speedUpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iSpeedUpService = ISpeedUpService.Stub.asInterface(service);
            try {
                if (iSpeedUpService != null) {
                    iSpeedUpService.registerISpeedUpServiceCallback(callback);
                }

                iSpeedUpService.startSpeedUpService(HomeActivity3.this.getPackageName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (iSpeedUpService != null) {
                    iSpeedUpService.unregisterISpeedUpServiceCallback(callback);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            iSpeedUpService = null;
        }
    };


    public void onFabClick(View v) {
        if (isInstalled && isServiceRunning)
            jumpWhenCanGo(this);
    }

    public void onInstallClick(View v) {
        if (KeepLiveUtils.hasNetwork(this)) {
//            canAutoJump = false;  //手动点击安装引擎插件，则不允许自动跳转
            if (isInstalled) {
                if (!isServiceRunning)
                    MainUtil.openYQ(this, readPhonePermissionTips, autoStartPermissionTips);
            } else {
                isInstalled = MainUtil.installYQ(this);
            }
        } else
            Snackbar.make(v, "暂无网络", Snackbar.LENGTH_SHORT).show();


//        LogUtil.e("tips", readPhonePermissionTips + "," + autoStartPermissionTips);

    }

    public void onGoMainClick(View v) {
        if (isInstalled && isServiceRunning)
            jumpWhenCanGo(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isInstalled = PackageUtils.isPkgInstalled(this, TextUtils.isEmpty(AppConfig.getYQPackageName()) ? SpeedUpService.APK_Package_Name : AppConfig.getYQPackageName());

        if (iSpeedUpService == null)
            bindRemoteService(); //AIDL远程调用引擎服务，有反应即引擎已开启运行
        else{
//            try {
//                iSpeedUpService.startSpeedUpService(AppConfig.getYQPackageName());
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
        }
        if (isInstalled) {
            if (!isServiceRunning && isFirstRemote) {
                isFirstRemote = false;
                MainUtil.openYQ(this, readPhonePermissionTips, autoStartPermissionTips);
            }
        }
    }


    /**
     * 需要安装/检查引擎
     *
     * @param isNeed
     */
    private synchronized void isNeedInstallPlug(Boolean isNeed) {
        if (isNeed) {
            tvInstall.setText("需要安装插件，请点击");
            tvInstall.setVisibility(View.VISIBLE);
            go.setVisibility(View.INVISIBLE);
            isServiceRunning = false;
        } else {
            go.setVisibility(View.VISIBLE);
            go.setText("开启梦幻之旅!");
            tvInstall.setVisibility(View.INVISIBLE);
            isServiceRunning = true;
            if (canAutoJump) //打开应用，自动跳转到主页
                jumpWhenCanGo(this);
        }
    }

    /**
     * 跳转到主页
     *
     * @param homeActivity context
     */
    private void jumpWhenCanGo(Context homeActivity) {
        canAutoJump = false;//防止多次调用打开多个界面
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private ISpeedUpServiceCallback.Stub callback = new ISpeedUpServiceCallback.Stub() {
        @Override
        public void onSpeedUpServiceMessage(String json) throws RemoteException {
            Log.i("HomeActivity", json + "");
            postSpeedUpPresent(json);
        }

        /**
         * 广告剩余时间
         *
         * @param time
         */
        @Override
        public void onSpeedUpServiceLessTime(long time) throws RemoteException {

        }

        @Override
        public void onSpeedUpServiceDismissed() throws RemoteException {

        }

        @Override
        public void onSpeedUpServicePresent() throws RemoteException {

        }

        @Override
        public void onSpeedUpServiceError(String message) throws RemoteException {

        }
    };

    private void postSpeedUpPresent(String msg) {
        isNeedInstallPlug("false".equals(msg));
    }

    @Override
    protected void onDestroy() {
        try {
            if (iSpeedUpService != null) {
                iSpeedUpService.unregisterISpeedUpServiceCallback(callback);
                unbindService(speedUpServiceConnection);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }
}

