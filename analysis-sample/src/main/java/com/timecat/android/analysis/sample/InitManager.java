package com.timecat.android.analysis.sample;


import android.content.Context;
import android.os.SystemClock;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
public class InitManager {

    private static Context sContext;

    private static Context sCache;
    private static boolean hasInit;

    public static void initLazy(Context ctx) {
        sCache = ctx;
    }

    public static boolean isHasInit() {
        return hasInit;
    }

    public static Context getContext() {
        return sContext;
    }

    public static Observable<?> ready() {
        return Observable.defer(
                new Func0<Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call() {
                        init(sCache);
                        return Observable.just(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void clear() {
        hasInit = false;
        sContext = null;
    }

    private static synchronized void init(Context ctx) {
        if (hasInit) {
            return;
        }
        SystemClock.sleep(2000);
        sContext = ctx;
        hasInit = true;
    }

}
