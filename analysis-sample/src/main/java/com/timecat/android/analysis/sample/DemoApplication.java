package com.timecat.android.analysis.sample;

import android.app.Application;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        InitManager.initLazy(this);
    }
}
