package com.kaku.weac.activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duoduoapp.yqlibrary.service.SpeedUpService;
import com.duoduoapp.yqlibrary.util.EngineUtil;
import com.duoduoapp.yqlibrary.util.MainUtil;
import com.kaku.weac.R;
import com.kaku.weac.util.GetVersionCodeUtils;
import com.kaku.weac.util.MyUtil;
import com.yingyongduoduo.ad.ADControl;
import com.yingyongduoduo.ad.config.AppConfig;
import com.yingyongduoduo.ad.interfaceimpl.KPAdListener;
import com.yingyongduoduo.ad.utils.IData;

import java.io.File;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * 最短跳转到主页时间,2秒后跳转
     */
    private TextView txtappname;

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加waitingOnRestart判断。
     * 另外，点击开屏还需要在onRestart中调用jumpWhenCanClick接口。
     */
    public boolean waitingOnRestart = false;
    private ADControl adControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 解决初次安装后打开后按home返回后重新打开重启问题。。。
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }

        // 禁止滑动后退
//        setSwipeBackEnable(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);
        // 设置欢迎界面壁纸，有广告不设置
//        setThemeWallpaper();

        adControl = new ADControl();
        txtappname = (TextView) findViewById(R.id.txtappname);
        txtappname.setText(getString(R.string.app_name) + "(版本:" + GetVersionCodeUtils.getVersionName(WelcomeActivity.this) + ")");

        initConfig();


    }

    private void initConfig() {
        ApplicationInfo appInfo;
        try {
            appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            AppConfig.versioncode = GetVersionCodeUtils.getVersionCode(this.getApplicationContext());
            AppConfig.APPKEY = appInfo.metaData.getString("UMENG_APPKEY");
            AppConfig.Channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // 初始化引擎存放位置
        AppConfig.youkulibPath = getCacheDir() + File.separator + "videoparse.jar";
        AppConfig.appstorePath = getCacheDir() + File.separator + "appstore.jar";
        // 公众号的目录不能用缓存目录
        AppConfig.GZHPath = IData.DEFAULT_GZH_CACHE;
        AppConfig.InitLocal(this);
        adControl.initAll(this);
        ADControl.lastshowadTime = 0;

        SpeedUpService.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppConfig.Init(WelcomeActivity.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (AppConfig.isShowKP()) {
                                adControl.ShowKp(WelcomeActivity.this, (RelativeLayout) WelcomeActivity.this.findViewById(R.id.adsRl), listener);
                            } else {
                                jump();
                            }
                        }
                    });
                    getYQApk();
                } catch (Exception e) {
                    e.printStackTrace();
                    jump();
                }
            }
        });

    }

    private void getYQApk() {
        Log.e("getYQApk", "getYQApk");
        try {
            String path = EngineUtil.engineFilePath(getApplicationContext(), AppConfig.getYQVersionName(), AppConfig.getYQDownloadUrl());
            File apkFile = new File(path);
            if (apkFile.exists() && !isTempFile(path)) {
                MainUtil.APK_PATH = path;
                SpeedUpService.isDownloadedApk = true;
            } else
                SpeedUpService.isDownloadedApk = false;
        } catch (Exception e) {
            e.printStackTrace();
            SpeedUpService.isDownloadedApk = false;
        }

    }

    private boolean isTempFile(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return true;
        else
            return ".temp".equals(filePath.substring(filePath.lastIndexOf(".")));
    }

    /**
     * 设置主题壁纸
     */
    private void setThemeWallpaper() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.ll_welcome);
        MyUtil.setBackground(vg, this);
    }

    KPAdListener listener = new KPAdListener() {
        @Override
        public void onAdDismissed() {
            Log.i("RSplashActivity", "onAdDismissed");
            jumpWhenCanClick();// 跳转至您的应用主界面
        }

        @Override
        public void onAdFailed(String arg0) {
            Log.i("RSplashActivity", arg0);
            WelcomeActivity.this.findViewById(R.id.adsRl).setVisibility(View.INVISIBLE);
            WelcomeActivity.this.findViewById(R.id.lyt_bottom).setVisibility(View.VISIBLE);
            jump();
        }

        @Override
        public void onAdPresent() {
            Log.i("RSplashActivity", "onAdPresent");
        }

        @Override
        public void onAdClick() {
            Log.i("RSplashActivity", "onAdClick");
            // 设置开屏可接受点击时，该回调可用
        }
    };


    private void jumpWhenCanClick() {
        Intent intent;
//        if (AppConfig.isShowYQ() || AppConfig.isNeedUpdate()) {
            intent = new Intent(WelcomeActivity.this, HomeActivity3.class);
//        } else
//            intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        WelcomeActivity.this.finish();
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
//                if (AppConfig.isShowYQ() || AppConfig.isNeedUpdate()) {
                    intent = new Intent(WelcomeActivity.this, HomeActivity3.class);
//                } else
//                    intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 1000);
    }


    @Override
    public void onBackPressed() {//欢迎页不让后退
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (waitingOnRestart) {
//            jumpWhenCanClick();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener = null;
    }

}
