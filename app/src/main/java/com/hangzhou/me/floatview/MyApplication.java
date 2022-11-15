package com.hangzhou.me.floatview;

import android.app.Application;

import com.hangzhou.me.afloat.DebugPanel;

/**
 * @Author: edison
 * @Email: kanglongba@gmail.com
 * @CreateDate: 2022/9/23 17:41
 * @Description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugPanel.init(this);
        DebugPanel.showNotify(getApplicationContext());
        UncaughtExceptionHandlerImpl.getInstance().init(this);
    }
}
