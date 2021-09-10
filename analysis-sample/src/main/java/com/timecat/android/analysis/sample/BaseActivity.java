package com.timecat.android.analysis.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
public abstract class BaseActivity extends Activity {

    Observable<?> ready;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBaseCreate(savedInstanceState);
        onAfterCreate(savedInstanceState);
    }

    private void onAfterCreate(@Nullable Bundle state) {
        if (InitManager.isHasInit()) {
            ready = null;
            afterCreate(state);
            return;
        }
        createReady(state);
    }

    private void createReady(Bundle state) {
        if (ready == null) {
            ready = InitManager
                    .ready()
                    .map(new Func1<Object, Object>() {
                        @Override
                        public Object call(Object result) {
                            afterCreate(state);
                            return result;
                        }
                    });
        }
    }

    protected abstract void afterCreate(@Nullable Bundle state);

    protected abstract void onBaseCreate(@Nullable Bundle savedInstanceState);

    @Override
    protected void onResume() {
        super.onResume();
        if (ready == null) {
            onObsResume();
        } else {
            Observable<?> obs = genObsResume();
            if (obs != null) {
                Observable.concat(ready, obs).subscribe();
            } else {
                ready.subscribe();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ready = null;
    }

    protected Observable<?> genObsResume() {
        return Observable.create(new OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                BaseActivity.this.onObsResume();
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    protected void onObsResume() {
    }
}
