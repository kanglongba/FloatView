package com.applovin.me.floatview;

import android.app.Application;

import com.applovin.me.afloat.DebugPanel;

/**
 * @Author: edison qian
 * @Email: edison.qian@applovin.com
 * @CreateDate: 2022/9/23 17:41
 * @Description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugPanel.init(this);
        DebugPanel.showNotify(getApplicationContext());
    }
}
