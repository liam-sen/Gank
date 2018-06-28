package com.johnny.gank;
/*
 * Copyright (C) 2016 Johnny Shieh Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Application;
import android.util.Log;

import com.johnny.gank.di.component.AppComponent;
import com.johnny.gank.di.component.DaggerAppComponent;
import com.johnny.gank.di.module.AppModule;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 */
public class GankApp extends Application{

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.initialize(
            new Settings()
                .isShowMethodLink(true)
                .isShowThreadInfo(true)
                .setMethodOffset(0)
                .setLogPriority(BuildConfig.DEBUG ? Log.VERBOSE : Log.ASSERT)
        );
        initInjector();
        LeakCanary.install(this);
    }

    private void initInjector() {
        mAppComponent = DaggerAppComponent.builder()
            .appModule(new AppModule(this))
            .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
