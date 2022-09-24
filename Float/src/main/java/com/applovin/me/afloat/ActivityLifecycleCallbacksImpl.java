package com.applovin.me.afloat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/9/23 19:00
 * @Description:
 */
public class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {
    private final AtomicBoolean isFirstActivityCreated = new AtomicBoolean(true);

    public static ActivityLifecycleCallbacksImpl of() {
        return new ActivityLifecycleCallbacksImpl();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (isFirstActivityCreated.compareAndSet(true, false)) {
            DebugPanel.showBall(activity);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
