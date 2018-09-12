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

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaku.weac.R;
import com.kaku.weac.util.LogUtil;
import com.kaku.weac.util.MyUtil;
import com.yingyongduoduo.ad.ADControl;


/**
 * 关于Activity
 *
 * @author 咖枯
 * @version 1.0 2016/2/15
 */
public class AboutActivity extends BaseActivity {
    private static final String LOG_TAG = "AboutActivity";
    private ADControl adControl;
    private LinearLayout adLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        assignViews();
    }

    private void assignViews() {
        adLinearLayout= (LinearLayout) findViewById(R.id.adLinearLayout);
        adControl=new ADControl();
        ViewGroup background = (ViewGroup) findViewById(R.id.background);
        MyUtil.setBackground(background, this);

        ImageView actionBack = (ImageView) findViewById(R.id.action_back);
        actionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置版本号
        setVersion();
        // 设置标语
        setSlogan();
    }

    private void setSlogan() {
        try {
            Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/weac_slogan.ttf");
            TextView SloganTv = (TextView) findViewById(R.id.weac_slogan_tv);
            SloganTv.setTypeface(fontFace);
        } catch (Exception e) {
            LogUtil.e(LOG_TAG, "Typeface.createFromAsset: " + e.toString());
        }
    }

    private void setVersion() {
        TextView versionTv = (TextView) findViewById(R.id.version_tv);
        versionTv.setText(getString(R.string.weac_version, MyUtil.getVersion(this)));
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
}
