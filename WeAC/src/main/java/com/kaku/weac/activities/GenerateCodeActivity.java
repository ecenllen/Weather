/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.kaku.weac.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.kaku.weac.R;
import com.kaku.weac.bean.Event.QRcodeLogoEvent;
import com.kaku.weac.common.WeacConstants;
import com.kaku.weac.util.LogUtil;
import com.kaku.weac.util.MyUtil;
import com.kaku.weac.util.OttoAppConfig;
import com.kaku.weac.util.ToastUtil;
import com.kaku.weac.zxing.encoding.EncodingUtils;
import com.squareup.otto.Subscribe;
import com.yingyongduoduo.ad.ADControl;

import java.io.File;
import java.io.FileOutputStream;


/**
 * 造码Activity
 *
 * @author 咖枯
 * @version 1.0 2016/2/2
 */
public class GenerateCodeActivity extends BaseActivity implements View.OnClickListener, ColorChooserDialog.ColorCallback {
    private static final String LOG_TAG = "GenerateCodeActivity";
    /**
     * 二维码前景色：默认黑色
     */
    private int mForeColor;

    /**
     * 二维码背景色：默认白色
     */
    private int mBackColor;
    private ImageView mActionOverflow;
    private PopupMenu mPopupMenu;
    private EditText mQrCodeEt;
    private ToggleButton mLogoTogBtn;
    private ImageView mQrCodeResultIv;
    private ImageView mLogoIv;
    private static final int REQUEST_LOCAL_ALBUM = 1;
    private String mLogoPath;
    private boolean mIsQRcodeGenerated;

    /**
     * 调色板使用状态：0，前景色；1，背景色
     */
    private int mPalette = 0;
    private ADControl adControl;
    private LinearLayout adLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OttoAppConfig.getInstance().register(this);
        setContentView(R.layout.activity_generate_code);
        ViewGroup background = (ViewGroup) findViewById(R.id.background);
        MyUtil.setBackground(background, this);
        assignValues();
        assignViews();
    }

    /**
     * 获取保存的自定义二维码logo地址、前景色、背景色
     */
    private void assignValues() {
        SharedPreferences share = getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        mLogoPath = share.getString(WeacConstants.QRCODE_LOGO_PATH, null);
        mForeColor = share.getInt(WeacConstants.FORE_COLOR, 0xff000000);
        mBackColor = share.getInt(WeacConstants.BACK_COLOR, 0xffffffff);

    }

    private void assignViews() {
        adLinearLayout= (LinearLayout) findViewById(R.id.adLinearLayout);
        adControl=new ADControl();
        ImageView actionBack = (ImageView) findViewById(R.id.action_back);
        final Button generateQRcodeBtn = (Button) findViewById(R.id.generate_qr_code_btn);
        mActionOverflow = (ImageView) findViewById(R.id.action_overflow);
        mQrCodeEt = (EditText) findViewById(R.id.qr_code_et);
        mLogoTogBtn = (ToggleButton) findViewById(R.id.logo_btn);
        mQrCodeResultIv = (ImageView) findViewById(R.id.qr_code_result_iv);
        mLogoIv = (ImageView) findViewById(R.id.logo_iv);

        if (mLogoPath != null) {
            mLogoIv.setImageBitmap(BitmapFactory.decodeFile(mLogoPath));
        }

        mLogoTogBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mIsQRcodeGenerated) {
                    generateQRcode();
                }
            }
        });

        actionBack.setOnClickListener(this);
        mActionOverflow.setOnClickListener(this);

        generateQRcodeBtn.setOnClickListener(this);
        generateQRcodeBtn.setClickable(false);

        mQrCodeEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    generateQRcodeBtn.setClickable(false);
                    generateQRcodeBtn.setBackgroundResource(R.drawable.shape_circle_btn_sure_invalidate);
                    generateQRcodeBtn.setTextColor(getResources().getColor(R.color.white_trans60));
                } else {
                    generateQRcodeBtn.setClickable(true);
                    generateQRcodeBtn.setBackgroundResource(R.drawable.bg_btn_sure);
                    generateQRcodeBtn.setTextColor(getResources().getColor(R.color.white_trans90));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.action_back:
                finish();
                break;
            // 生成二维码
            case R.id.generate_qr_code_btn:
                generateQRcode();
                break;
            // 更多（溢出菜单按钮）
            case R.id.action_overflow:
                operateOverflow();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adControl.addAd(adLinearLayout,this);
        if (System.currentTimeMillis() - BaseActivity.time > 120 * 1000) {
            BaseActivity.time = System.currentTimeMillis();
            adControl.homeGet5Score(this);
        }
    }

    private void operateOverflow() {
        if (MyUtil.isFastDoubleClick()) {
            return;
        }
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this, mActionOverflow);
            mPopupMenu.getMenuInflater().inflate(R.menu.generate_qr_code_overflow, mPopupMenu.getMenu());
            mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.select_logo:
                            Intent intent = new Intent(GenerateCodeActivity.this, LocalAlbumActivity.class);
                            intent.putExtra(WeacConstants.REQUEST_LOCAL_ALBUM_TYPE, 2);
                            startActivityForResult(intent, REQUEST_LOCAL_ALBUM);
                            overridePendingTransition(R.anim.move_in_bottom, 0);
                            break;
                        case R.id.save_qr_code:
                            // 已经生成了二维码
                            if (mIsQRcodeGenerated) {
                                Drawable drawable = mQrCodeResultIv.getDrawable();
                                BitmapDrawable bd = (BitmapDrawable) drawable;
                                Bitmap bitmap = bd.getBitmap();

                                String filePath = WeacConstants.QRCODE_PATH + "/qrcode" +
                                        bitmap.hashCode() + ".jpg";
                                File file = MyUtil.getFileDirectory(GenerateCodeActivity.this, filePath);
                                String path = file.getAbsolutePath();
                                try {
                                    if (file.exists()) {
                                        //noinspection ResultOfMethodCallIgnored
                                        file.delete();
                                    }
                                    if (!file.createNewFile()) {
                                        ToastUtil.showShortToast(GenerateCodeActivity.this,
                                                getString(R.string.save_fail_retry));
                                        return true;
                                    }
                                    FileOutputStream fis = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fis);
                                    ToastUtil.showLongToast(GenerateCodeActivity.this,
                                            getString(R.string.picture_already_save_to, path));
                                } catch (Exception e) {
                                    ToastUtil.showShortToast(GenerateCodeActivity.this,
                                            getString(R.string.save_fail_retry));
                                    LogUtil.e(LOG_TAG, e.toString());
                                }
                            } else {
                                ToastUtil.showShortToast(GenerateCodeActivity.this,
                                        getString(R.string.generate_qrcode_please));
                            }
                            break;
                        case R.id.fore_color:
                            mPalette = 0;
                            new ColorChooserDialog.Builder(GenerateCodeActivity.this, R.string.fore_color)
                                    .titleSub(R.string.colors)
                                    .doneButton(R.string.sure)
                                    .cancelButton(R.string.cancel)
                                    .backButton(R.string.back)
                                    .customButton(R.string.custom_define)
                                    .presetsButton(R.string.back)
                                    .show();
                            break;
                        case R.id.back_color:
                            mPalette = 1;
                            new ColorChooserDialog.Builder(GenerateCodeActivity.this, R.string.back_color)
                                    .titleSub(R.string.colors)
                                    .doneButton(R.string.sure)
                                    .cancelButton(R.string.cancel)
                                    .backButton(R.string.back)
                                    .customButton(R.string.custom_define)
                                    .presetsButton(R.string.back)
                                    .show();
                            break;
                        case R.id.restore:
                            new AlertDialogWrapper.Builder(GenerateCodeActivity.this)
                                    .setTitle(R.string.restore)
                                    .setMessage(R.string.reset_logo_color)
                                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mLogoPath = null;
                                            mForeColor = 0xff000000;
                                            mBackColor = 0xffffffff;
                                            mLogoIv.setImageResource(R.drawable.ic_logo);
                                            if (mIsQRcodeGenerated) {
                                                generateQRcode();
                                            }

                                            SharedPreferences share = getSharedPreferences(
                                                    WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor edit = share.edit();
                                            edit.putString(WeacConstants.QRCODE_LOGO_PATH, null);
                                            edit.putInt(WeacConstants.FORE_COLOR, 0xff000000);
                                            edit.putInt(WeacConstants.BACK_COLOR, 0xffffffff);
                                            edit.apply();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                            break;
                    }
                    return true;
                }
            });
            mPopupMenu.show();
        } else {
            mPopupMenu.show();
        }
    }

    private void generateQRcode() {
        String contentString = mQrCodeEt.getText().toString();
        Bitmap logoBitmap = null;
        if (mLogoTogBtn.isChecked()) {
            if (mLogoPath != null) {
                // 自定义logo图标
                logoBitmap = BitmapFactory.decodeFile(mLogoPath);
            }
            if (logoBitmap == null) {
                // 默认logo为应用图标
                logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
            }
        }
        int size = MyUtil.dip2px(this, 200);
        //根据字符串生成二维码图片并显示在界面上，第2,3个参数为图片宽高
        Bitmap qrCodeBitmap = EncodingUtils.createQRCode(contentString, size, size, logoBitmap, mForeColor, mBackColor);
        mQrCodeResultIv.setImageBitmap(qrCodeBitmap);
        mIsQRcodeGenerated = true;
    }

    @Subscribe
    public void onLogoUpdateEvent(QRcodeLogoEvent event) {
        mLogoPath = event.getLogoPath();
        mLogoIv.setImageBitmap(BitmapFactory.decodeFile(mLogoPath));

        if (mIsQRcodeGenerated) {
            generateQRcode();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OttoAppConfig.getInstance().unregister(this);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, int selectedColor) {
        LogUtil.d(LOG_TAG, "onColorSelection: " + selectedColor);
        switch (mPalette) {
            // 前景色
            case 0:
                mForeColor = selectedColor;
                operateColor(selectedColor, WeacConstants.FORE_COLOR);
                break;
            // 背景色
            case 1:
                mBackColor = selectedColor;
                operateColor(selectedColor, WeacConstants.BACK_COLOR);
                break;
        }

    }

    private void operateColor(int selectedColor, String saveColorKey) {
        if (mIsQRcodeGenerated) {
            generateQRcode();
        }

        SharedPreferences share = getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = share.edit();
        edit.putInt(saveColorKey, selectedColor);
        edit.apply();
    }

}